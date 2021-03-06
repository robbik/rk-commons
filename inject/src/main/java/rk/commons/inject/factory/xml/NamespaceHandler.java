package rk.commons.inject.factory.xml;

import org.w3c.dom.Element;

import rk.commons.inject.factory.config.ObjectDefinition;

public interface NamespaceHandler {

	void init();

	ObjectDefinition parse(Element element, ObjectDefinitionParserDelegate delegate);
}
