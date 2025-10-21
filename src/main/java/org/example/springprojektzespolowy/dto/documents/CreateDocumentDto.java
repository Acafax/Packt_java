package org.example.springprojektzespolowy.dto.documents;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateDocumentDto(String name, String path, String fileType , MultipartFile file) {
}
