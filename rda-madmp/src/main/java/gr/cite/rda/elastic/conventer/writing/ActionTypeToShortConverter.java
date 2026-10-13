package gr.cite.rda.elastic.conventer.writing;

import gr.cite.rda.common.enums.ActionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ActionTypeToShortConverter implements Converter<ActionType, Short> {
	@Override
	public Short convert(ActionType source) {
		return source.getValue();
	}
}
