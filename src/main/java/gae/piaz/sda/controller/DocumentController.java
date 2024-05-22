package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.DocumentDTO;
import gae.piaz.sda.controller.dto.DocumentTextDTO;
import gae.piaz.sda.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * Receive a file and store it in the database.
     * TODO can be async
     */
    @PostMapping("/file")
    public ResponseEntity<DocumentDTO> uploadFile(
            @RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        return ResponseEntity.ok(documentService.uploadFile(file, name));
    }

    /**
     * Receive a new document directly as text content.
     */
    @PostMapping("/text")
    public ResponseEntity<DocumentDTO> uploadText(@RequestBody DocumentTextDTO documentTextDTO) {
        return ResponseEntity.ok(documentService.uploadText(documentTextDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentDTO>> getAll() {
        return ResponseEntity.ok(documentService.getAll());
    }
}
