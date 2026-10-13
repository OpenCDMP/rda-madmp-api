package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.SensitiveDataType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToSensitiveDataTypeConverter implements Converter<String, SensitiveDataType> {

    @Override
    public SensitiveDataType convert(@NotNull String source) {
        return SensitiveDataType.of(source);
    }
}
