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

import static io.bootique.tools.template.services.DefaultPropertyService.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

    public static final String TEST_PACKAGE = "io.bootique.demo";
    public static final String TEST_CLASS_NAME = "TestModule";
    public static final String DEFAULT_FOLDER = System.getProperty("user.dir");
    public static final String PARENT_FOLDER = "parent-folder";


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
                .args("--new")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(NAME, "App");
        propertyService.setProperty(ARTIFACT, PARENT_FOLDER);
        assertEquals(TEST_PACKAGE, propertyService.getProperty(GROUP));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void mavenRuntimeTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("--new", "--tpl=maven-prj")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(ARTIFACT, PARENT_FOLDER);
        propertyService.setProperty(NAME, "App");

        assertEquals(TEST_PACKAGE, propertyService.getProperty(GROUP));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void gradleRuntimeTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("--new", "--tpl=gradle-prj")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(ARTIFACT, PARENT_FOLDER);
        propertyService.setProperty(NAME, "App");

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void moduleRuntimeTest() throws IOException {
        cleanup();

        BQRuntime runtime = testFactory.app()
                .args("--new", "--tpl=module" )
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(NAME, TEST_CLASS_NAME);
        propertyService.setProperty(GROUP, TEST_PACKAGE);

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();

    }

    @Test
    public void mavenClasspathTest() throws IOException {
        cleanup();
        BQRuntime runtime = testFactory.app()
                .args("--new",  "-c=classpath:test-tpl/maven-prj.yml")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(ARTIFACT, PARENT_FOLDER);
        propertyService.setProperty(NAME, "App");

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }

    @Test
    public void gradleClasspathTest() throws IOException {
        cleanup();

        BQRuntime runtime = testFactory.app()
                .args("--new",  "-c=classpath:test-tpl/gradle-prj.yml")

                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        propertyService.setProperty(ARTIFACT, PARENT_FOLDER);
        propertyService.setProperty(NAME, "App");

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }


    private void cleanup() throws IOException {
        //Project cleanup
        Files.deleteIfExists(Paths.get("target", "tmp-output", "subfolder", "example.file"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "parent-folder", "build.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "parent-folder", "settings.gradle"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "parent-folder", "pom.xml"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "parent-folder", "main", "java", "io", "bootique", "demo", "App.java"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "parent-folder", "test", "java", "io", "bootique", "demo", "MyTest.java"));
        //Module cleanup
        Files.deleteIfExists(Paths.get("target", "tmp-output", "main", "java", "io", "bootique", "demo", "TestModule.java"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "main", "java", "io", "bootique", "demo", "TestModuleProvider.java"));
        Files.deleteIfExists(Paths.get("target", "tmp-output", "main", "resources", "META-INF", "io.bootique.BQModuleProvider"));

    }
}
