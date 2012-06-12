package rk.commons.inject.factory;

import java.util.Map;
import java.util.Set;

import rk.commons.inject.factory.config.ObjectDefinition;

public interface ObjectFactory {
	
	boolean containsObject(String objectName);

	Object getObject(String objectName);

	<T> T getObject(String objectName, Class<T> type);

	<T> Map<String, T> getObjectsOfType(Class<T> type);

	Set<String> getObjectNames();
	
	Object createObject(ObjectDefinition definition);
	
	void destroy();
}
