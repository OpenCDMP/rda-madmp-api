package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.SupportVersioningType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToSupportVersioningTypeConverter implements Converter<String, SupportVersioningType> {

    @Override
    public SupportVersioningType convert(@NotNull String source) {
        return SupportVersioningType.of(source);
    }
}
