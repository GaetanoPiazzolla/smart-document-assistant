package gae.piaz.sda.repository;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "document")
@Data
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Long size;

    @Column(name = "uploaded_at")
    private Long uploadedAt;

    @Column(name = "content")
    private byte[] content;

    // TODO probably we can store directly the List of VectorStoreIds
    @OneToMany(mappedBy = "documentId", fetch = FetchType.LAZY)
    private List<DocumentVectorStoreEntity> documentVectorStoreEntities;
}
