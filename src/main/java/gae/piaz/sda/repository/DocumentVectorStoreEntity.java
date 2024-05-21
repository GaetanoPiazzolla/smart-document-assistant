package gae.piaz.sda.repository;

import jakarta.persistence.*;

import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "document_vector_store")
@Data
public class DocumentVectorStoreEntity {

    @Column(name = "vector_store_id")
    private UUID vectorStoreId;

    @Column(name = "document_id")
    private Long documentId;

}