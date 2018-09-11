package io.bootique.tools.template.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewProjectCommand extends AbstractInteractiveCommand {

    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;

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

        System.setProperty(COMMAND_TYPE, NewProjectCommand.class.getName());
        templateService.get().process();
        return CommandOutcome.succeeded();
    }

}
