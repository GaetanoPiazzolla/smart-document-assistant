package gae.piaz.sda.repository;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "document_vector_store")
@Data
public class DocumentVectorStoreEntity {

    @Id
    @Column(name = "vector_store_id")
    private String vectorStoreId;

    @Id
    @Column(name = "document_id")
    private Integer documentId;
}
