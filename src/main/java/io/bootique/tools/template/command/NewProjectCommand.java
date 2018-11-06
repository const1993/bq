package io.bootique.tools.template.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;

import java.util.List;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;
import static io.bootique.tools.template.services.DefaultPropertyService.PACKAGE;

public class NewProjectCommand extends InteractiveCommandWithMetadata {

    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;

    public NewProjectCommand(InteractiveCommandMetadata.Builder metadataBuilder) {
        super(metadataBuilder);
    }

    public NewProjectCommand(InteractiveCommandMetadata metadata) {
        super(metadata);
    }

    public NewProjectCommand(List<OptionMetadata> options) {
        super(InteractiveCommandMetadata
                .interactiveBuilder(NewProjectCommand.class)
                .name("new-project")
                .description("Creates new module.")
                .addInteractiveOptions(options)
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {
        PropertyService propertyService = propertyServiceProvider.get();

        if (!propertyService.hasProperty(PACKAGE)) {
            propertyService.setProperty(PACKAGE, cli.optionString(PACKAGE));
        }

        templateService.get().process();
        return CommandOutcome.succeeded();
    }

}
