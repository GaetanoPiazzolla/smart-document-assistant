package gae.piaz.sda.service;

import gae.piaz.sda.controller.dto.DocumentContentDTO;
import gae.piaz.sda.controller.dto.DocumentDTO;
import gae.piaz.sda.controller.dto.DocumentTextDTO;
import gae.piaz.sda.repository.DocumentEntity;
import gae.piaz.sda.repository.DocumentRepository;
import gae.piaz.sda.repository.DocumentVectorStoreEntity;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    @Autowired private DocumentRepository documentRepository;

    @Autowired private VectorStore vectorStore;

    public DocumentDTO uploadFile(MultipartFile file, String name) throws IOException {

        // TODO get those parameters from the client or the configuration
        int defaultChunkSize = 30;
        int minChunkSizeChars = 20;
        int minChunkLenghtToEmbed = 1;
        int maxNumChunks = 10000;
        boolean keepSeparator = true;

        List<Document> vectorStoreDocuments =
                new TokenTextSplitter(
                                defaultChunkSize,
                                minChunkSizeChars,
                                minChunkLenghtToEmbed,
                                maxNumChunks,
                                keepSeparator)
                        .apply(new TextReader(file.getResource()).get());

        vectorStore.accept(vectorStoreDocuments);

        // TODO duplicated code
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setSize(file.getSize());
        documentEntity.setType(file.getContentType());
        documentEntity.setUploadedAt(System.currentTimeMillis());
        documentEntity.setContent(file.getBytes());
        documentEntity.setDocumentVectorStoreEntities(
                vectorStoreDocuments.stream()
                        .map(
                                vectorStoreDocument -> {
                                    DocumentVectorStoreEntity documentVectorStoreEntity =
                                            new DocumentVectorStoreEntity();
                                    documentVectorStoreEntity.setDocumentId(documentEntity.getId());
                                    documentVectorStoreEntity.setVectorStoreId(
                                            vectorStoreDocument.getId());
                                    return documentVectorStoreEntity;
                                })
                        .collect(Collectors.toList()));
        documentRepository.save(documentEntity);

        return mapDocumentToDTO(documentEntity, false);
    }

    public DocumentDTO uploadText(DocumentTextDTO documentTextDTO) {

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("name", documentTextDTO.getName());
        customMetadata.put("charset", "UTF-8");

        // TODO get those parameters from the client or the configuration
        int defaultChunkSize = 30;
        int minChunkSizeChars = 20;
        int minChunkLenghtToEmbed = 1;
        int maxNumChunks = 10000;
        boolean keepSeparator = true;

        List<Document> vectorStoreDocuments =
                new TokenTextSplitter(
                                defaultChunkSize,
                                minChunkSizeChars,
                                minChunkLenghtToEmbed,
                                maxNumChunks,
                                keepSeparator)
                        .apply(List.of(new Document(documentTextDTO.getContent(), customMetadata)));

        vectorStore.accept(vectorStoreDocuments);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(documentTextDTO.getName());
        documentEntity.setSize((long) documentTextDTO.getContent().length());
        documentEntity.setType("text/plain");
        documentEntity.setContent(documentTextDTO.getContent().getBytes());
        documentEntity.setUploadedAt(System.currentTimeMillis());
        documentEntity.setDocumentVectorStoreEntities(
                vectorStoreDocuments.stream()
                        .map(
                                vectorStoreDocument -> {
                                    DocumentVectorStoreEntity documentVectorStoreEntity =
                                            new DocumentVectorStoreEntity();
                                    documentVectorStoreEntity.setDocumentId(documentEntity.getId());
                                    documentVectorStoreEntity.setVectorStoreId(
                                            vectorStoreDocument.getId());
                                    return documentVectorStoreEntity;
                                })
                        .collect(Collectors.toList()));
        documentRepository.save(documentEntity);

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName(documentTextDTO.getName());
        documentDTO.setSize((long) documentTextDTO.getContent().length());
        documentDTO.setType("text/plain");
        documentDTO.setUploadedAt(System.currentTimeMillis());

        return documentDTO;
    }

    public List<DocumentDTO> getAll() {
        return documentRepository.findAll().stream()
                .map(documentEntity -> mapDocumentToDTO(documentEntity, false))
                .collect(Collectors.toList());
    }

    public DocumentContentDTO getDocumentById(Integer id) {
        DocumentEntity documentEntity = documentRepository.findById(id).orElseThrow();
        DocumentContentDTO documentDTO =
                (DocumentContentDTO) mapDocumentToDTO(documentEntity, true);
        documentDTO.setContent(documentEntity.getContent());
        return documentDTO;
    }

    public void delete(Integer id) {
        DocumentEntity documentEntity = documentRepository.findById(id).orElseThrow();
        List<String> vectorStoreIds =
                documentEntity.getDocumentVectorStoreEntities().stream()
                        .map(DocumentVectorStoreEntity::getVectorStoreId)
                        .collect(Collectors.toList());
        vectorStore.delete(vectorStoreIds);
        documentRepository.deleteById(id);
    }

    private DocumentDTO mapDocumentToDTO(DocumentEntity documentEntity, boolean includeContent) {
        DocumentDTO documentDTO;
        if (includeContent) {
            DocumentContentDTO documentContentDTO = new DocumentContentDTO();
            documentContentDTO.setContent(documentEntity.getContent());
            documentDTO = documentContentDTO;
        } else {
            documentDTO = new DocumentDTO();
        }
        documentDTO.setId(documentEntity.getId());
        documentDTO.setName(documentEntity.getName());
        documentDTO.setSize(documentEntity.getSize());
        documentDTO.setType(documentEntity.getType());
        documentDTO.setUploadedAt(documentEntity.getUploadedAt());
        return documentDTO;
    }

    public InputStream download(Integer id) {
        return documentRepository
                .findById(id)
                .map(documentEntity -> new ByteArrayInputStream(documentEntity.getContent()))
                .orElseThrow();
    }
}
