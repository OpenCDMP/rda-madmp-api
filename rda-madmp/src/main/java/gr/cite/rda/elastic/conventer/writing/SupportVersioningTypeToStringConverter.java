package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.SupportVersioningType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class SupportVersioningTypeToStringConverter implements Converter<SupportVersioningType, String> {
	@Override
	public String convert(SupportVersioningType source) {
		return source.getValue();
	}
}
