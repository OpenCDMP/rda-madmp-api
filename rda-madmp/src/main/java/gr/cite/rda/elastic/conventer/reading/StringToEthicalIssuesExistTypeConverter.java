package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.EthicalIssuesExistType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToEthicalIssuesExistTypeConverter implements Converter<String, EthicalIssuesExistType> {

    @Override
    public EthicalIssuesExistType convert(@NotNull String source) {
        return EthicalIssuesExistType.of(source);
    }
}
