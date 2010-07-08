package com.googlecode.mjorm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses XML Object descriptor files and returns
 * {@link ObjectDescriptor}s contained within the file.
 */
public class XmlObjectDescriptorParser {

	private XPath xpath;
	private DocumentBuilder builder;

	/**
	 * Adds the given document configuration.
	 * @param file the {@link File}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 * @throws ClassNotFoundException on error
	 */
	public List<ObjectDescriptor> parseDocument(File file)
		throws IOException,
		ParserConfigurationException,
		SAXException,
		XPathExpressionException, ClassNotFoundException {
		return parseDocument(new FileInputStream(file));
	}

	/**
	 * Adds the given document configuration.
	 * @param inputStream the {@link InputStream}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 * @throws ClassNotFoundException on error
	 */
	public List<ObjectDescriptor> parseDocument(InputStream inputStream)
		throws IOException,
		ParserConfigurationException,
		SAXException,
		XPathExpressionException, ClassNotFoundException {
		if (builder==null) {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		return parseDocument(builder.parse(inputStream));
	}

	/**
	 * Adds the given document configuration.
	 * @param doc the document
	 * @throws XPathExpressionException on error
	 * @throws ClassNotFoundException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 */
	public List<ObjectDescriptor> parseDocument(Document doc)
		throws XPathExpressionException, ClassNotFoundException {
		if (xpath==null) {
			xpath = XPathFactory.newInstance().newXPath();
		}

		// create return list
		List<ObjectDescriptor> ret = new ArrayList<ObjectDescriptor>();

		// get descriptor elements
		NodeList descriptorEls = (NodeList)xpath.evaluate(
			"/descriptors/object", doc, XPathConstants.NODESET);

		// loop through each element
		for (int i=0; i<descriptorEls.getLength(); i++) {

			// get descriptor
			Element descriptorEl = (Element)descriptorEls.item(i);
			ObjectDescriptor descriptor = new ObjectDescriptor();

			// get class
			Class<?> objClass = Class.forName(descriptorEl.getAttribute("class"));
			descriptor.setObjectClass(objClass);

			// get properties
			NodeList propertyEls = (NodeList)xpath.evaluate(
				"./property", descriptorEl, XPathConstants.NODESET);
			for (int j=0; j<propertyEls.getLength(); j++) {

				// get element
				Element propertyEl = (Element)propertyEls.item(j);

				// get property name and type
				String propName = propertyEl.getAttribute("name");
				Class<?> propClass = propertyEl.hasAttribute("class")
					? Class.forName(propertyEl.getAttribute("class")) : null;
				Method setter = ReflectionUtil.findSetter(objClass, propName, propClass);
				Method getter = ReflectionUtil.findGetter(objClass, propName, propClass);
				if (getter==null || setter==null) {
					throw new IllegalArgumentException(
						"Unable to find getter or setter for: "+propName);
				}
				if (propClass==null) {
					propClass = getter.getReturnType();
				}
				Type genericType = getter.getGenericReturnType();

				// get parameter types
				NodeList parameterTypeEls = (NodeList)xpath.evaluate(
					"./type-param", propertyEl, XPathConstants.NODESET);
				Class<?>[] parameterTypes = new Class<?>[parameterTypeEls.getLength()];
				for (int k=0; k<parameterTypeEls.getLength(); k++) {
					Element parameterTypeEl = (Element)parameterTypeEls.item(k);
					parameterTypes[k] = Class.forName(parameterTypeEl.getAttribute("class"));
				}

				// create the PropertyDescriptor and add it
				PropertyDescriptor desc = new PropertyDescriptor(
					propName, propClass, genericType, parameterTypes, setter, getter);
				descriptor.getProperties().add(desc);
			}

			// add return
			ret.add(descriptor);
		}

		// return the list
		return ret;
	}

}
