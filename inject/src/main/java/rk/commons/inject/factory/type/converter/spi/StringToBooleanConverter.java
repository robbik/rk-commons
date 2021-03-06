package rk.commons.inject.factory.type.converter.spi;

import rk.commons.inject.factory.type.converter.TypeConverter;

public class StringToBooleanConverter implements TypeConverter {
	
	public static final Class<?> FROM = String.class;
	
	public static final Class<?> TO = boolean.class;
	
	public Object convert(Object from) {
		return Boolean.parseBoolean((String) from);
	}
}
