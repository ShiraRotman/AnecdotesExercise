package rotman.shira.anecdotes.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import rotman.shira.anecdotes.data.Evidence;
import rotman.shira.anecdotes.data.EvidenceFormat;

import javax.validation.Valid;

@RestController
@RequestMapping("/collect")
public class EvidenceController {
    private ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String collectEvidence(@RequestBody @Valid Evidence evidence) {
        JsonNode evidenceDataNode = evidence.getEvidencePayload().get("evidence_data");
        if ((evidenceDataNode == null) || (!evidenceDataNode.isArray()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing evidence data!!!");
        ArrayNode evidenceArrayNode = (ArrayNode) evidenceDataNode;
        ArrayNode resultArrayNode = objectMapper.createArrayNode();

        for (JsonNode evidenceElementNode : evidenceArrayNode) {
            if (!evidenceElementNode.isObject())
                continue;
            ObjectNode evidenceObjectNode = (ObjectNode) evidenceElementNode;
            ObjectNode resultObjectNode = objectMapper.createObjectNode();

            for (EvidenceFormat evidenceFormat : evidence.getEvidenceFormats()) {
                String resultFieldFormat = evidenceFormat.getResultFieldFormat();
                StringBuilder fieldValueBuilder = new StringBuilder();
                int nextIndex = 0, startIndex = resultFieldFormat.indexOf("${");

                while ((startIndex > -1) && (nextIndex < resultFieldFormat.length())) {
                    int endIndex = resultFieldFormat.indexOf('}', startIndex);
                    if (endIndex == -1)
                        break;
                    fieldValueBuilder.append(resultFieldFormat.substring(nextIndex, startIndex));
                    String[] fieldPathParts = resultFieldFormat.substring(startIndex + 2, endIndex).split("\\.");

                    if (fieldPathParts.length > 0) {
                        String pathValue = getPathValue(evidenceObjectNode, fieldPathParts);
                        fieldValueBuilder.append(pathValue);
                    }
                    else fieldValueBuilder.append("${}");
                    nextIndex = endIndex + 1;
                    if (nextIndex < resultFieldFormat.length())
                        startIndex = resultFieldFormat.indexOf("${", nextIndex);
                }

                if (startIndex == -1)
                    fieldValueBuilder.append(resultFieldFormat.substring(nextIndex));
                resultObjectNode.put(evidenceFormat.getResultFieldName(), fieldValueBuilder.toString());
            }
            resultArrayNode.add(resultObjectNode);
        }

        try {
            return objectMapper.writeValueAsString(resultArrayNode);
        }
        catch (JsonProcessingException jsonProcessingException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate result!!!");
        }
    }

    private static String getPathValue(ObjectNode evidenceObjectNode, String[] fieldPathParts) {
        String pathValue = null;
        JsonNode tempNode = evidenceObjectNode;
        int index;

        for (index = 0; index < fieldPathParts.length - 1; index++) {
            tempNode = tempNode.get(fieldPathParts[index]);
            if ((tempNode == null) || (!tempNode.isObject())) {
                break;
            }
        }

        if (index == fieldPathParts.length - 1) {
            tempNode = tempNode.get(fieldPathParts[fieldPathParts.length - 1]);
            pathValue = (tempNode == null) || (!tempNode.isValueNode()) ? null : tempNode.asText();
        }
        return pathValue;
    }
}
