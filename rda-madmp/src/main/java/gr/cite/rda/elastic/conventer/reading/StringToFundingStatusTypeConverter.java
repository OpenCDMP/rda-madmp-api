package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.FundingStatusType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToFundingStatusTypeConverter implements Converter<String, FundingStatusType> {

    @Override
    public FundingStatusType convert(@NotNull String source) {
        return FundingStatusType.of(source);
    }
}
