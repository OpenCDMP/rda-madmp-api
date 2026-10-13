package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.CertifiedWithType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class CertifiedWithTypeToStringConverter implements Converter<CertifiedWithType, String> {
	@Override
	public String convert(CertifiedWithType source) {
		return source.getValue();
	}
}
