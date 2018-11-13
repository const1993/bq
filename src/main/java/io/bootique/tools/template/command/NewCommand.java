package io.bootique.tools.template.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;

import java.nio.file.Paths;
import java.util.List;

import static io.bootique.tools.template.services.DefaultPropertyService.*;

public class NewCommand extends InteractiveCommandWithMetadata {
    @Inject
    Provider<TemplateService> templateService;

    @Inject
    Provider<PropertyService> propertyServiceProvider;
    private boolean isModule;

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
        PropertyService propertyService = propertyServiceProvider.get();
        String artifact = propertyService.getProperty(ARTIFACT);
        isModule = artifact != null && artifact.isEmpty();

        checkOption(GROUP,"",  cli);
        checkOption(ARTIFACT, "", cli);

        String parent = "";
        if (isModule) {
            checkOption(NAME, "StubModule", cli);
            String name = propertyService.getProperty(NAME);
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            propertyService.setProperty(NAME, name);
        } else {
            checkOption(NAME, "App" , cli);
            String name = propertyService.getProperty(NAME);
            if (!name.equals("App")) {
                parent = name;
                propertyService.setProperty(NAME, "App");
            }
        }

        checkOption(VERSION, "1.0-SNAPSHOT", cli);

        templateService.get().process(Paths.get(parent));
        return CommandOutcome.succeeded();
    }


    private void checkOption(String name, String alternativeOption, Cli cli) {
        PropertyService propertyService = propertyServiceProvider.get();
        if (!propertyService.hasProperty(name)) {
            String value = cli.optionString(name);
            String option = value != null && !value.isEmpty() ?
                    value : alternativeOption;
            propertyService.setProperty(name, option);
        }
    }

}
