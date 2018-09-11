package io.bootique.tools.template.services;

import io.bootique.tools.template.source.SourceSet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ModuleTemplateService extends ZipTemplateService {

    private static final String TEMPLATES_MODULE_PATH = "templates/module-tpl.zip";

    public ModuleTemplateService(Path outputRoot, List<SourceSet> sourceSets) {
        super(Paths.get(TEMPLATES_MODULE_PATH), outputRoot, sourceSets);
    }

}
