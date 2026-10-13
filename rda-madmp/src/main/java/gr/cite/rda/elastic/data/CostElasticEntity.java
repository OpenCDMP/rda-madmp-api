package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.CurrencyCodeType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class CostElasticEntity {

    @JsonProperty("currency_code")
    @Field(value = CostElasticEntity._currencyCode, type = FieldType.Keyword)
    private CurrencyCodeType currencyCode;
    public static final String _currencyCode = "currencyCode";

    @Field(value = CostElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @Field(value = CostElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    @Field(value = CostElasticEntity._value, type = FieldType.Double)
    private Double value;
    public static final String _value = "value";

    public CurrencyCodeType getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(CurrencyCodeType currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}