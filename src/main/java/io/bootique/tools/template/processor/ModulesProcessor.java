package io.bootique.tools.template.processor;

import io.bootique.tools.template.Template;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;

public class ModulesProcessor extends JavaPackageProcessor {

    protected static final String EXAMPLE_MODULE = "ExampleModule";

    @Override
    public Template process(Template template) {
        return template.withContent(parseContent(template)).withPath(outputPath(template));
    }

    @Override
    Path outputPath(Template template) {
        Path path = super.outputPath(template);
        String property = propertyService.getProperty(NAME);
        String pathString = path.toString().replace(EXAMPLE_MODULE, property);
        return Paths.get(pathString);
    }

    String parseContent(Template template) {

        String content = template.getContent();
        content = replaceImportDeclaration(content);
        content = replacePackageDeclaration(content);
        return content.replaceAll("class " + EXAMPLE_MODULE, propertyService.getProperty(NAME));
    }

}
