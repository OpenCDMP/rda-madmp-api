package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.LanguageType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToLanguageTypeConverter implements Converter<String, LanguageType> {

    @Override
    public LanguageType convert(@NotNull String source) {
        return LanguageType.of(source);
    }
}
