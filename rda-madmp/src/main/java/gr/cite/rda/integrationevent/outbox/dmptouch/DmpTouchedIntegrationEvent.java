package gr.cite.rda.integrationevent.outbox.dmptouch;

import gr.cite.rda.common.enums.ActionType;
import gr.cite.rda.integrationevent.TrackedEvent;
import gr.cite.rda.model.rdadmp.DmpRootModel;

import java.util.UUID;

public class DmpTouchedIntegrationEvent extends TrackedEvent {

    private UUID dmpId;

    private DmpRootModel data;

    private UUID submitterId;

    private ActionType actionType;

    private String sourceCode;

    private String externalId;

    private String externalIdType;

    public UUID getDmpId() {
        return dmpId;
    }

    public void setDmpId(UUID dmpId) {
        this.dmpId = dmpId;
    }

    public DmpRootModel getData() {
        return data;
    }

    public void setData(DmpRootModel data) {
        this.data = data;
    }

    public UUID getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(UUID submitterId) {
        this.submitterId = submitterId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }
}
