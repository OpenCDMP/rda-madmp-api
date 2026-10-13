package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum FundingStatusType implements DatabaseEnum<String> {

    PLANNED("planned"),
    APPLIED("applied"),
    GRANTED("granted"),
    REJECTED("rejected");
    private final String value;

    FundingStatusType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return this.value;
    }

    private static final Map<String, FundingStatusType> map = EnumUtils.getEnumValueMap(FundingStatusType.class);

    public static FundingStatusType of(String i) {
        return map.get(i);
    }

}
