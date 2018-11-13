package io.bootique.tools.template.module;

import java.util.List;
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
import io.bootique.type.TypeRef;

import static io.bootique.tools.template.services.DefaultPropertyService.*;

public class LiveTemplateModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {

        binder.bind(PropertyService.class).to(DefaultPropertyService.class).in(Singleton.class);
        binder.bind(ConfigurationFactory.class).toProvider(ExtendedJsonConfigurationFactoryPovider.class).in(Singleton.class);

        InteractiveOptionMetadata interactiveTpl = InteractiveOptionMetadata
                .builder("tpl")
                .valueOptional("template name")
                .defaultValue("maven-prj")
                .valueWithConfig("maven-prj", "classpath:templates/maven-prj.yml")
                .valueWithConfig("gradle-prj", "classpath:templates/gradle-prj.yml")
                .valueWithConfig("module", "classpath:templates/module.yml")
                .description("Project or module template or path to config file. " +
                        "Allowed templates: " +
                        "maven-prj(default), gradle-prj, module.")
                .build();

        InteractiveOptionMetadata artifactId = InteractiveOptionMetadata
                .builder(ARTIFACT)
                .valueRequired("artifact")
                .interactive()
                .description("Artifact id. Will be used as projects parent folder.")
                .build();
        InteractiveOptionMetadata groupOption = InteractiveOptionMetadata
                .builder(GROUP)
                .valueRequired("group")
                .interactive()
                .description("Group. Package structure.")
                .build();
        InteractiveOptionMetadata nameOption = InteractiveOptionMetadata
                .builder(NAME)
                .valueRequired("name")
                .interactive()
                .description("For module: module name. For project name of parent folder if its not mentioned, " +
                        "will be used artifact as parent folder.")
                .build();
        InteractiveOptionMetadata versionOption = InteractiveOptionMetadata
                .builder(VERSION)
                .valueRequired("version")
                .interactive()
                .description("Project version. e.g. 1.0-SNAPSHOT")
                .build();

        List<OptionMetadata> options = Stream.of(artifactId, groupOption, nameOption, versionOption, interactiveTpl).collect(Collectors.toList());
        BQCoreModule.extend(binder)
                .addCommand(new NewCommand(options));

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
