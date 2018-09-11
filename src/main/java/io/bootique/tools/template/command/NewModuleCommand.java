package io.bootique.tools.template.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewModuleCommand extends AbstractInteractiveCommand {

    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;


    public NewModuleCommand() {
        super(InteractiveCommandMetadata
                .interactiveBuilder(NewModuleCommand.class)
                .name("new-module")
                .description("Creates new module.")
                .addInteractiveOptions(
                        Stream.of(
                                InteractiveOptionMetadata
                                        .builder(NAME)
                                        .valueRequired("module_name")
                                        .interactive()
                                        .description("Module name.")
                                        .build(),
                                InteractiveOptionMetadata
                                        .builder(PACKAGE)
                                        .valueRequired("package_path")
                                        .interactive()
                                        .description("Dot separated module path executed.")
                                        .build())

                                .collect(Collectors.toList()
                                ))
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {

        PropertyService propertyService = propertyServiceProvider.get();
        if (!propertyService.hasProperty(NAME)) {
            String value = cli.optionString(NAME);
            System.out.println("Read property name: " + value);
            propertyService.setProperty(NAME, value);
            System.out.println("what is in properties: " + propertyService.getProperty(NAME));
        }

        if (!propertyService.hasProperty(PACKAGE)) {
            propertyService.setProperty(PACKAGE, cli.optionString(PACKAGE));

        }

        System.setProperty(COMMAND_TYPE, NewModuleCommand.class.getName());
        templateService.get().process();
        return CommandOutcome.succeeded();
    }
}

