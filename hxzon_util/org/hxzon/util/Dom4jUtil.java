package org.hxzon.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class Dom4jUtil {

    public static Document toDocument(String xmlStr) {
        try {
            return DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static Element getRoot(Document doc) {
        return doc.getRootElement();
    }

    public static Element getRoot(String xmlStr) {
        return getRoot(toDocument(xmlStr));
    }

    public static Element getElement(Element e, String path) {
        return (Element) e.selectSingleNode(path);
    }

    @SuppressWarnings("unchecked")
    public static List<Element> getElements(Element e, String path) {
        return e.selectNodes(path);
    }

    @SuppressWarnings("unchecked")
    public static List<Element> getElements(Element e) {
        return e.elements();
    }

    @SuppressWarnings("unchecked")
    public static Element getElement(Element e) {
        List<Element> es = e.elements();
        return es.isEmpty() ? null : es.get(0);
    }

    public static String getText(Element parent, String path) {
        Node node = parent.selectSingleNode(path);
        if (node == null) {
            return null;
        } else {
            return node.getText().trim();
        }
    }

    public static Element addElement(Element parent, String name, String text) {
        Element e = parent.addElement(name);
        e.setText(text);
        return e;
    }
}
