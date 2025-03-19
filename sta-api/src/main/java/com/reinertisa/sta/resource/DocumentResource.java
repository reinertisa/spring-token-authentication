package com.reinertisa.sta.resource;

import com.reinertisa.sta.domain.Response;
import com.reinertisa.sta.dto.Document;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.dto.api.IDocument;
import com.reinertisa.sta.dtorequest.UpdateDocRequest;
import com.reinertisa.sta.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.reinertisa.sta.utils.RequestUtils.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = { "/documents" })
public class DocumentResource {
    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('document:create') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> saveDocuments(@AuthenticationPrincipal User user,
                                                  @RequestParam("files") List<MultipartFile> documents,
                                                  HttpServletRequest request) {
        Collection<Document> newDocuments = documentService.saveDocuments(user.getUserId(), documents);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(request, Map.of("documents", newDocuments),
                        "Document(s) uploaded.", HttpStatus.CREATED));
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<IDocument> documents = documentService.getDocuments(page, size);
        return ResponseEntity.ok().body(getResponse(request, Map.of("documents", documents),
                "Document(s) retrieved.", HttpStatus.OK));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> searchDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                    @RequestParam(value = "base", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @RequestParam(value = "name", defaultValue = "") String name) {
        Page<IDocument> documents = documentService.getDocuments(page, size, name);
        return ResponseEntity.ok().body(getResponse(request, Map.of("documents", documents),
                "Document(s) retrieved", HttpStatus.OK));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getDocument(@AuthenticationPrincipal User user,
                                                @PathVariable("documentId") String documentId,
                                                HttpServletRequest request) {
        IDocument document = documentService.getDocumentByDocumentId(documentId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("document", document),
                "Document retrieved.", HttpStatus.OK));
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateDocument(@AuthenticationPrincipal User user,
                                                   @RequestBody UpdateDocRequest document,
                                                   HttpServletRequest request) {
        IDocument updateDocument = documentService.updateDocument(
                document.getDocumentId(), document.getName(), document.getDescription());
        return ResponseEntity.ok().body(getResponse(request, Map.of("document", updateDocument),
                "Document updated.", HttpStatus.OK));
    }

    @GetMapping("/download/{documentName}")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@AuthenticationPrincipal User user,
                                                     @PathVariable("documentName") String documentName,
                                                     HttpServletRequest request) throws IOException {
        Resource resource = documentService.getResource(documentName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", documentName);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;File-Name=%s", resource.getFilename()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
                .headers(httpHeaders).body(resource);
    }
}
