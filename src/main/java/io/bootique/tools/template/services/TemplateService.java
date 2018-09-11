package io.bootique.tools.template.services;

import io.bootique.tools.template.TemplateException;

import java.util.Map;

public interface TemplateService {

    void process() throws TemplateException;

    void process(Map<String, String> options) throws TemplateException;

}
