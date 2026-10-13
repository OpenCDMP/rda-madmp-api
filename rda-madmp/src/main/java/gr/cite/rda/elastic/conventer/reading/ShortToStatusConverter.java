package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ShortToStatusConverter implements Converter<Integer, Status> {
	@Override
	public Status convert(Integer source) {
		return Status.of(source.shortValue());
	}
}

