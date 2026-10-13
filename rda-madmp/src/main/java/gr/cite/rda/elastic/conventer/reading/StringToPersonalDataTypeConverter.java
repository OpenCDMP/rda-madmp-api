package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.PersonalDataType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToPersonalDataTypeConverter implements Converter<String, PersonalDataType> {

    @Override
    public PersonalDataType convert(@NotNull String source) {
        return PersonalDataType.of(source);
    }
}
