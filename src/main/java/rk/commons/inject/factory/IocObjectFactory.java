package rk.commons.inject.factory;

import java.util.Map;
import java.util.Set;

import rk.commons.inject.factory.config.ObjectDefinition;

public interface IocObjectFactory {
	
	boolean containsObject(String objectQName);

	Object getObject(String objectQName);

	<T> Map<String, T> getObjectsOfType(Class<T> type);

	Set<String> getObjectQNames();
	
	Object createObject(ObjectDefinition definition);
	
	void destroy();
}
