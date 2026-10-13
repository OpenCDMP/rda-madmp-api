package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.CurrencyCodeType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class CurrencyCodeTypeToStringConverter implements Converter<CurrencyCodeType, String> {
	@Override
	public String convert(CurrencyCodeType source) {
		return source.getValue();
	}
}
