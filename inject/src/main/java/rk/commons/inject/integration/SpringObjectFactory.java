package rk.commons.inject.integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import rk.commons.inject.factory.ObjectNotFoundException;
import rk.commons.inject.factory.SingletonObjectFactory;
import rk.commons.loader.ResourceLoader;

public class SpringObjectFactory extends SingletonObjectFactory {

	private final ApplicationContext spring;

	public SpringObjectFactory(ResourceLoader resourceLoader, ApplicationContext spring) {
		super(resourceLoader);
		
		this.spring = spring;
	}

	public boolean containsObject(String objectQName) {
		if (super.containsObject(objectQName)) {
			return true;
		} else {
			return spring.containsBean(objectQName);
		}
	}

	public Object getObject(String objectQName) {
		ObjectNotFoundException notFoundException;
		
		try {
			return super.getObject(objectQName);
		} catch (ObjectNotFoundException e) {
			notFoundException = e;
		}
		
		try {
			return spring.getBean(objectQName);
		} catch (NoSuchBeanDefinitionException e) {
			throw notFoundException;
		}
	}

	public <T> Map<String, T> getObjectsOfType(Class<T> type) {
		Map<String, T> merged = new HashMap<String, T>();
		
		merged.putAll(spring.getBeansOfType(type));
		merged.putAll(super.getObjectsOfType(type));
		
		return merged;
	}

	public Set<String> getObjectNames() {
		Set<String> merged = new HashSet<String>();
		
		merged.addAll(super.getObjectNames());
		merged.addAll(Arrays.asList(spring.getBeanDefinitionNames()));
		
		return merged;
	}
}
