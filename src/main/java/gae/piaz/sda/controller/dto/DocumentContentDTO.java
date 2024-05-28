package gae.piaz.sda.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentContentDTO extends DocumentDTO {
    private byte[] content;
}
