package com.reinertisa.sta.service.impl;

import com.reinertisa.sta.dto.Document;
import com.reinertisa.sta.dto.api.IDocument;
import com.reinertisa.sta.entity.DocumentEntity;
import com.reinertisa.sta.entity.UserEntity;
import com.reinertisa.sta.exception.ApiException;
import com.reinertisa.sta.repository.DocumentRepository;
import com.reinertisa.sta.repository.UserRepository;
import com.reinertisa.sta.service.DocumentService;
import com.reinertisa.sta.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.reinertisa.sta.constant.Constants.FILE_STORAGE;
import static com.reinertisa.sta.utils.DocumentUtils.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return documentRepository.findDocumentsByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Collection<Document> saveDocuments(String userId, List<MultipartFile> documents) {
        List<Document> newDocuments = new ArrayList<>();
        UserEntity userEntity = userRepository.findUserByUserId(userId).get();
        Path storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        try {
            for (MultipartFile document : documents) {
                String filename = StringUtils.cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if ("..".contains(filename)) {
                    throw new ApiException(String.format("Invalid file name: %s", filename));
                }
                DocumentEntity documentEntity = DocumentEntity.builder()
                        .documentId(UUID.randomUUID().toString())
                        .name(filename)
                        .owner(userEntity)
                        .extension(getExtension(filename))
                        .uri(getDocumentUri(filename))
                        .formattedSize(byteCountToDisplaySize(document.getSize()))
                        .icon(setIcon(getExtension(filename)))
                        .build();

                DocumentEntity saveDocument = documentRepository.save(documentEntity);
                Files.copy(document.getInputStream(), storage.resolve(filename), REPLACE_EXISTING);
                Document newDocument = fromDocumentEntity(
                        saveDocument,
                        userService.getUserById(saveDocument.getCreatedBy()),
                        userService.getUserById(saveDocument.getUpdatedBy()));
                newDocuments.add(newDocument);
            }
            return newDocuments;
        } catch (Exception ex) {
            throw new ApiException("Unable to save documents");
        }
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
