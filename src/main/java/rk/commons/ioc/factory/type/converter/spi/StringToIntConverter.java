package rk.commons.ioc.factory.type.converter.spi;

import rk.commons.ioc.factory.type.converter.TypeConverter;

public class StringToIntConverter implements TypeConverter {
	
	public Object convert(Object from) {
		return Integer.parseInt((String) from);
	}
}
