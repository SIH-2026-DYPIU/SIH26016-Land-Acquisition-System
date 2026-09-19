package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Document;
import com.sih.landacquisitionsystem.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public List<Document> getDocumentsByAcquisitionCase(Long caseId) {
        return documentRepository.findAll().stream()
                .filter(d -> d.getAcquisitionCase().getId().equals(caseId))
                .toList();
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public Document updateDocument(Document document) {
        return documentRepository.save(document);
    }

    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}