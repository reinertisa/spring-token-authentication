package com.reinertisa.sta.service.impl;

import com.reinertisa.sta.dto.Document;
import com.reinertisa.sta.dto.api.IDocument;
import com.reinertisa.sta.repository.DocumentRepository;
import com.reinertisa.sta.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return null;
    }

    @Override
    public Collection<Document> saveDocuments(String userId, List<MultipartFile> documents) {
        return List.of();
    }

    @Override
    public IDocument updateDocument(String documentId, String name, String description) {
        return null;
    }

    @Override
    public void deleteDocument(String documentId) {

    }

    @Override
    public IDocument getDocumentByDocumentId(String documentId) {
        return null;
    }

    @Override
    public Resource getResource(String documentName) {
        return null;
    }
}
