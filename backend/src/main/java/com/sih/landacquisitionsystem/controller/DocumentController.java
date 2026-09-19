package com.sih.landacquisitionsystem.controller;

import com.sih.landacquisitionsystem.model.Document;
import com.sih.landacquisitionsystem.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Document createDocument(@RequestBody Document document) {
        return documentService.createDocument(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document documentDetails) {
        return documentService.getDocumentById(id)
                .map(document -> {
                    document.setFileName(documentDetails.getFileName());
                    document.setFileType(documentDetails.getFileType());
                    document.setDocumentType(documentDetails.getDocumentType());
                    document.setUrl(documentDetails.getUrl());
                    document.setAcquisitionCase(documentDetails.getAcquisitionCase());
                    document.setUploadedBy(documentDetails.getUploadedBy());
                    return ResponseEntity.ok(documentService.updateDocument(document));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(document -> {
                    documentService.deleteDocument(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}