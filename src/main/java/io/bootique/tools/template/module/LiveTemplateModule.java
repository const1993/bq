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
import io.bootique.command.Command;
import io.bootique.command.CommandManager;
import io.bootique.config.ConfigurationFactory;
import io.bootique.meta.application.ApplicationMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.command.InteractiveCommandMetadata;
import io.bootique.tools.template.command.NewProjectCommand;
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
import io.bootique.tools.template.command.NewModuleCommand;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;
import io.bootique.type.TypeRef;

public class LiveTemplateModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {

        binder.bind(PropertyService.class).to(DefaultPropertyService.class).in(Singleton.class);

        OptionMetadata helloTemplateOption = OptionMetadata.builder("hello-tpl").description("Load template by option").build();
        OptionMetadata gradleTemplateOption = OptionMetadata.builder("gradle-hello-tpl").description("Load template by option").build();
        OptionMetadata moduleOption = OptionMetadata.builder("module-template").description("Load module template").build();

        InteractiveOptionMetadata nameOption = InteractiveOptionMetadata
                .builder("name")
                .valueRequired("module_name")
                .interactive()
                .description("Module name.")
                .build();
        InteractiveOptionMetadata package_path = InteractiveOptionMetadata
                .builder("java.package")
                .valueRequired("package_path")
                .interactive()
                .description("Dot separated module path executed.")
                .build();
        BQCoreModule.extend(binder)
                .addOption(gradleTemplateOption).addConfigOnOption(gradleTemplateOption.getName(), "classpath:templates/gradle-hello-tpl.yml")
                .addOption(helloTemplateOption).addConfigOnOption(helloTemplateOption.getName(), "classpath:templates/hello-tpl.yml")
                .addOption(moduleOption).addConfigOnOption(moduleOption.getName(), "classpath:templates/module-tpl.yml")
                .addCommand(new NewProjectCommand(Stream.of(package_path).collect(Collectors.toList())))
                .addCommand(NewModuleCommand.class);

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
        props.forEach(propertyService::setProperty);
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
