package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum DataAccessType implements DatabaseEnum<String> {

    OPEN("open"),
    RESTRICTED("restricted"),
    SHARED("shared"),
    CLOSED("closed");
    private final String value;

    DataAccessType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return this.value;
    }

    private static final Map<String, DataAccessType> map = EnumUtils.getEnumValueMap(DataAccessType.class);

    public static DataAccessType of(String i) {
        return map.get(i);
    }

}
