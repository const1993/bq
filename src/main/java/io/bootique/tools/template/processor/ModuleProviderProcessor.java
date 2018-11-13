package io.bootique.tools.template.processor;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

import static io.bootique.tools.template.services.DefaultPropertyService.GROUP;
import static io.bootique.tools.template.services.DefaultPropertyService.NAME;

public class ModuleProviderProcessor implements TemplateProcessor {

    protected static final String MODULE_PATH_EXAMPLE = "service.provider.example";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template.withContent(processContent(template));
    }

    String processContent(Template template) {

        String content = template.getContent();

        String name = propertyService.getProperty(NAME);

        if (name == null) {
            return content;
        }

        name = !name.endsWith("Provider") ? name + "Provider" : name;
        String group = propertyService.getProperty(GROUP);
        return content.replaceAll(MODULE_PATH_EXAMPLE, group.isEmpty() ? name +"\n" : group + "." + name + "\n");
    }


}
