package io.bootique.tools.template.services;

import io.bootique.tools.template.PropertyService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPropertyService implements PropertyService {

    public static String NAME = "name";
//    public static String PACKAGE = "java.package";
    public static String GROUP = "project.groupId";
    public static String ARTIFACT = "project.artifactId";
    public static String VERSION = "project.version";

    public static String MODULE_NAME = "module.name";
    public static String MODULE_PROVIDER_NAME = "module.provider.name";

    private final Map<String, String> propertyMap = new ConcurrentHashMap<>();

    @Override
    public String getProperty(String property) {
        return propertyMap.get(property);
    }

    @Override
    public void setProperty(String property, String value) {
        propertyMap.put(property, value);
    }

    @Override
    public boolean hasProperty(String property) {
        return propertyMap.containsKey(property);
    }
}
