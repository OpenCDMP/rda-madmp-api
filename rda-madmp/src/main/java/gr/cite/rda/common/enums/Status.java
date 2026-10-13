package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum Status implements DatabaseEnum<Short> {

    NotFinalized((short) 0), Finalized((short) 1);

    private final Short value;

    Status(Short value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, Status> map = EnumUtils.getEnumValueMap(Status.class);

    public static Status of(Short i) {
        return map.get(i);
    }


}
