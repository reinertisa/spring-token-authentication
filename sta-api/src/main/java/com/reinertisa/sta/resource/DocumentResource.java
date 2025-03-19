package com.reinertisa.sta.resource;

import com.reinertisa.sta.domain.Response;
import com.reinertisa.sta.dto.Document;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.dto.api.IDocument;
import com.reinertisa.sta.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
    public ResponseEntity<Response> saveDocuments(@AuthenticationPrincipal User user,
                                                  @RequestParam("files") List<MultipartFile> documents,
                                                  HttpServletRequest request) {
        Collection<Document> newDocuments = documentService.saveDocuments(user.getUserId(), documents);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(request, Map.of("documents", newDocuments),
                        "Document(s) uploaded.", HttpStatus.CREATED));
    }

    @GetMapping("")
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<IDocument> documents = documentService.getDocuments(page, size);
        return ResponseEntity.ok().body(getResponse(request, Map.of("documents", documents),
                "Document(s) retrieved.", HttpStatus.OK));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                    @RequestParam(value = "base", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @RequestParam(value = "name", defaultValue = "") String name) {
        Page<IDocument> documents = documentService.getDocuments(page, size, name);
        return ResponseEntity.ok().body(getResponse(request, Map.of("documents", documents), "Document(s) retrieved", HttpStatus.OK));
    }

}
