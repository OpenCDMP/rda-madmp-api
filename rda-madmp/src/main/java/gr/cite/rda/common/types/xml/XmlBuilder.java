package gr.cite.rda.common.types.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


public class XmlBuilder {
    private static final Logger logger = LoggerFactory.getLogger(XmlBuilder.class);

    public static Document getDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            return doc;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String generateXml(Document doc) {
        TransformerFactory tFact = TransformerFactory.newInstance();
        Transformer trans;
        try {
            trans = tFact.newTransformer();
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(doc);
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            trans.transform(source, result);
            return writer.toString();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static Document fromXml(String xml) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            InputSource inputStream = new InputSource(new StringReader(xml));
            Document doc = docBuilder.parse(inputStream);
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    public static Element getNodeFromListByTagName(NodeList list, String tagName) {
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node element = list.item(temp);
            if (element.getNodeType() == Node.ELEMENT_NODE) {
                if (element.getNodeName().equals(tagName)) return (Element) element;
            }
        }
        return null;
    }
}
