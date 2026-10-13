package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.DataAccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToDataAccessTypeConverter implements Converter<String, DataAccessType> {

    @Override
    public DataAccessType convert(@NotNull String source) {
        return DataAccessType.of(source);
    }
}
