package rk.commons.inject.factory.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.ObjectDefinitionRegistry;
import rk.commons.inject.util.PropertyHelper;
import rk.commons.loader.ResourceLoader;
import rk.commons.util.StringHelper;

public class ObjectDefinitionParserDelegate {

	public static final String DEFAULT_OBJECT_PACKAGE_NAME = null;
	
	private final ResourceLoader resourceLoader;

	private NamespaceHandlerResolver resolver;

	private ObjectDefinitionRegistry registry;

	private String packageName;
	
	public ObjectDefinitionParserDelegate(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setNamespaceHandlerResolver(NamespaceHandlerResolver resolver) {
		this.resolver = resolver;
	}

	public void setObjectDefinitionRegistry(ObjectDefinitionRegistry registry) {
		this.registry = registry;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getNamespaceURI(Node node) {
		return node.getNamespaceURI();
	}

	public String getLocalName(Node node) {
		return node.getLocalName();
	}

	public Object parse(Element element) {
		String namespaceURI = getNamespaceURI(element);

		NamespaceHandler handler = resolver.resolve(namespaceURI);

		ObjectDefinition definition = handler.parse(element, this);
		String objectQName = definition.getObjectQName();
		
		if (!StringHelper.hasText(objectQName)) {
			int counter = 1;
			
			while (registry.containsObjectDefinition(
					objectQName = PropertyHelper.generateObjectQName(packageName, definition, counter))) {
				++counter;
			}
			
			definition.setObjectQName(objectQName);
		} else {
			definition.setObjectQName(PropertyHelper.applyDefaultPackageName(packageName, objectQName));
		}

		registry.registerObjectDefinition(definition);

		return definition;
	}
	
	public Object parseFirstChildElement(Element element) {
		NodeList childNodes = element.getChildNodes();
		
		for (int i = 0, n = childNodes.getLength(); i < n; ++i) {
			Node childNode = childNodes.item(i);
			
			if (childNode instanceof Element) {
				return parse((Element) childNode);
			}
		}
		
		return null;
	}
	
	public List<Object> parseChildElements(Element element) {
		List<Object> childObjects = new ArrayList<Object>();
		
		NodeList childNodes = element.getChildNodes();
		
		for (int i = 0, n = childNodes.getLength(); i < n; ++i) {
			Node childNode = childNodes.item(i);
			
			if (childNode instanceof Element) {
				childObjects.add(parse((Element) childNode));
			}
		}
		
		return childObjects;
	}

	public List<Object> parseChildElements(Element element, String tagName) {
		List<Object> childObjects = new ArrayList<Object>();
		
		NodeList childNodes = element.getElementsByTagName(tagName);
		
		for (int i = 0, n = childNodes.getLength(); i < n; ++i) {
			Node childNode = childNodes.item(i);
			
			if (childNode instanceof Element) {
				childObjects.add(parse((Element) childNode));
			}
		}
		
		return childObjects;
	}
}
