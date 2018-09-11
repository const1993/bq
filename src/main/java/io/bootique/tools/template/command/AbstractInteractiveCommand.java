package io.bootique.tools.template.command;

public abstract class AbstractInteractiveCommand extends InteractiveCommandWithMetadata {

    public static final String NAME = "name";
    public static final String PACKAGE = "java.package";
    public static final String COMMAND_TYPE = "command-type";

    public AbstractInteractiveCommand(InteractiveCommandMetadata.Builder metadataBuilder) {
        super(metadataBuilder);
    }

    public AbstractInteractiveCommand(InteractiveCommandMetadata metadata) {
        super(metadata);
    }

}
