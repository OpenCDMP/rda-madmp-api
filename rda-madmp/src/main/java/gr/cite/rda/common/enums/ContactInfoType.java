package gr.cite.rda.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.rda.data.conventers.DatabaseEnum;

import java.util.Map;

public enum ContactInfoType implements DatabaseEnum<Short> {
	Email((short) 0),
	MobilePhone((short) 1),
	LandLinePhone((short) 2);
	private final Short value;

	ContactInfoType(Short value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public Short getValue() {
		return value;
	}

	private static final Map<Short, ContactInfoType> map = EnumUtils.getEnumValueMap(ContactInfoType.class);

	public static ContactInfoType of(Short i) {
		return map.get(i);
	}
}
