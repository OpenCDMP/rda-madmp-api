package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.AccessType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class AccessTypeToShortConverter implements Converter<AccessType, Short> {
	@Override
	public Short convert(AccessType source) {
		return source.getValue();
	}
}
