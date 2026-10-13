package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class StatusToShortConverter implements Converter<Status, Short> {
	@Override
	public Short convert(Status source) {
		return source.getValue();
	}
}

