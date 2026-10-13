package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum AccessType implements DatabaseEnum<Short> {

    Public((short) 0), Restricted((short) 1);

    private final Short value;

    AccessType(Short value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, AccessType> map = EnumUtils.getEnumValueMap(AccessType.class);

    public static AccessType of(Short i) {
        return map.get(i);
    }

}
