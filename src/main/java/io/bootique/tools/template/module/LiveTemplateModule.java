package io.bootique.tools.template.module;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.cli.CliFactory;
import io.bootique.command.CommandManager;
import io.bootique.config.ConfigurationFactory;
import io.bootique.meta.application.ApplicationMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.command.NewCommand;
import io.bootique.tools.template.provider.ExtendedJsonConfigurationFactoryPovider;
import io.bootique.tools.template.services.DefaultPropertyService;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;
import io.bootique.tools.template.processor.GradleProcessor;
import io.bootique.tools.template.processor.JavaPackageProcessor;
import io.bootique.tools.template.processor.MavenProcessor;
import io.bootique.tools.template.processor.ModuleProviderProcessor;
import io.bootique.tools.template.processor.ModulesProcessor;
import io.bootique.tools.template.processor.TemplateProcessor;
import io.bootique.tools.template.services.cli.InteractiveCliFactory;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;
import io.bootique.tools.template.services.options.TemplateOptionMetadata;
import io.bootique.type.TypeRef;

import static io.bootique.tools.template.services.DefaultPropertyService.*;

public class LiveTemplateModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {

        binder.bind(PropertyService.class).to(DefaultPropertyService.class).in(Singleton.class);
        binder.bind(ConfigurationFactory.class).toProvider(ExtendedJsonConfigurationFactoryPovider.class).in(Singleton.class);

        OptionMetadata templateOption = TemplateOptionMetadata
                .builder("tpl")
                .valueRequired("Template id")
                .defaultValue("maven-prj", "classpath:templates/hello-tpl.yml")
                .valueRequired("gradle-prj", "classpath:templates/gradle-hello-tpl.yml")
                .valueRequired("module", "classpath:templates/module-tpl.yml")
                .description("Project or module template or path to config file.")
                .valueOptional()
                .build();
        InteractiveOptionMetadata artifactId = InteractiveOptionMetadata
                .builder(ARTIFACT)
                .valueRequired("artifactId")
                .interactive()
                .description("Artifact id.")
                .build();
        InteractiveOptionMetadata groupOption = InteractiveOptionMetadata
                .builder(GROUP)
                .valueRequired("group")
                .interactive()
                .description("Group. Package structure.")
                .build();
        InteractiveOptionMetadata nameOption = InteractiveOptionMetadata
                .builder(NAME)
                .valueRequired("module_name")
                .interactive()
                .description("Module name.")
                .build();
        InteractiveOptionMetadata versionOption = InteractiveOptionMetadata
                .builder(VERSION)
                .valueRequired("version")
                .interactive()
                .description("Project version. e.g. 1.0-SNAPSHOT")
                .build();

        BQCoreModule.extend(binder)
                .addOption(templateOption)
                .addCommand(new NewCommand(Stream.of(artifactId, groupOption, nameOption, versionOption).collect(Collectors.toList())));

        contributeProcessor(binder, "module", ModulesProcessor.class);
        contributeProcessor(binder, "moduleProvider", ModuleProviderProcessor.class);
        contributeProcessor(binder, "gradle", GradleProcessor.class);
        contributeProcessor(binder, "maven", MavenProcessor.class);
        contributeProcessor(binder, "javaPackage", JavaPackageProcessor.class);
    }

    public static void contributeProcessor(Binder binder, String name, TemplateProcessor processor) {
        MapBinder.newMapBinder(binder, String.class, TemplateProcessor.class)
                .addBinding(name).toInstance(processor);
    }

    public static void contributeProcessor(Binder binder, String name, Class<? extends TemplateProcessor> processor) {
        MapBinder.newMapBinder(binder, String.class, TemplateProcessor.class)
                .addBinding(name).to(processor);
    }

    @Singleton
    @Provides
    public TemplateService createTemplateService(ConfigurationFactory configurationFactory, Map<String, TemplateProcessor> processorMap) {
        return configurationFactory
                .config(TemplateServiceFactory.class, configPrefix)
                .createTemplateService(processorMap);
    }

    @Singleton
    @Provides
    public PropertyService createPropertyService(ConfigurationFactory configurationFactory) {
        Map<String, String> props = configurationFactory.config(new TypeRef<>() {}, "properties");
        PropertyService propertyService = new DefaultPropertyService();
        if (props != null) {
            props.forEach(propertyService::setProperty);
        }

        return propertyService;
    }

    @Provides
    @Singleton
    CliFactory provideCliFactory(
            Provider<CommandManager> commandManagerProvider,
            ApplicationMetadata applicationMetadata) {
        return new InteractiveCliFactory(commandManagerProvider, applicationMetadata);
    }

    @Override
    protected String defaultConfigPrefix() {
        return "templates";
    }
}
