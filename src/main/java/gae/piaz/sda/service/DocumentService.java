package gae.piaz.sda.service;

import gae.piaz.sda.controller.dto.DocumentDTO;
import gae.piaz.sda.repository.DocumentEntity;
import gae.piaz.sda.repository.DocumentRepository;
import gae.piaz.sda.repository.DocumentVectorStoreEntity;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private VectorStore vectorStore;

    public DocumentDTO uploadFile(MultipartFile file, String name) {

        List<Document> vectorStoreDocuments =
                new TokenTextSplitter(30, 20, 1, 10000, true)
                        .apply(new TextReader(file.getResource()).get());

        vectorStore.accept(vectorStoreDocuments);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setSize(file.getSize());
        documentEntity.setType(file.getContentType());
        documentEntity.setUploadedAt(System.currentTimeMillis());
        documentEntity.setDocumentVectorStoreEntities(
                vectorStoreDocuments.stream().map(vectorStoreDocument -> {
                    DocumentVectorStoreEntity documentVectorStoreEntity = new DocumentVectorStoreEntity();
                    documentVectorStoreEntity.setDocumentId(documentEntity.getId());
                    documentVectorStoreEntity.setVectorStoreId(vectorStoreDocument.getId());
                    return documentVectorStoreEntity;
                }).collect(Collectors.toList()));
        documentRepository.save(documentEntity);

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName(name);
        documentDTO.setSize(file.getSize());
        documentDTO.setType(file.getContentType());
        documentDTO.setUploadedAt(System.currentTimeMillis());
        documentDTO.setVectorStoreUUIDs(
                vectorStoreDocuments.stream().map(Document::getId).collect(Collectors.toList()));

        return documentDTO;
    }

    public List<DocumentDTO> getAll() {
        return documentRepository.findAll().stream().map(documentEntity -> {
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setId(documentEntity.getId());
            documentDTO.setName(documentEntity.getName());
            documentDTO.setSize(documentEntity.getSize());
            documentDTO.setType(documentEntity.getType());
            documentDTO.setUploadedAt(documentEntity.getUploadedAt());
            documentDTO.setVectorStoreUUIDs(
                    documentEntity.getDocumentVectorStoreEntities().stream().map(
                            DocumentVectorStoreEntity::getVectorStoreId).collect(Collectors.toList()));
            return documentDTO;
        }).collect(Collectors.toList());
    }

}
