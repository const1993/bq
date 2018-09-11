package io.bootique.tools.template.command;

import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import io.bootique.meta.application.CommandMetadata;

public abstract class InteractiveCommandWithMetadata implements Command {
    private InteractiveCommandMetadata metadata;

    public InteractiveCommandWithMetadata(InteractiveCommandMetadata.Builder metadataBuilder) {
        this(metadataBuilder.build());
    }

    public InteractiveCommandWithMetadata(InteractiveCommandMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public CommandMetadata getMetadata() {
        return metadata;
    }

    @Override
    public abstract CommandOutcome run(Cli cli);

}
