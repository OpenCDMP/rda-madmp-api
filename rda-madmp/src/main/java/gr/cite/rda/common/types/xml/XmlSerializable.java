package gr.cite.rda.common.types.xml;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlSerializable<T> {
    Element toXml(Document doc);

    T fromXml(Element item);
}
