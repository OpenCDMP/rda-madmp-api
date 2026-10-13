package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.DataAccessType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class DataAccessTypeToStringConverter implements Converter<DataAccessType, String> {
	@Override
	public String convert(DataAccessType source) {
		return source.getValue();
	}
}
