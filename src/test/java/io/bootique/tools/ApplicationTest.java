package io.bootique.tools;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;
import io.bootique.tools.template.command.NewModuleCommand;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

    public static final String TEST_PACKAGE = "io.bootique.demo";
    public static final String TEST_CLASS_NAME = "Test";
    public static final String DEFAULT_FOLDER = System.getProperty("user.dir");

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Before
    public void before() {
        System.setProperty("user.dir", DEFAULT_FOLDER + "/target/tmp-output");
    }

    @Test
    public void runtimeTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("--new-project", "-c=classpath:test-tpl/hello-tpl.yml")
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
                .args("--new-project", "--gradle-hello-tpl")
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
        BQRuntime runtime = testFactory.app()
                .args("--new-project", "--hello-tpl")
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

        BQRuntime runtime = testFactory.app()
                .args("--new-project", "--gradle-hello-tpl")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void createModuleTest() throws IOException {
        cleanup();
        System.setProperty(NewModuleCommand.COMMAND_TYPE, NewModuleCommand.class.getName());

        BQRuntime runtime = testFactory.app()
                .args("--new-module", "-c=classpath:templates/module-tpl.yml", "--name=Test", "-java.package=io.bootique.demo")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));
        propertyService.setProperty("name", "Test");

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process(Map.of(NewModuleCommand.NAME, TEST_CLASS_NAME, NewModuleCommand.PACKAGE, TEST_PACKAGE));

    }


    private void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get("target", "tmp-output", "subfolder", "test.file"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "build.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "settings.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "pom.xml"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "io", "bootique", "demo", "Test.java"));
    }
}
