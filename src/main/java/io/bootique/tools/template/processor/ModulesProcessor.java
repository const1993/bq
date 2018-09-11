package io.bootique.tools.template.processor;

import io.bootique.tools.template.Template;
import io.bootique.tools.template.command.AbstractInteractiveCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModulesProcessor extends JavaPackageProcessor {

    public static final String EXAMPLE_MODULE = "ExampleModule";

    @Override
    public Template process(Template template) {
        return template.withContent(parseContent(template)).withPath(outputPath(template));
    }

    @Override
    Path outputPath(Template template) {
        Path path = super.outputPath(template);
        String pathString = path.toString().replace(EXAMPLE_MODULE, propertyService.getProperty(AbstractInteractiveCommand.NAME));
        return Paths.get(pathString);
    }

    String parseContent(Template template) {

        String content = template.getContent();
        content = replaceImportDeclaration(content);
        content = replacePackageDeclaration(content);
        return content.replaceAll("class " + EXAMPLE_MODULE, propertyService.getProperty(AbstractInteractiveCommand.NAME));
    }

}
