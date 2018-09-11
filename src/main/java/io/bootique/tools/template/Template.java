package io.bootique.tools.template;

import java.nio.file.Path;
import java.util.Objects;

public class Template {

    private String name;
    private final Path path;
    private final String content;

    public Template(Path path, String content, String name, String dotSeparatedPath) {
        this.path = Objects.requireNonNull(path);
        this.content = Objects.requireNonNull(content);
        this.name = name;
    }

    public Template(Path path, String content) {
        this(path, content, null, null);
    }

    public Path getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public Template withPath(Path newPath) {
        if(path.equals(newPath)) {
            return this;
        }
        return new Template(newPath, content);
    }

    public Template withContent(String newContent) {
        if(content.equals(newContent)) {
            return this;
        }
        return new Template(path, newContent);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "template {" + path + "}";
    }
}
