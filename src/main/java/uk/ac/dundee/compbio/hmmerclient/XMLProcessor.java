package uk.ac.dundee.compbio.hmmerclient;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

class XMLProcessor {

  private static final
  DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newDefaultInstance();

  private static final DocumentBuilder documentBuilder;

  static {
    try {
      documentBuilder = builderFactory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  static IOException processErrorBody(InputStream responseBody) {
    final Document doc;
    try {
      doc = documentBuilder.parse(responseBody);
      doc.normalize();
    }
    catch (IOException e) {
      return e;
    }
    catch (SAXException e) {
      return new IOException("malformed XML received from the server", e);
    }
    var root = doc.getDocumentElement();
    if (!root.getNodeName().equals("error"))
      return new IOException("server response missing XML node /error");
    var descriptionNode = root.getElementsByTagName("description").item(0);
    if (descriptionNode == null)
      return new IOException("server response missing XML node /error/description");
    return new IOException(descriptionNode.getTextContent());
  }

  static Collection<ResultType> processResultTypesBody(InputStream responseBody)
      throws IOException {
    final Document doc;
    try {
      doc = documentBuilder.parse(responseBody);
      doc.normalize();
    }
    catch (SAXException e) {
      throw new IOException("malformed XML received from the server", e);
    }
    var root = doc.getDocumentElement();
    if (!root.getNodeName().equals("types"))
      throw new IOException("XML response missing /types node");

    var results = new ArrayList<ResultType>();
    var node = root.getFirstChild();
    while (node != null) {
      if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("type"))
        results.add(parseResultTypeNode(node));
      node = node.getNextSibling();
    }
    return results;
  }

  private static ResultType parseResultTypeNode(Node rootNode) throws IOException {
    assert rootNode.getNodeType() == Node.ELEMENT_NODE;
    assert rootNode.getNodeName().equals("type");
    String description = null, fileSuffix = null, identifier = null, label = null, mediaType = null;
    var node = rootNode.getFirstChild();
    while (node != null) {
      var textContent = node.getTextContent();
      switch (node.getNodeName()) {
        case "description":
          description = textContent;
          break;
        case "fileSuffix":
          fileSuffix = textContent;
          break;
        case "identifier":
          identifier = textContent;
          break;
        case "label":
          label = textContent;
          break;
        case "mediaType":
          mediaType = textContent;
          break;
      }
      node = node.getNextSibling();
    }
    if (description == null || fileSuffix == null || identifier == null || label == null || mediaType == null) {
      throw new IOException("incomplete <type> node");
    }
    return new ResultType(description, fileSuffix, identifier, label, mediaType);
  }
}
