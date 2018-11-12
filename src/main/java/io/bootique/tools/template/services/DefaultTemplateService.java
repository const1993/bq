package io.bootique.tools.template.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import io.bootique.tools.template.Template;
import io.bootique.tools.template.TemplateException;
import io.bootique.tools.template.source.SourceSet;

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

        System.out.println("Start default processing.... " + templateRoot);

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
        return new Template(outputRoot.resolve(path), content);
    }

    void saveTemplate(Template template) {
        try {
            Files.createDirectories(template.getPath().getParent());
            Path fileName = template.getPath().getFileName();
            boolean equals = ("io.bootique.BQModuleProvider").equals(fileName.toString());

            if (equals) {
                Files.write(template.getPath(), template.getContent().getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.write(template.getPath(), template.getContent().getBytes(),
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
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
