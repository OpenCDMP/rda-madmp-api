package gr.cite.rda.elastic.conventer.reading;

import gr.cite.rda.common.enums.GeoLocationType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToGeoLocationTypeConverter implements Converter<String, GeoLocationType> {

    @Override
    public GeoLocationType convert(@NotNull String source) {
        return GeoLocationType.of(source);
    }
}
