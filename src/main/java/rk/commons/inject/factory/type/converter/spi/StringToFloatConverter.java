package rk.commons.inject.factory.type.converter.spi;

import rk.commons.inject.factory.type.converter.TypeConverter;

public class StringToFloatConverter implements TypeConverter {
	
	public Object convert(Object from) {
		return Float.parseFloat((String) from);
	}
}
