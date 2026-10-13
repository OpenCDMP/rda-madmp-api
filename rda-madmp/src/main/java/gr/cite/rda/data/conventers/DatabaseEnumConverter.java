package gr.cite.rda.data.conventers;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class DatabaseEnumConverter<EnumType extends DatabaseEnum<T>, T> implements AttributeConverter<EnumType, T> {
    protected abstract EnumType of(T dbData);
    
    @Override
    public T convertToDatabaseColumn(EnumType value) {
        if (value == null)  throw new IllegalArgumentException("value");
        return value.getValue();
    }

    @Override
    public EnumType convertToEntityAttribute(T dbData) {
        return this.of(dbData);
    }
}
