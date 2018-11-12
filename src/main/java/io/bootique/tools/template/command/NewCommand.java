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

public class NewCommand extends InteractiveCommandWithMetadata {
    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;

    public NewCommand(InteractiveCommandMetadata.Builder metadataBuilder) {
        super(metadataBuilder);
    }

    public NewCommand(InteractiveCommandMetadata metadata) {
        super(metadata);
    }

    public NewCommand(List<OptionMetadata> options) {
        super(InteractiveCommandMetadata
                .interactiveBuilder(NewCommand.class)
                .name("new")
                .description("Creates new module or project.")
                .addInteractiveOptions(options)
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {

        checkOption(ARTIFACT, null, cli);
        checkOption(GROUP,null,  cli);
        checkOption(NAME, propertyServiceProvider.get().getProperty(ARTIFACT), cli);
        checkOption(VERSION, "1.0-SNAPSHOT", cli);

        templateService.get().process();
        return CommandOutcome.succeeded();
    }


    private void checkOption(String name, String alternativeOption, Cli cli) {
        PropertyService propertyService = propertyServiceProvider.get();
        if (!propertyService.hasProperty(name)) {
            String value = cli.optionString(name);
            propertyService.setProperty(name, value != null && !value.isEmpty()  ?
                    value : alternativeOption);
        }
    }

}
