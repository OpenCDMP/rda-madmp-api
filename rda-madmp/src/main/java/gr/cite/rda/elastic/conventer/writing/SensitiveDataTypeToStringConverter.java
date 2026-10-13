package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.SensitiveDataType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class SensitiveDataTypeToStringConverter implements Converter<SensitiveDataType, String> {
	@Override
	public String convert(SensitiveDataType source) {
		return source.getValue();
	}
}
