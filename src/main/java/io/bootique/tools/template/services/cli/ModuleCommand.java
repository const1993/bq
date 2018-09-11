package io.bootique.tools.template.services.cli;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.template.services.TemplateService;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleCommand extends InteractiveCommandWithMetadata {


    public static final String NAME = "name";
    public static final String PACKAGE = "package";
    @Inject
    Provider<TemplateService> templateService;

    public ModuleCommand(CommandMetadata.Builder metadataBuilder) {
        super(InteractiveCommandMetadata
                .interactiveBuilder(ModuleCommand.class)
                .name("create-module")
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
        String name = cli.optionString(NAME);
        String pack = cli.optionString(PACKAGE);
        templateService.get().process(Map.of(NAME, name, PACKAGE, pack));
        return CommandOutcome.succeeded();
    }
}

