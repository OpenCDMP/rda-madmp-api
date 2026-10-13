package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.CurrencyCodeType;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ CostModel._currencyCode, CostModel._description, CostModel._title, CostModel._value})
@Schema(name = "The Cost Schema")
public class CostModel {

    @JsonProperty("currency_code")
    private CurrencyCodeType currencyCode;
    public static final String _currencyCode = "currencyCode";

    private String description;
    public static final String _description = "description";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

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