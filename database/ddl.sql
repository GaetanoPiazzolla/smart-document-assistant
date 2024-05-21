CREATE TABLE document (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    size BIGINT,
    uploaded_at BIGINT
);

CREATE TABLE document_vector_store (
    document_id BIGINT NOT NULL,
    vector_store_id UUID NOT NULL,
    FOREIGN KEY (document_id) REFERENCES document(id)
    FOREIGN KEY (vector_store_id) REFERENCES vector_store(id)
);