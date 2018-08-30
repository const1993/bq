package io.bootique.tools;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.TemplateService;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void runtimeTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("-c=classpath:test-tpl/hello-tpl.yml")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void gradleRuntimeTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("-c=classpath:test-tpl/gradle-hello-tpl.yml")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void loadFromClasspathTest() throws IOException {
        cleanup();
        System.setProperty("user.dir", System.getProperty("user.dir") + "/target/tmp-output");
        BQRuntime runtime = testFactory.app()
                .args("--hello-tpl")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void loadGradleProjectFromClasspathTest() throws IOException {
        cleanup();
        System.setProperty("user.dir", System.getProperty("user.dir") + "/target/tmp-output");

        BQRuntime runtime = testFactory.app()
                .args("--gradle-hello-tpl")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    private void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get("target", "tmp-output", "subfolder", "test.file"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "build.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "settings.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "pom.xml"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "io", "bootique", "demo", "Test.java"));
    }
}
