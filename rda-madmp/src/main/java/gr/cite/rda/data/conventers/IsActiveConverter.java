package gr.cite.rda.data.conventers;

import gr.cite.rda.common.enums.IsActive;
import jakarta.persistence.Converter;

@Converter
public class IsActiveConverter extends DatabaseEnumConverter<IsActive, Short> {
    public IsActive of(Short i) {
        return IsActive.of(i);
    }
}
