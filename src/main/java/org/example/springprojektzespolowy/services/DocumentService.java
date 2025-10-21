package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.example.springprojektzespolowy.dto.documents.CreateDocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDtoWithFile;
import org.example.springprojektzespolowy.dto.documents.UpdateDocumentDto;
import org.example.springprojektzespolowy.dto.mappers.DocumentDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.GroupDtoMapper;
import org.example.springprojektzespolowy.models.Document;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.repositories.DocumentsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class DocumentService {
    private final DocumentDtoMapper documentDtoMapper;
    private final DocumentsRepository documentsRepository;
    private final GroupService groupService;
    private final GroupDtoMapper groupDtoMapper;

    public DocumentService(DocumentDtoMapper ticketDtoMapper, DocumentsRepository ticketsRepository, GroupService groupService, GroupDtoMapper groupDtoMapper){
        this.documentDtoMapper = ticketDtoMapper;
        this.documentsRepository = ticketsRepository;
        this.groupService = groupService;
        this.groupDtoMapper = groupDtoMapper;
    }
    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public Set<DocumentDto> getAllDocuments(Long groupId){
        Set<Document> allByGroupId = documentsRepository.findAllByGroup_Id(groupId);
        System.out.println(allByGroupId.size());
        return documentDtoMapper.convert(allByGroupId);
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupMemberByDocument(authentication.name, #id)")
    public DocumentDtoWithFile getDocumentWithFileById(Long id){
        Document document = documentsRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return documentDtoMapper.convertWithFiles(document);
    }


    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public Set<DocumentDtoWithFile> getAllDocumentsWithFiles(Long groupId){
        Set<Document> allByGroupId = documentsRepository.findAllByGroup_Id(groupId);
        return documentDtoMapper.convertSetWithFiles(allByGroupId);
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupMemberByDocument(authentication.name, #id)")
    public DocumentDto getDocumentById(Long id){
        try {
            Document document = documentsRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            return documentDtoMapper.convert(document);
        }catch (EntityNotFoundException ex){
            throw new EntityNotFoundException(ex);
        }
    }
    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public DocumentDtoWithFile getDocumentsInGroupDocByName(Long groupId, String ticketName){
        Document ticketFromGroupByName = documentsRepository.getDocumentFromGroupByIdAndByDocName(groupId, ticketName);
        if (ticketFromGroupByName == null) throw new EntityNotFoundException();
        return documentDtoMapper.convertWithFiles(ticketFromGroupByName);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public DocumentDto createDocument(CreateDocumentDto documentDto, Long groupId)throws IOException {
        if (documentHasEmptyVariables(documentDto)) throw new NullPointerException();
        if (!isFileSizeIsValid(documentDto.file())) throw new FileSizeLimitExceededException("Plik przekracza dozwoloną wielkość (10MB)", documentDto.file().getSize(), 10);

        Group group = groupDtoMapper.convert(groupService.getGroupDTOById(groupId));

        Document document = new Document();
        try {
            document.setFile(documentDto.file().getBytes());
            document.setName(documentDto.name());
            document.setPath(documentDto.path());
            document.setFileType(documentDto.fileType());
            document.setGroup(group);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        documentsRepository.save(document);

        return documentDtoMapper.convert(document);
    }



    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public DocumentDto deleteDocument(Long id, Long groupId){
        Document document = documentsRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        documentsRepository.deleteById(id);

        return documentDtoMapper.convert(document);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #id)")
    public Set<DocumentDto> deleteDocumentsByGroupId(Long id){
        Set<Document> tickets = documentsRepository.deleteTicketsByGroup_Id(id);
        return documentDtoMapper.convert(tickets);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public DocumentDto updateWholeDocument(UpdateDocumentDto updateTicketDto, Long groupId){
         return documentsRepository.findById(updateTicketDto.id())
                .map(existingTicket ->{
                    existingTicket.setName(updateTicketDto.documentName());

                    documentsRepository.save(existingTicket);
                    return documentDtoMapper.convert(existingTicket);
                })
                 .orElseThrow(EntityNotFoundException::new);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public DocumentDto patchDocument(UpdateDocumentDto updateTicketDto, Long ticketId, Long groupId){
        return documentsRepository.findById(ticketId)
                .map(existingTicket ->{
                    if (updateTicketDto.documentName() != null) existingTicket.setName(updateTicketDto.documentName());
                    if (updateTicketDto.path() != null)existingTicket.setPath(updateTicketDto.path());
                    documentsRepository.save(existingTicket);
                    return documentDtoMapper.convert(existingTicket);
                })
                .orElseThrow(EntityNotFoundException::new);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException (AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak uprawnien do groupy");
    }

    private boolean documentHasEmptyVariables(CreateDocumentDto document){
        return Stream.of(
                document.name().isEmpty(),
                document.fileType().isEmpty(),
                document.path().isEmpty(),
                document.file() == null
        ).anyMatch(Boolean::valueOf);
    }

    private boolean isFileSizeIsValid(MultipartFile file){
        long size = file.getSize();
        return size/(1024.0 * 1024.0)<10;
    }

}
