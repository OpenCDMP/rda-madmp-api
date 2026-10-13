package gr.cite.rda.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.model.rdadmp.DmpRootModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "DMPListResponseBody")
@JsonPropertyOrder({ "total_count", "items" })
public class QueryResult {

    public QueryResult() {
    }

    public QueryResult(List<DmpRootModel> items, Integer count) {
        this.items = items;
        this.count = count;
    }

    public QueryResult(DmpRootModel item) {
        this.items = List.of(item);
        this.count = 1;
    }

    public QueryResult(List<DmpRootModel> items) {
        this.items = items;
        if (items != null)
            this.count = items.size();
        else
            this.count = 0;
    }

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DmpRootModel> items;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("total_count")
    private Integer count;

    public List<DmpRootModel> getItems() {
        return items;
    }

    public void setItems(List<DmpRootModel> items) {
        this.items = items;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }



}
