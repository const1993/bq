package io.bootique.tools.template.processor;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.bootique.tools.template.TemplateException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static io.bootique.tools.template.services.DefaultPropertyService.GROUP;
import static io.bootique.tools.template.services.DefaultPropertyService.NAME;

public class MavenProcessor extends XMLTemplateProcessor {


    @Override
    protected Document processDocument(Document document) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            Node artefactId = (Node)xpath.evaluate("/project/artifactId", document, XPathConstants.NODE);
            artefactId.setTextContent(propertyService.getProperty("project.artifactId"));

            Node groupId = (Node)xpath.evaluate("/project/groupId", document, XPathConstants.NODE);
            groupId.setTextContent(propertyService.getProperty("project.groupId"));

            Node version = (Node)xpath.evaluate("/project/version", document, XPathConstants.NODE);
            version.setTextContent(propertyService.getProperty("project.version"));

            Node main = (Node) xpath.evaluate("/project/properties/main.class", document, XPathConstants.NODE);
            if (main != null) {
                main.setTextContent(propertyService.getProperty(GROUP) + "." + propertyService.getProperty(NAME));
            }
        } catch (XPathExpressionException ex) {
            throw new TemplateException("Unable to modify xml, is template a proper maven xml?", ex);
        }

        return document;
    }
}
