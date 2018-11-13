package io.bootique.tools.template.processor;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;
import io.bootique.tools.template.services.DefaultPropertyService;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bootique.tools.template.services.DefaultPropertyService.ARTIFACT;
import static io.bootique.tools.template.services.DefaultPropertyService.GROUP;
import static io.bootique.tools.template.services.DefaultPropertyService.VERSION;

public class GradleProcessor implements TemplateProcessor {

    private static final String EXAMPLE_PROJECT = "'example-project'";
    private static final String EXAMPLE_GROUP = "'example.group'";
    private static final String GRADLE_PROJECT_VERSION = "'1.0-SNAPSHOT'";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template.withContent(processContent(template.getContent())).withPath(outputPath(template));
    }

    String processContent(String content) {

        return content.replaceAll("rootProject.name = " + EXAMPLE_PROJECT, "rootProject.name = '" + propertyService.getProperty(ARTIFACT) + "'")
                .replaceAll("group = " + EXAMPLE_GROUP, "group = '" + propertyService.getProperty(GROUP) + "'")
                .replaceAll("version = " + GRADLE_PROJECT_VERSION, "version = '" + propertyService.getProperty(VERSION) + "'");
    }

    Path outputPath(Template template) {
        Path input = template.getPath();
        String pathStr = input.toString();
        return Paths.get(pathStr);
    }

}
