package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.GeoLocationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class GeoLocationTypeToStringConverter implements Converter<GeoLocationType, String> {
	@Override
	public String convert(GeoLocationType source) {
		return source.getValue();
	}
}
