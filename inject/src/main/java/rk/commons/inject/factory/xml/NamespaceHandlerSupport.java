package rk.commons.inject.factory.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import rk.commons.inject.factory.config.ObjectDefinition;

public abstract class NamespaceHandlerSupport implements NamespaceHandler {

	private Map<String, ObjectDefinitionParser> parsers;

	public NamespaceHandlerSupport() {
		parsers = new HashMap<String, ObjectDefinitionParser>();
	}

	public abstract void init();

	protected void registerObjectDefinitionParser(String localName,
			ObjectDefinitionParser parser) {
		parsers.put(localName, parser);
	}

	public ObjectDefinition parse(Element element,
			ObjectDefinitionParserDelegate delegate) {

		ObjectDefinitionParser parser = parsers.get(delegate
				.getLocalName(element));
		if (parser == null) {
			throw new IllegalArgumentException("no parser found for element "
					+ delegate.getLocalName(element));
		}

		return parser.parse(element, delegate);
	}
}
