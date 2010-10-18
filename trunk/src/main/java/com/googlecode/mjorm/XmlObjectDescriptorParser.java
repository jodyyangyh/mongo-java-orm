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

import com.googlecode.jot.ReflectionUtil;

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
		XPathExpressionException,
		ClassNotFoundException {
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
		XPathExpressionException,
		ClassNotFoundException {
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
		throws XPathExpressionException,
		ClassNotFoundException {
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
			boolean foundIdentifier = false;
			for (int j=0; j<propertyEls.getLength(); j++) {

				// get element
				Element propertyEl = (Element)propertyEls.item(j);

				// get property name and type
				String propName = propertyEl.getAttribute("name");
				Class<?> propClass = propertyEl.hasAttribute("class")
					? Class.forName(propertyEl.getAttribute("class")) : null;
				boolean propIsIdentifier = propertyEl.hasAttribute("id")
					&& Boolean.parseBoolean(propertyEl.getAttribute("id"));
				boolean propIsAutoGen = propertyEl.hasAttribute("auto")
					&& Boolean.parseBoolean(propertyEl.getAttribute("auto"));
				String propColumn = propertyEl.hasAttribute("column")
					? propertyEl.getAttribute("column") : propName;

				// find the getter and setter.
				Method propSetter = ReflectionUtil.findSetter(objClass, propName, propClass);
				Method propGetter = ReflectionUtil.findGetter(objClass, propName, propClass);
				if (propGetter==null || propSetter==null) {
					throw new IllegalArgumentException(
						"Unable to find getter or setter named "+propName+" for: "+objClass);
				}

				// make sure we have the type and get
				// the generic type if there is one
				if (propClass==null) {
					propClass = propGetter.getReturnType();
				}
				Type propGenericType = propGetter.getGenericReturnType();
				if (propIsIdentifier && !foundIdentifier) {
					foundIdentifier = true;
				} else if (propIsIdentifier && foundIdentifier) {
					throw new IllegalArgumentException(
						"Two identifiers found for: "+objClass);
				}

				// get parameter types
				NodeList parameterTypeEls = (NodeList)xpath.evaluate(
					"./type-param", propertyEl, XPathConstants.NODESET);
				Class<?>[] propParamTypes = new Class<?>[parameterTypeEls.getLength()];
				for (int k=0; k<parameterTypeEls.getLength(); k++) {
					Element parameterTypeEl = (Element)parameterTypeEls.item(k);
					propParamTypes[k] = Class.forName(parameterTypeEl.getAttribute("class"));
				}

				// create the PropertyDescriptor
				PropertyDescriptor desc = new PropertyDescriptor();
				desc.setName(propName);
				desc.setPropColumn(propColumn);
				desc.setGetter(propGetter);
				desc.setSetter(propSetter);
				desc.setGenericType(propGenericType);
				desc.setIdentifier(propIsIdentifier);
				desc.setObjectClass(propClass);
				desc.setParameterTypes(propParamTypes);
				desc.setAutoGenerated(propIsAutoGen);

				// add to the object descriptor
				descriptor.getProperties().add(desc);
			}

			// add return
			ret.add(descriptor);
		}

		// return the list
		return ret;
	}

}
