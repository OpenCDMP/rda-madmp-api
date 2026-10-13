package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.AccessType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ShortToAccessTypeConverter implements Converter<Integer, AccessType> {
	@Override
	public AccessType convert(Integer source) {
		return AccessType.of(source.shortValue());
	}
}
