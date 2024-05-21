package gae.piaz.sda.controller.dto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DocumentDTO {

    private Integer id;

    private String name;

    private String type;

    private Long size;

    private Long uploadedAt;

    private List<String> vectorStoreUUIDs;

}
