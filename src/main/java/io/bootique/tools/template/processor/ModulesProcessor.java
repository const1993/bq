package io.bootique.tools.template.processor;

import io.bootique.tools.template.Template;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;

public class ModulesProcessor extends JavaPackageProcessor {

    protected static final String EXAMPLE_MODULE = "ExampleModule";
    protected static final String EXAMPLE_MODULE_PROVIDER = "ExampleModuleProvider";

    @Override
    public Template process(Template template) {
        return template.withContent(parseContent(template)).withPath(outputPath(template));
    }

    @Override
    Path outputPath(Template template) {
        Path path = super.outputPath(template);
        String pathString = path.toString();

        pathString = path.endsWith(EXAMPLE_MODULE_PROVIDER + ".java") ?
            pathString.replace(EXAMPLE_MODULE_PROVIDER, propertyService.getProperty(NAME) + "Provider"):
                path.endsWith(EXAMPLE_MODULE + ".java") ?
                        pathString.replace(EXAMPLE_MODULE, propertyService.getProperty(NAME)): pathString;

        return Paths.get(pathString);
    }

    String parseContent(Template template) {

        String content = template.getContent();
        content = replaceImportDeclaration(content);
        content = replacePackageDeclaration(content);
        content = content.replaceAll("class " + EXAMPLE_MODULE_PROVIDER, "class " + propertyService.getProperty(NAME) + "Provider");
        content = content.replaceAll(EXAMPLE_MODULE, propertyService.getProperty(NAME));

        return content.replaceAll("new " + EXAMPLE_MODULE, "new " + propertyService.getProperty(NAME));
    }

}
