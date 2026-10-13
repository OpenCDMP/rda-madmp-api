package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.CertifiedWithType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToCertifiedWithTypeConverter implements Converter<String, CertifiedWithType> {

    @Override
    public CertifiedWithType convert(@NotNull String source) {
        return CertifiedWithType.of(source);
    }
}
