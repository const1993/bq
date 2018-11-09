package io.bootique.tools.template.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.config.ConfigurationFactory;
import io.bootique.config.ConfigurationSource;
import io.bootique.config.OptionRefWithConfig;
import io.bootique.config.jackson.InPlaceLeftHandMerger;
import io.bootique.config.jackson.InPlaceMapOverrider;
import io.bootique.config.jackson.InPlaceResourceOverrider;
import io.bootique.config.jackson.JsonNodeConfigurationBuilder;
import io.bootique.config.jackson.JsonNodeConfigurationFactory;
import io.bootique.config.jackson.JsonNodeJsonParser;
import io.bootique.config.jackson.JsonNodeYamlParser;
import io.bootique.config.jackson.MultiFormatJsonNodeParser;
import io.bootique.env.Environment;
import io.bootique.jackson.JacksonService;
import io.bootique.log.BootLogger;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.resource.ResourceFactory;
import io.bootique.tools.template.services.options.TemplateOptionMetadata;
import joptsimple.OptionSpec;

import java.io.InputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Collections.singletonMap;
import static java.util.function.Function.identity;

public class ExtendedJsonConfigurationFactoryPovider implements Provider<ConfigurationFactory> {
    private ConfigurationSource configurationSource;
    private Environment environment;
    private JacksonService jacksonService;
    private BootLogger bootLogger;
    private Set<OptionMetadata> optionMetadata;
    private Set<OptionRefWithConfig> optionDecorators;
    private Cli cli;

    @Inject
    public ExtendedJsonConfigurationFactoryPovider(
            ConfigurationSource configurationSource,
            Environment environment,
            JacksonService jacksonService,
            BootLogger bootLogger,
            Set<OptionMetadata> optionMetadata,
            Set<OptionRefWithConfig> optionDecorators,
            Cli cli) {

        this.configurationSource = configurationSource;
        this.environment = environment;
        this.jacksonService = jacksonService;
        this.bootLogger = bootLogger;
        this.optionMetadata = optionMetadata;
        this.optionDecorators = optionDecorators;
        this.cli = cli;
    }

    protected JsonNode loadConfiguration(Map<String, String> properties) {

        // hopefully sharing the mapper between parsers is safe... Does it
        // change the state during parse?
        ObjectMapper textToJsonMapper = jacksonService.newObjectMapper();
        Map<MultiFormatJsonNodeParser.ParserType, Function<InputStream, Optional<JsonNode>>> parsers = new EnumMap<>(MultiFormatJsonNodeParser.ParserType.class);
        parsers.put(MultiFormatJsonNodeParser.ParserType.YAML, new JsonNodeYamlParser(textToJsonMapper));
        parsers.put(MultiFormatJsonNodeParser.ParserType.JSON, new JsonNodeJsonParser(textToJsonMapper));

        Function<URL, Optional<JsonNode>> parser = new MultiFormatJsonNodeParser(parsers, bootLogger);

        BinaryOperator<JsonNode> singleConfigMerger = new InPlaceLeftHandMerger(bootLogger);

        Function<JsonNode, JsonNode> overrider = andCliOptionOverrider(identity(), parser, singleConfigMerger);

        if (!properties.isEmpty()) {
            overrider = overrider.andThen(new InPlaceMapOverrider(properties));
        }

        return JsonNodeConfigurationBuilder.builder()
                .parser(parser)
                .merger(singleConfigMerger)
                .resources(configurationSource)
                .overrider(overrider)
                .build();
    }

    private Function<JsonNode, JsonNode> andCliOptionOverrider(
            Function<JsonNode, JsonNode> overrider,
            Function<URL, Optional<JsonNode>> parser,
            BinaryOperator<JsonNode> singleConfigMerger) {

        if (optionMetadata.isEmpty()) {
            return overrider;
        }

        List<OptionSpec<?>> detectedOptions = cli.detectedOptions();
        if (detectedOptions.isEmpty()) {
            return overrider;
        }

        for (OptionSpec<?> cliOpt : detectedOptions) {

            OptionMetadata omd = findMetadata(cliOpt);

            if (omd == null) {
                continue;
            }

            // config decorators are loaded first, and then can be overridden from options...
            for (OptionRefWithConfig decorator : optionDecorators) {
                if (decorator.getOptionName().equals(omd.getName())) {
                    overrider = overrider.andThen(new InPlaceResourceOverrider(decorator.getConfigResource().getUrl(),
                            parser, singleConfigMerger));
                }
            }

            String cliValue = cli.optionString(omd.getName());
            if (cliValue == null) {
                cliValue = omd.getDefaultValue();
            }

            String finalCliValue = cliValue;
            String configPath = omd.getConfigPath();

            if (configPath == null) {

                if (omd instanceof TemplateOptionMetadata) {
                    configPath = ((TemplateOptionMetadata) omd).getConfigPath(finalCliValue);
                }

                if (configPath != null) {
                    overrider = overrider.andThen(new InPlaceResourceOverrider(new ResourceFactory(configPath).getUrl(),
                            parser, singleConfigMerger));
                }
            }

            if (omd.getConfigPath() != null) {

                finalCliValue = cliValue;
                overrider = overrider.andThen(new InPlaceMapOverrider(
                        singletonMap(configPath, finalCliValue)
                ));
            }
        }

        return overrider;
    }

    private OptionMetadata findMetadata(OptionSpec<?> option) {

        List<String> optionNames = option.options();

        // TODO: allow lookup of option metadata by name to avoid linear scans...
        // Though we are dealing with small collection, so shouldn't be too horrible.

        for (OptionMetadata omd : optionMetadata) {
            if (optionNames.contains(omd.getName())) {
                return omd;
            }
        }

        // this was likely a command, not an option.
        return null;
    }

    @Override
    public ConfigurationFactory get() {

        Map<String, String> properties = environment.frameworkProperties();

        JsonNode rootNode = loadConfiguration(properties);

        ObjectMapper jsonToObjectMapper = jacksonService.newObjectMapper();
        return new JsonNodeConfigurationFactory(rootNode, jsonToObjectMapper);
    }
}
