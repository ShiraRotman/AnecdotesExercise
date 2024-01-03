package rotman.shira.anecdotes.data;

import javax.validation.constraints.NotBlank;

public class EvidenceFormat {
    @NotBlank
    private String resultFieldName;
    @NotBlank
    private String resultFieldFormat;

    public String getResultFieldName() {
        return resultFieldName;
    }

    public String getResultFieldFormat() {
        return resultFieldFormat;
    }
}
