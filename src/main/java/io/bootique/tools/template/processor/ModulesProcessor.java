package io.bootique.tools.template.processor;

import io.bootique.tools.template.Template;

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
        String name = template.getName();
        String pathString = path.toString().replace(EXAMPLE_MODULE, name != null ? name : "");
        return Paths.get(pathString);
    }

    String parseContent(Template template) {

        String name = template.getName();

        String content = template.getContent();
        content = replaceImportDeclaration(content);
        content = replacePackageDeclaration(content);
        return content.replaceAll("class " + EXAMPLE_MODULE, name != null ? name : "");
    }

}
