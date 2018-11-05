package io.bootique.tools;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.services.TemplateService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.bootique.tools.template.services.DefaultPropertyService.NAME;
import static io.bootique.tools.template.services.DefaultPropertyService.PACKAGE;
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
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        assertEquals(TEST_PACKAGE, propertyService.getProperty(PACKAGE));

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
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        propertyService.setProperty(PACKAGE, TEST_PACKAGE);


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
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        propertyService.setProperty(PACKAGE, TEST_PACKAGE);

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
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        propertyService.setProperty(PACKAGE, TEST_PACKAGE);

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void createModuleTest() throws IOException {
        cleanup();

        BQRuntime runtime = testFactory.app()
                .args("--new-module", "-m" )
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        propertyService.setProperty(PACKAGE, TEST_PACKAGE);

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();

    }

    private void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get("target", "tmp-output", "subfolder", "test.file"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "build.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "settings.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "pom.xml"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "io", "bootique", "demo", "Test.java"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "resources", "META-INF", "io.bootique.BQModuleProvider"));

    }
}
