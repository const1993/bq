package io.bootique.tools.template.command;

import io.bootique.command.Command;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.names.ClassToName;
import io.bootique.tools.template.services.options.InteractiveOptionMetadata;

import java.util.ArrayList;
import java.util.Collection;

public class InteractiveCommandMetadata extends CommandMetadata {

    private String name;
    private String description;
    private String shortName;
    private boolean hidden;
    private Collection<OptionMetadata> options;


    public InteractiveCommandMetadata() {
        this.options = new ArrayList<>();
    }

    public static Builder interactiveBuilder(Class<? extends Command> commandType) {
        return new Builder().commandType(commandType);
    }

    public static Builder interactiveBuilder(String commandName) {
        return new Builder().name(commandName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns an option representation of this command, that may be used in help generation or exposing the command
     * in a CLI parser.
     *
     * @return option representation of this command.
     * @since 0.21
     */
    public OptionMetadata asOption() {
        // TODO: cache the value?
        // using getters instead of vars ; some getters have logic
        return OptionMetadata.builder(getName()).shortName(getShortName()).description(getDescription()).build();
    }

    public Collection<OptionMetadata> getOptions() {
        return options;
    }

    /**
     * Returns the short name
     *
     * @return command short name.
     * @since 0.21
     */
    public String getShortName() {
        return (shortName != null) ? shortName : name.substring(0, 1);
    }

    /**
     * Returns whether the command should be hidden by default. Ultimately {@link io.bootique.command.CommandManager}
     * defines whether any given command is public or hidden. This property defines the default policy for the given
     * command.
     *
     * @return whether the command should be hidden by default.
     * @since 0.25
     */
    public boolean isHidden() {
        return hidden;
    }

    public static class Builder {

        private static ClassToName NAME_BUILDER = ClassToName
                .builder()
                .convertToLowerCase()
                .partsSeparator("-")
                .stripSuffix("Command")
                .build();

        private InteractiveCommandMetadata command;

        private Builder() {
            this.command = new InteractiveCommandMetadata();
        }

        public InteractiveCommandMetadata build() {
            validateName(command.name);
            return command;
        }

        public InteractiveCommandMetadata.Builder commandType(Class<? extends Command> commandType) {
            command.name = NAME_BUILDER.toName(commandType);
            return this;
        }

        public InteractiveCommandMetadata.Builder name(String name) {
            command.name = validateName(name);
            return this;
        }

        public InteractiveCommandMetadata.Builder shortName(char shortName) {
            command.shortName = String.valueOf(shortName);
            return this;
        }

        public InteractiveCommandMetadata.Builder description(String description) {
            this.command.description = description;
            return this;
        }


        public Builder addInteractiveOptions(Collection<OptionMetadata> options) {
            this.command.options.addAll(options);
            return this;
        }

        /**
         * @return this interactiveBuilder instance.
         * @since 0.25
         */
        public InteractiveCommandMetadata.Builder hidden() {
            this.command.hidden = true;
            return this;
        }

        private String validateName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Null 'name'");
            }

            if (name.length() == 0) {
                throw new IllegalArgumentException("Empty 'name'");
            }

            return name;
        }
    }
}
