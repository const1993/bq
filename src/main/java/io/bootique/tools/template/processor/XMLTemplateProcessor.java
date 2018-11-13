package io.bootique.tools.template.processor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;
import io.bootique.tools.template.TemplateException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static io.bootique.tools.template.services.DefaultPropertyService.ARTIFACT;
import static java.io.File.separator;

public abstract class XMLTemplateProcessor implements TemplateProcessor {

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template.withContent(processContent(template))
                .withPath(outputPath(template));
    }

    String processContent(Template template) {
        Document document = createDocument(template.getContent());
        processDocument(document);
        return documentToString(document);
    }

    protected abstract Document processDocument(Document document);

    String documentToString(Document document) {
        Source input = new DOMSource(document);
        StringWriter writer = new StringWriter();
        Result output = new StreamResult(writer);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(input, output);
        } catch (Exception ex) {
            throw new TemplateException("Unable convert DOM to string", ex);
        }

        return writer.toString();
    }

    Document createDocument(String content) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);

        try {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ex) {
            throw new TemplateException("Unable to configure DocumentBuilderFactory", ex);
        }

        try {
            DocumentBuilder domBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(content));
            return domBuilder.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new TemplateException("Unable to parse XML", e);
        }
    }

    Path outputPath(Template template) {
        Path input = template.getPath();
        String pathStr = input.toString();
        String artifact = propertyService.getProperty(ARTIFACT);
        String parentFolder = !artifact.isEmpty() ? separator + artifact : "";
        return Paths.get(pathStr.replaceFirst(separator + "_", parentFolder));
    }

}
