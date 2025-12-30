package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.dto.documents.CreateDocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDtoWithFile;
import org.example.springprojektzespolowy.dto.documents.UpdateDocumentDto;
import org.example.springprojektzespolowy.services.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@Controller
@RequestMapping("/doc")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @GetMapping("/{groupId}")
    public ResponseEntity<Set<DocumentDto>> getDocumentsByGroupId(@PathVariable Long groupId){
        Set<DocumentDto> allDocuments = documentService.getAllDocuments(groupId);
        return ResponseEntity.ok(allDocuments);
    }

    @GetMapping("/id/{docId}")
    public ResponseEntity<DocumentDtoWithFile> getDocument(@PathVariable Long docId){
        DocumentDtoWithFile documentById = documentService.getDocumentWithFileById(docId);
        return ResponseEntity.ok(documentById);
    }

    @GetMapping("/{docName}/{groupId}")
    public ResponseEntity<DocumentDtoWithFile> getDocumentFromGroupDocByName(@PathVariable String docName, @PathVariable Long groupId){
        DocumentDtoWithFile documentsInGroupByName = documentService.getDocumentsInGroupDocByName(groupId, docName);
        return ResponseEntity.ok(documentsInGroupByName);
    }

    @PostMapping(value = "/create/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> createDocument (@ModelAttribute CreateDocumentDto document, @PathVariable Long groupId) throws IOException {
        DocumentDto documentsFromGroupByName = documentService.createDocument(document, groupId);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{groupId}")
                .buildAndExpand(groupId)
                .toUri();

        return ResponseEntity.created(uri).body(documentsFromGroupByName);
    }

    @PatchMapping(value = "/patch/{docId}/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> patchDocument(@ModelAttribute UpdateDocumentDto updateDocumentDto, @PathVariable Long docId, @PathVariable Long groupId){
        if (updateDocumentDto==null) throw new IllegalArgumentException("Update document data cannot be null");
        DocumentDto documentDto = documentService.patchDocument(updateDocumentDto, docId, groupId);
        return ResponseEntity.ok(documentDto);
    }

    @DeleteMapping("/{id}/{groupId}")
    public ResponseEntity<DocumentDto> deleteDocument(@PathVariable Long id, @PathVariable Long groupId){
        DocumentDto allDocuments = documentService.deleteDocument(id, groupId);
        return ResponseEntity.ok().body(allDocuments);
    }

}
