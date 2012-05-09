package rk.commons.inject.factory.type.converter.spi;

import rk.commons.inject.factory.type.converter.TypeConverter;

public class StringToIntConverter implements TypeConverter {
	
	public Object convert(Object from) {
		return Integer.parseInt((String) from);
	}
}
