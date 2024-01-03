package rotman.shira.anecdotes.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import rotman.shira.anecdotes.utils.TreeJsonDeserializer;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Evidence {
    @JsonDeserialize(using = TreeJsonDeserializer.class)
    @NotNull
    private JsonNode evidencePayload;
    @NotNull @Valid
    private EvidenceFormat[] evidenceFormats;

    public JsonNode getEvidencePayload() {
        return evidencePayload;
    }

    public EvidenceFormat[] getEvidenceFormats() {
        return evidenceFormats;
    }
}
