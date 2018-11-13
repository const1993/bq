package io.bootique.tools.template.services;

import io.bootique.tools.template.TemplateException;

import java.nio.file.Path;

public interface TemplateService {

    void process(Path parentFolder) throws TemplateException;
}
