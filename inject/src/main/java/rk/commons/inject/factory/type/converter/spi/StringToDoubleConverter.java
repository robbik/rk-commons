package rk.commons.inject.factory.type.converter.spi;

import rk.commons.inject.factory.type.converter.TypeConverter;

public class StringToDoubleConverter implements TypeConverter {
	
	public Object convert(Object from) {
		return Double.parseDouble((String) from);
	}
}
