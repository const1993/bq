package io.bootique.tools.template.processor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;
import static io.bootique.tools.template.services.DefaultPropertyService.PACKAGE;

public class JavaPackageProcessor implements TemplateProcessor {

    protected static final String TEMPLATE_PACKAGE = "example";
    protected static final String EXAMPLE_CLASS = "Test";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template
                .withPath(outputPath(template))
                .withContent(processContent(template));
    }

    String processContent(Template template) {
        String content = template.getContent();
        content = replacePackageDeclaration(content);
        content = replaceImportDeclaration(content);
        return content.replace("class " + EXAMPLE_CLASS, "class " + propertyService.getProperty(NAME));
    }

    String replacePackageDeclaration(String content) {
        return content.replaceAll("\\bpackage " + TEMPLATE_PACKAGE, "package " + propertyService.getProperty(PACKAGE));
    }

    String replaceImportDeclaration(String content) {
        return content.replaceAll("\\bimport " + TEMPLATE_PACKAGE, "import " + propertyService.getProperty(PACKAGE));
    }

    Path outputPath(Template template) {
        Path input = template.getPath();
        String pathStr = input.toString();
        Path packagePath = packageToPath(propertyService.getProperty(PACKAGE));
        char separator = File.separatorChar;
        pathStr = pathStr.replaceAll( separator + "?" + TEMPLATE_PACKAGE + separator, separator + packagePath.toString() + separator);
        return Paths.get(pathStr);
    }

    Path packageToPath(String packageName) {
        char separator = File.separatorChar;
        return Paths.get(packageName.replace('.', separator));
    }
}
