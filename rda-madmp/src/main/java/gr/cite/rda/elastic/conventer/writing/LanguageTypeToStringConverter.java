package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.LanguageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class LanguageTypeToStringConverter implements Converter<LanguageType, String> {
	@Override
	public String convert(LanguageType source) {
		return source.getValue();
	}
}
