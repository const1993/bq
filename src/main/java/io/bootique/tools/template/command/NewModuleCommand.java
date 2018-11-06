package io.bootique.tools.template.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;

import java.util.List;

import static io.bootique.tools.template.services.DefaultPropertyService.*;

public class NewModuleCommand extends InteractiveCommandWithMetadata {

    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;

    public NewModuleCommand(InteractiveCommandMetadata.Builder metadataBuilder) {
        super(metadataBuilder);
    }

    public NewModuleCommand(InteractiveCommandMetadata metadata) {
        super(metadata);
    }

    public NewModuleCommand(List<OptionMetadata> options) {
        super(InteractiveCommandMetadata
                .interactiveBuilder(NewModuleCommand.class)
                .name("new-module")
                .description("Creates new module.")
                .addInteractiveOptions(options)
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {

        PropertyService propertyService = propertyServiceProvider.get();
        String value = propertyService.getProperty(NAME);

        if (value == null) {
            value = cli.optionString(NAME);
        }

        propertyService.setProperty(NAME, value);
        propertyService.setProperty(MODULE_NAME, !value.contains("Module") ? value + "Module": value);
        propertyService.setProperty(MODULE_PROVIDER_NAME, !value.contains("Module") ? value + "ModuleProvider": value + "Provider");

        if (!propertyService.hasProperty(PACKAGE)) {
            propertyService.setProperty(PACKAGE, cli.optionString(PACKAGE));

        }

        templateService.get().process();
        return CommandOutcome.succeeded();
    }
}

