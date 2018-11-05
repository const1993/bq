package io.bootique.tools.template.processor;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

public class GradleProcessor implements TemplateProcessor {

    private static final String EXAMPLE_PROJECT = "'example-project'";
    private static final String EXAMPLE_GROUP = "'example.group'";
    private static final String GRADLE_PROJECT_VERSION = "'1.0-SNAPSHOT'";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template.withContent(processContent(template.getContent()));
    }

    String processContent(String content) {

        return content.replaceAll("rootProject.name = " + EXAMPLE_PROJECT, "rootProject.name = '" + propertyService.getProperty("project.artifactId") + "'")
                .replaceAll("group = " + EXAMPLE_GROUP, "group = '" + propertyService.getProperty("project.groupId") + "'")
                .replaceAll("version = " + GRADLE_PROJECT_VERSION, "version = '" + propertyService.getProperty("project.version") + "'");
    }

}
