package io.bootique.tools.template.services;

import io.bootique.tools.template.TemplateException;
import io.bootique.tools.template.source.SourceSet;

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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTemplateService extends DefaultTemplateService {

    public ZipTemplateService(Path templateRoot, Path outputRoot, List<SourceSet> sourceSets) {
        super(templateRoot, outputRoot, sourceSets);
    }

    @Override
    public void process(Path parentFolder) throws TemplateException {

        outputWithParent = outputRoot.resolve(parentFolder);

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
                System.out.println("Project will be created in folder: " + outputWithParent );

                ZipFile zipFile = new ZipFile(array[0].replace("jar:file:", ""));
                readInnerZipFile(zipFile, array[1].substring(1, array[1].length()));
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
    }

    void readInnerZipFile(ZipFile outerZipFile, String innerZipFileEntryName) {
        File tempFile = null;
        FileOutputStream tempOut;
        ZipFile innerZipFile = null;
        try {
            tempFile = File.createTempFile("tempFile", "zip");
            tempOut = new FileOutputStream(tempFile);

            ZipEntry subEntry = outerZipFile.getEntry(innerZipFileEntryName);
            InputStream inputStream = outerZipFile.getInputStream(subEntry);
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
