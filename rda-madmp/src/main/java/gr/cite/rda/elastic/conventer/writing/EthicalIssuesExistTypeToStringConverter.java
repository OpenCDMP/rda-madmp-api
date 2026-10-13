package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.EthicalIssuesExistType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EthicalIssuesExistTypeToStringConverter implements Converter<EthicalIssuesExistType, String> {
	@Override
	public String convert(EthicalIssuesExistType source) {
		return source.getValue();
	}
}
