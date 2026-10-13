package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.PersonalDataType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class PersonalDataTypeToStringConverter implements Converter<PersonalDataType, String> {
	@Override
	public String convert(PersonalDataType source) {
		return source.getValue();
	}
}
