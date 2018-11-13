package io.bootique.tools.template.processor;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

import static io.bootique.tools.template.services.DefaultPropertyService.GROUP;
import static java.io.File.separatorChar;

public class JavaPackageProcessor implements TemplateProcessor {

    protected static final String TEMPLATE_PACKAGE = "example";

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
        return content;
    }

    String replacePackageDeclaration(String content) {
        return content.replaceAll("\\bpackage " + TEMPLATE_PACKAGE, "package " + propertyService.getProperty(GROUP));
    }

    String replaceImportDeclaration(String content) {
        return content.replaceAll("\\bimport " + TEMPLATE_PACKAGE, "import " + propertyService.getProperty(GROUP));
    }

    Path outputPath(Template template) {
        Path input = template.getPath();
        String pathStr = input.toString();
        Path packagePath = packageToPath(propertyService.getProperty(GROUP));
        pathStr = pathStr.replaceAll( separatorChar + "?" + TEMPLATE_PACKAGE + separatorChar, separatorChar + packagePath.toString() + separatorChar);
        return Paths.get(pathStr);
    }

    Path packageToPath(String packageName) {
        return Paths.get(packageName.replace('.', separatorChar));
    }
}
