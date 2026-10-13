package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.FundingStatusType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class FundingStatusTypeToStringConverter implements Converter<FundingStatusType, String> {
	@Override
	public String convert(FundingStatusType source) {
		return source.getValue();
	}
}
