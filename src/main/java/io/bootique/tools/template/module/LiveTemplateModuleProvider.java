package io.bootique.tools.template.module;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class LiveTemplateModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new LiveTemplateModule();
    }

}
