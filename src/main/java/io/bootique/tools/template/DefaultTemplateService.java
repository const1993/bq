package io.bootique.tools.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.bootique.tools.template.source.SourceSet;

public class DefaultTemplateService implements TemplateService {

    private final Path templateRoot;
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

        if (templateRoot.toString().startsWith("jar:file:")) {
            throw new TemplateException("Cant read jar file " + templateRoot);
        }

        URI fileURI = templateRoot.toUri();

        if (!Files.exists(templateRoot) ) {
            try {
                String name = templateRoot.toString();
                URL resource = ClassLoader.getSystemResource(name);

                fileURI = resource.toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (fileURI.getScheme().equals("jar") ) {
            try {
                String[] array = fileURI.toString().split("!");

                ZipFile zipFile = new ZipFile(array[0].replace("jar:file:", ""));
                readInnerZipFile(zipFile, array[1].substring(1, array[1].length()) + ".zip");
                return;
            } catch (IOException ex) {
                throw new TemplateException("Can't read template jar " + templateRoot, ex);
            }
        }

        File file = new File(fileURI);
        if (!file.isDirectory()) {
            try {
                ZipFile zipFile = new ZipFile(file);
                parseZipTemplate(zipFile);
                return;
            } catch (IOException ex) {
                throw new TemplateException("Can't read template zip " + templateRoot, ex);
            }
        }

        try {
            Path start = Paths.get(fileURI);
            Files.walk(start).forEach(path -> processPath(path, start));
        } catch (IOException ex) {
            throw new TemplateException("Can't read template root directory " + templateRoot, ex);
        }
    }

    private void readInnerZipFile(ZipFile outerZipFile, String innerZipFileEntryName) {
        File tempFile = null;
        FileOutputStream tempOut;
        ZipFile innerZipFile = null;
        try {
            tempFile = File.createTempFile("tempFile", "zip");
            tempOut = new FileOutputStream(tempFile);

            ZipEntry subentry = outerZipFile.getEntry(innerZipFileEntryName);
            InputStream inputStream = outerZipFile.getInputStream(subentry);
            inputStream.transferTo(tempOut);

            innerZipFile = new ZipFile(tempFile);

            parseZipTemplate(innerZipFile);

        } catch (IOException ex) {
            throw new TemplateException("Can't read template zip " + templateRoot, ex);
        } finally {
            try {
                if (outerZipFile != null) {
                    outerZipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (tempFile != null && !tempFile.delete()) {
                System.out.println("Could not delete " + tempFile);
            }
            try {
                if (innerZipFile != null) {
                    innerZipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void parseZipTemplate(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        String topLevelDirName = null;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            topLevelDirName = topLevelDirName == null ? entry.getName() : topLevelDirName;
            processJarEntry(entry, zipFile, topLevelDirName);
        }
    }

    void processJarEntry(ZipEntry entry, ZipFile file, String topLevelDirName) {
        if (entry.isDirectory()) {
            return;
        }

        Path relativePath = Paths.get(entry.getName().replaceFirst(topLevelDirName, ""));

        for (var set : sourceSets) {
            if (set.combineFilters().test(relativePath)) {
                saveTemplate(set.combineProcessors().process(loadTemplate(relativePath, loadZipEntryContent(file, entry))));
            }
        }
    }

    void processPath(Path path, Path start) {
        // TODO: any good use-case for empty dirs in template projects? skip for now.
        if (Files.isDirectory(path)) {
            return;
        }

        Path relativeDir = start.relativize(path);

        // Process templates
        for (var set : sourceSets) {
            if (set.combineFilters().test(relativeDir)) {
                saveTemplate(set.combineProcessors().process(loadTemplate(relativeDir, loadContent(path))));
            }
        }
    }

    private Template loadTemplate(Path path, String content) {
        return new Template(outputRoot.resolve(path), content);
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

    String loadZipEntryContent(ZipFile file, ZipEntry entry) {
        String content;
        try {
            content = new String(file.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read template " + entry.getName() + " from zip file " + file.getName(), ex);
        }
        return content;
    }
}
