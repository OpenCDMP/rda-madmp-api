package gr.cite.rda.data.conventers;

import com.fasterxml.jackson.annotation.JsonValue;

public interface DatabaseEnum<T> {
    @JsonValue
    T getValue();
}
