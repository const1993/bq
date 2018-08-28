package io.bootique.tools.template;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.bootique.tools.template.processor.TemplateProcessor;
import io.bootique.tools.template.source.SourceSet;

public class DefaultTemplateService implements TemplateService {

    private Path templateRoot;
    private final Path outputRoot;
    private final List<SourceSet> sourceSets;

    public DefaultTemplateService(Path templateRoot, Path outputRoot, List<SourceSet> sourceSets) {
        this.templateRoot = templateRoot;
        this.outputRoot = outputRoot == null ? Paths.get(System.getProperty("user.dir")) : outputRoot;
        this.sourceSets = sourceSets.isEmpty()
                ? List.of(new SourceSet())  // will just copy everything to destination root
                : sourceSets;
    }

    public void process() throws TemplateException {

        if (templateRoot.toString().startsWith("~") || outputRoot.toString().startsWith("~")) {
            throw new TemplateException("Can't read template root directory with '~' home " + templateRoot);
        }

        if (!Files.exists(templateRoot)) {
            try {
                String name = templateRoot.toString();
                System.out.println("name: " + name);
                URL resource = ClassLoader.getSystemResource(name);
                System.out.println(resource);

                URI uri = resource.toURI();
                System.out.println(uri);

                Path path = null;

                if (uri.getScheme().equals("jar")) {
                    String[] array = uri.toString().split("!");
                    System.out.println("file: "+ array[0]);
                    System.out.println("template: "+ array[1]);

                    ZipFile zipFile = new ZipFile(array[0].replace("jar:file:", ""));
                    readInnerZipFile(zipFile, array[1].substring(1, array[1].length()) + ".zip");
                } else {
                    // Not running in a jar, so just use a regular filesystem path
                    path = Paths.get(uri);
                }

                System.out.println("url: " + path);
                templateRoot = null;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.walk(templateRoot).forEach(this::processPath);
        } catch (IOException ex) {
            throw new TemplateException("Can't read template root directory " + templateRoot, ex);
        }
    }

    public void readInnerZipFile(ZipFile outerZipFile, String innerZipFileEntryName) {
        File tempFile = null;
        FileOutputStream tempOut = null;
        ZipFile innerZipFile = null;
        try {
            tempFile = File.createTempFile("tempFile", "zip");
            tempOut = new FileOutputStream(tempFile);

            ZipEntry subentry = outerZipFile.getEntry(innerZipFileEntryName);
            System.out.println("innerZipFileEntryName " + innerZipFileEntryName);
            System.out.println("outerZipFile " + outerZipFile);
            System.out.println("subentry " + subentry);
            InputStream inputStream = outerZipFile.getInputStream(subentry);
            inputStream.transferTo(tempOut);

                    innerZipFile = new ZipFile(tempFile);
            Enumeration<? extends ZipEntry> entries = innerZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println("I can read" + entry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Make sure to clean up your I/O streams
            try {
                if (outerZipFile != null)
                    outerZipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (tempFile != null && !tempFile.delete()) {
                System.out.println("Could not delete " + tempFile);
            }
            try {
                if (innerZipFile != null)
                    innerZipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void processJarEntry(ZipEntry entry) {
        String realName = entry.getName();
        System.out.println("entry: " + realName);

    }

    void processPath(Path path) {
        // TODO: any good use-case for empty dirs in template projects? skip for now.
        if(Files.isDirectory(path)) {
            return;
        }

        Path relativeDir = templateRoot.relativize(path);

        // Process templates
        for (var set : sourceSets) {
            if (set.combineFilters().test(relativeDir)) {
                TemplateProcessor templateProcessor = set.combineProcessors();
                saveTemplate(templateProcessor.process(loadTemplate(path)));
            }
        }
    }

    private Template loadTemplate(Path path) {
        return new Template(convertToOutputPath(path), loadContent(path));
    }

    void saveTemplate(Template template) {
        try {
            Files.createDirectories(template.getPath().getParent());
            Files.write(template.getPath(), template.getContent().getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new TemplateException("Can't process template " + template, ex);
        }
    }

    String loadContent(Path path) {
        String content;
        try {
            content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read template " + path, ex);
        }
        return content;
    }

    /**
     * Utility method that converts path from templates source dir into target dir.
     *
     * @param path original path in templates directory
     * @return path in target directory
     */
    Path convertToOutputPath(Path path) {
        Path relativeDir = templateRoot.relativize(path);
        return outputRoot.resolve(relativeDir);
    }
}
