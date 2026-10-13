package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum ActionType implements DatabaseEnum<Short> {

    None((short) 0),
    CreatePlan((short) 1),
    UpdatePlan((short) 2);

    private final Short value;

    ActionType(Short value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, ActionType> map = EnumUtils.getEnumValueMap(ActionType.class);

    public static ActionType of(Short i) {
        return map.get(i);
    }

}
