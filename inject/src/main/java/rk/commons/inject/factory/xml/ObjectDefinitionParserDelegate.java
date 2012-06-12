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

	private String objectNamePrefix;

	private String objectNameSuffix;
	
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

	public void setObjectNamePrefix(String objectNamePrefix) {
		this.objectNamePrefix = objectNamePrefix;
	}

	public String getObjectNamePrefix() {
		return objectNamePrefix;
	}

	public void setObjectNameSuffix(String objectNameSuffix) {
		this.objectNameSuffix = objectNameSuffix;
	}

	public String getObjectNameSuffix() {
		return objectNameSuffix;
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
		String objectName = definition.getObjectName();
		
		if (!StringHelper.hasText(objectName)) {
			int counter = 0;
			
			do {
				++counter;
				
				objectName = PropertyHelper.generateObjectName(objectNamePrefix, objectNameSuffix,
						definition, counter);
			} while (registry.containsObjectDefinition(objectName));
			
			definition.setObjectName(objectName);
		} else {
			definition.setObjectName(PropertyHelper.getObjectName(objectNamePrefix, objectNameSuffix,
					objectName));
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
