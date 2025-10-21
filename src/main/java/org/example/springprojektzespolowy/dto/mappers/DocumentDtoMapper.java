package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.documents.CreateDocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDtoWithFile;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.models.Document;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DocumentDtoMapper {

    public DocumentDto convert(Document document){
        return new DocumentDto(
                document.getId(),
                document.getName(),
                document.getPath(),
                document.getFileType()
        );
    }

    public DocumentDtoWithFile convertWithFiles(Document document){
        return new DocumentDtoWithFile(
                document.getId(),
                document.getName(),
                document.getPath(),
                document.getFileType(),
                document.getFile()
        );
    }

    public Set<DocumentDtoWithFile> convertSetWithFiles(Set<Document> documents){
        return documents.stream()
                .map(this::convertWithFiles)
                .collect(Collectors.toSet());
    }

    public Set<DocumentDto> convert(Set<Document> documents){
        return documents.stream()
                .map(this::convert)
                .collect(Collectors.toSet());
    }

    public Set<DocumentDto> convertExpDocument(Set<ExpensesDocument> expensesDocuments){
        return expensesDocuments.stream().map(expensesDocument -> {
            return new DocumentDto(
                    expensesDocument.getDocument().getId(),
                    expensesDocument.getDocument().getName(),
                    expensesDocument.getDocument().getPath(),
                    expensesDocument.getDocument().getFileType()
            );
        }).collect(Collectors.toSet());
    }


    public Set<Document> convert(Set<CreateDocumentDto> documents, Group group){
        return documents.stream().map(document -> {
            try {
                return new Document(
                        document.name(),
                        document.path(),
                        document.fileType(),
                        document.file().getBytes(),
                        group
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }



    public Document convert(DocumentDto documentDto){
        return new Document(
                documentDto.documentName(),
                documentDto.path(),
                documentDto.fileType()
        );
    }

}
