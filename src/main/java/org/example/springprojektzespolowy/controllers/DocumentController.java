package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.example.springprojektzespolowy.dto.documents.CreateDocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDtoWithFile;
import org.example.springprojektzespolowy.dto.documents.UpdateDocumentDto;
import org.example.springprojektzespolowy.services.DocumentService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        try {
            Set<DocumentDto> allDocuments = documentService.getAllDocuments(groupId);
            return ResponseEntity.ok(allDocuments);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/id/{docId}")
    public ResponseEntity<DocumentDtoWithFile> getDocument(@PathVariable Long docId){
        try {
            DocumentDtoWithFile documentById = documentService.getDocumentWithFileById(docId);
            return ResponseEntity.ok(documentById);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{docName}/{groupId}")
    public ResponseEntity<DocumentDtoWithFile> getDocumentFromGroupDocByName(@PathVariable String docName, @PathVariable Long groupId){
        try {
            DocumentDtoWithFile documentsInGroupByName = documentService.getDocumentsInGroupDocByName(groupId, docName);
            return ResponseEntity.ok(documentsInGroupByName);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/create/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> createDocument (@ModelAttribute CreateDocumentDto document, @PathVariable Long groupId){
        try {
            DocumentDto documentsFromGroupByName = documentService.createDocument(document, groupId);

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("{groupId}")
                    .buildAndExpand(groupId)
                    .toUri();

            return ResponseEntity.created(uri).body(documentsFromGroupByName);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (IllegalArgumentException | NullPointerException ex){
            return ResponseEntity.badRequest().build();
        }catch (FileSizeLimitExceededException ex){
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }catch (EntityExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping(value = "/patch/{docId}/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> patchDocument(@ModelAttribute UpdateDocumentDto updateDocumentDto, @PathVariable Long docId, @PathVariable Long groupId){
        try {
            if (updateDocumentDto==null) throw new IllegalArgumentException();
            DocumentDto documentDto = documentService.patchDocument(updateDocumentDto, docId,groupId);
            return ResponseEntity.ok(documentDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (HttpMessageNotReadableException | IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/{id}/{groupId}")
    public ResponseEntity<DocumentDto> deleteDocument(@PathVariable Long id, @PathVariable Long groupId){
        try {
            DocumentDto allDocuments = documentService.deleteDocument(id, groupId);
            return ResponseEntity.ok().body(allDocuments);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
