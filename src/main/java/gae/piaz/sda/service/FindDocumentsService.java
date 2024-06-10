package gae.piaz.sda.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import gae.piaz.sda.controller.dto.DocumentDTO;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindDocumentsService
        implements Function<FindDocumentsService.Request, FindDocumentsService.Response> {

    @Autowired private DocumentService documentService;

    /** Document Function request. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Find Documents Function Request.")
    public record Request(
            @JsonProperty(required = false, value = "name")
                    @JsonPropertyDescription("The name of the document. E.G: 'RAG with Spring'")
                    String name,
            @JsonProperty(required = false, value = "type")
                    @JsonPropertyDescription("The type of the document. E.G: 'pdf'")
                    String type,
            @JsonProperty(required = false, value = "greater_than")
                    @JsonPropertyDescription("The size of the document in bytes. E.G: 1024")
                    Long greaterThan,
            @JsonProperty(required = false, value = "smaller_than")
                    @JsonPropertyDescription("The size of the document in bytes. E.G: 1024")
                    Long smallerThan) {}

    /** Document Function response. */
    public record Response(Integer occurrences) {}

    @Override
    public Response apply(Request request) {
        try {
            List<DocumentDTO> documents = documentService.getAll();

            if (request.name() != null) {
                documents =
                        documents.stream()
                                .filter(
                                        document ->
                                                document.getName().equalsIgnoreCase(request.name()))
                                .toList();
            }
            if (request.type() != null) {
                documents =
                        documents.stream()
                                .filter(
                                        document ->
                                                document.getType().equalsIgnoreCase(request.type()))
                                .toList();
            }
            if (request.greaterThan() != null) {
                documents =
                        documents.stream()
                                .filter(document -> document.getSize() > (request.greaterThan()))
                                .toList();
            }
            if (request.smallerThan() != null) {
                documents =
                        documents.stream()
                                .filter(document -> document.getSize() < (request.smallerThan()))
                                .toList();
            }

            return new Response(documents.size());
        } catch (Exception e) {
            return new Response(0);
        }
    }
}
