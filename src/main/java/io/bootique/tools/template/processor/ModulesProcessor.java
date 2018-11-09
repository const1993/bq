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
        String name = getName(template);
        pathString = path.endsWith(EXAMPLE_MODULE_PROVIDER + ".java") ?
            pathString.replace(EXAMPLE_MODULE_PROVIDER, name + "Provider"):
                path.endsWith(EXAMPLE_MODULE + ".java") ?
                        pathString.replace(EXAMPLE_MODULE, name): pathString;

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

    private String getName(Template template) {

//        Path path = super.outputPath(template);
//
//        if (path.endsWith(EXAMPLE_MODULE_PROVIDER + ".java")) {
//
//            String property = propertyService.getProperty(MODULE_PROVIDER_NAME);
//            System.out.println(property);
//            return property;
//        }
//
//        if(path.endsWith(EXAMPLE_MODULE + ".java")) {
//            String property = propertyService.getProperty(MODULE_NAME);
//            System.out.println(property);
//            return property;
//        }
//
//        System.out.println(propertyService.getProperty(NAME));
        return propertyService.getProperty(NAME);
    }
}
