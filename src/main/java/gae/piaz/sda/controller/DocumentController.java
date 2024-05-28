package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.DocumentContentDTO;
import gae.piaz.sda.controller.dto.DocumentDTO;
import gae.piaz.sda.controller.dto.DocumentTextDTO;
import gae.piaz.sda.service.DocumentService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("api/v1/document")
public class DocumentController {

    @Autowired private DocumentService documentService;

    /** Receive a file and store it in the database. */
    @PostMapping("/file")
    public ResponseEntity<DocumentDTO> uploadFile(
            @RequestParam("file") MultipartFile file, @RequestParam("name") String name)
            throws IOException {
        return ResponseEntity.ok(documentService.uploadFile(file, name));
    }

    /** Receive a new document directly as text content. */
    @PostMapping("/text")
    public ResponseEntity<DocumentDTO> uploadText(@RequestBody DocumentTextDTO documentTextDTO) {
        return ResponseEntity.ok(documentService.uploadText(documentTextDTO));
    }

    /** Get the document by its id. */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentContentDTO> get(@PathVariable Integer id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    /** Download the document by its id. */
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Integer id) {
        MediaType contentType = MediaType.TEXT_PLAIN;
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(documentService.download(id)));
    }

    /** Delete the document and related vector store documents. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        documentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentDTO>> getAll() {
        return ResponseEntity.ok(documentService.getAll());
    }
}
