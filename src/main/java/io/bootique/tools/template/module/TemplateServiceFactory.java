package io.bootique.tools.template.module;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.tools.template.DefaultTemplateService;
import io.bootique.tools.template.TemplateService;
import io.bootique.tools.template.ZipTemplateService;
import io.bootique.tools.template.processor.TemplateProcessor;

@BQConfig("Template configuration")
public class TemplateServiceFactory {

    private File templateRoot;
    private File output;
    private List<SourceSetFactory> sourceSets;

    TemplateService createTemplateService(Map<String, TemplateProcessor> processorMap) {

        if (templateRoot != null && templateRoot.toString().endsWith(".zip")) {
            return new ZipTemplateService(templateRoot.toPath(),
                    output != null ? output.toPath() : null,
                    sourceSets != null ? sourceSets.stream()
                            .map(factory -> factory.createSourceSet(processorMap))
                            .collect(Collectors.toList()) : Collections.emptyList());
        }

        return new DefaultTemplateService(
                templateRoot != null ? templateRoot.toPath() : null,
                output != null ? output.toPath() : null,
                sourceSets != null ? sourceSets.stream()
                        .map(factory -> factory.createSourceSet(processorMap))
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    @BQConfigProperty("Template root directory")
    public void setTemplateRoot(File templateRoot) {
        this.templateRoot = templateRoot;
    }

    @BQConfigProperty("Output directory")
    public void setOutput(File output) {
        this.output = output;
    }

    @BQConfigProperty("Template source sets")
    public void setSourceSets(List<SourceSetFactory> sourceSets) {
        this.sourceSets = sourceSets;
    }

}
