package io.bootique.tools.template.processor;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;
import static java.io.File.separator;

public class ModuleProviderProcessor implements TemplateProcessor {

    protected static final String MODULE_PROVIDER = "io.bootique.BQModuleProvider";
    protected static final String MODULE_PATH_EXAMPLE = "service.provider.example";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template.withContent(processContent(template)).withPath(processPath(template));
    }

    String processContent(Template template) {

        String content = template.getContent();

        String name = propertyService.getProperty(NAME);

        if (name == null) {
            return content;
        }

        name = !name.endsWith("ModuleProvider") ? name + "ModuleProvider" : name;
        return content.replaceAll(MODULE_PATH_EXAMPLE, propertyService.getProperty("java.package") + "." + name);
    }

    Path processPath(Template template) {
        Path path = template.getPath();
        String input = path.toString();

        return  Paths.get( input.replaceAll(MODULE_PROVIDER, separator + "resources" + separator + "META-INF" + separator + MODULE_PROVIDER));
    }
}
