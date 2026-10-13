package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.CurrencyCodeType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToCurrencyCodeTypeConverter implements Converter<String, CurrencyCodeType> {

    @Override
    public CurrencyCodeType convert(@NotNull String source) {
        return CurrencyCodeType.of(source);
    }
}
