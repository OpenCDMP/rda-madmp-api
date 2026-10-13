package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.ActionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ShortToActionTypeConverter implements Converter<Integer, ActionType> {
	@Override
	public ActionType convert(Integer source) {
		return ActionType.of(source.shortValue());
	}
}
