package rk.commons.inject.factory.support;

import java.util.Set;

import rk.commons.inject.factory.config.ObjectDefinition;

public interface ObjectDefinitionRegistry {

	void registerObjectDefinition(ObjectDefinition definition);
	
	void removeObjectDefinition(String objectQName);
	
	ObjectDefinition getObjectDefinition(String objectQName);
	
	boolean containsObjectDefinition(String objectQName);
	
	Set<String> getObjectQNames();
}
