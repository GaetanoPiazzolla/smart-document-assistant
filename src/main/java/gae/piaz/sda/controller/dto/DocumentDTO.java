package gae.piaz.sda.controller.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentDTO {
    @NotNull
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String type;
    private Long size;
    private Long uploadedAt;
    private List<String> vectorStoreUUIDs;
}
