package io.bootique.tools.template.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.bootique.tools.template.Template;
import io.bootique.tools.template.TemplateException;
import io.bootique.tools.template.source.SourceSet;

import static java.nio.file.StandardOpenOption.*;

public class DefaultTemplateService implements TemplateService {

    protected final Path templateRoot;
    protected final Path outputRoot;
    protected final List<SourceSet> sourceSets;

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

        try {
            Files.walk(templateRoot).forEach(this::processPath);
        } catch (IOException ex) {
            throw new TemplateException("Can't read template root directory " + templateRoot, ex);
        }
    }

    void processPath(Path path) {
        // TODO: any good use-case for empty dirs in template projects? skip for now.
        if (Files.isDirectory(path)) {
            return;
        }

        Path relativeDir = templateRoot.relativize(path);

        // Process templates
        for (var set : sourceSets) {
            if (set.combineFilters().test(relativeDir)) {
                saveTemplate(set.combineProcessors().process(loadTemplate(relativeDir, loadContent(path))));
            }
        }
    }

    Template loadTemplate(Path path, String content) {
        Path pathWithParent = outputRoot.resolve("_");
        return new Template(pathWithParent.resolve(path), content);
    }

    void saveTemplate(Template template) {
        try {
            Path tmpath = template.getPath();
            Path path = Paths.get(tmpath.toString().replace("/_", ""));
            Files.createDirectories(path.getParent());
            Path fileName = path.getFileName();
            boolean equals = ("io.bootique.BQModuleProvider").equals(fileName.toString());

            if (equals) {
                Files.write(path, template.getContent().getBytes(), CREATE, APPEND);
            } else {
                Files.write(path, template.getContent().getBytes(), CREATE_NEW, WRITE);
            }
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

}
