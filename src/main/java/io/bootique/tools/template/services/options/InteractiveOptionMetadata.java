package io.bootique.tools.template.services.options;

import io.bootique.meta.application.OptionMetadata;
import io.bootique.meta.application.OptionValueCardinality;

import java.util.HashMap;
import java.util.Map;

public class InteractiveOptionMetadata extends OptionMetadata {

    private boolean interactive;

    private String name;
    private String description;
    private String shortName;
    private OptionValueCardinality valueCardinality;
    private String valueName;

    private String configPath;
    private String defaultValue;
    private Map<String, String> templatePaths;

    public static Builder builder(String name) {
        return new Builder().name(name);
    }

    public static Builder builder(String name, String description) {
        return new Builder().name(name).description(description);
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
     * @return option short name.
     * @since 0.21
     */
    public String getShortName() {
        return (shortName != null) ? shortName : name.substring(0, 1);
    }

    public OptionValueCardinality getValueCardinality() {
        return valueCardinality;
    }

    public String getValueName() {
        return valueName;
    }

    /**
     * Returns an optional configuration path associated with this option.
     *
     * @return null or a dot-separated "path" that navigates configuration tree to the property associated with this
     * option. E.g. "jdbc.myds.password".
     * @since 0.24
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Returns an optional configuration path associated with this option.
     *
     * @return null or a dot-separated "path" that navigates configuration tree to the property associated with this
     * option. E.g. "jdbc.myds.password".
     * @since 0.24
     */
    public String getConfigPath(String value) {
        return templatePaths.getOrDefault(value, value);
    }

    /**
     * Returns the default value for this option. I.e. the value that will be used if the option is provided on
     * command line without an explicit value.
     *
     * @return the default value for this option.
     * @since 0.24
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isInteractive() {
        return interactive;
    }


    public static class Builder extends OptionMetadata.Builder{

        private InteractiveOptionMetadata interactiveOption;
        private Map<String, String> templatePaths;

        protected Builder() {
            super();
            this.interactiveOption = new InteractiveOptionMetadata();
            this.templatePaths = new HashMap<>();
        }

        public InteractiveOptionMetadata.Builder interactive() {
            this.interactiveOption.interactive = true;
            return this;
        }

        public Builder name(String name) {
            super.name(name);
            return this;
        }

        public Builder shortName(String shortName) {
            super.shortName(shortName);
            return this;
        }

        public Builder shortName(char shortName) {
            super.shortName(shortName);
            return this;
        }

        public Builder description(String description) {
            super.description(description);
            return this;
        }

        public Builder valueRequired() {
            return valueRequired("");
        }

        public Builder valueRequired(String valueName) {
            super.valueRequired(valueName);
            return this;
        }

        public Builder valueOptional() {
            return valueOptional("");
        }

        public Builder valueOptional(String valueName) {
            super.valueOptional(valueName);
            return this;
        }

        /**
         * Sets the configuration property path that should be associated to this option value.
         *
         * @param configPath a dot-separated "path" that navigates configuration tree to the desired property. E.g.
         *                   "jdbc.myds.password".
         * @return this interactiveBuilder instance
         * @since 0.24
         */
        public Builder configPath(String configPath) {
            super.configPath(configPath);
            return this;
        }

        /**
         * Sets the default value for this option.
         *
         * @param defaultValue a default value for the option.
         * @return this interactiveBuilder instance
         * @since 0.24
         */
        public Builder defaultValue(String defaultValue) {
            super.defaultValue(defaultValue);
            return this;
        }

        public Builder valueWithConfig(String value, String configPath) {
            templatePaths.put(value, configPath);
            return this;
        }

        public InteractiveOptionMetadata build() {
            OptionMetadata optionMetadata = super.build();

            InteractiveOptionMetadata metadata = new InteractiveOptionMetadata();
            metadata.interactive = interactiveOption.interactive;
            metadata.name = optionMetadata.getName();
            metadata.description = optionMetadata.getDescription();
            metadata.shortName = optionMetadata.getShortName();
            metadata.configPath = optionMetadata.getConfigPath();
            metadata.defaultValue = optionMetadata.getDefaultValue();
            metadata.valueCardinality = optionMetadata.getValueCardinality();
            metadata.valueName = optionMetadata.getValueName();
            metadata.templatePaths = templatePaths;

            return metadata;
        }


    }

}
