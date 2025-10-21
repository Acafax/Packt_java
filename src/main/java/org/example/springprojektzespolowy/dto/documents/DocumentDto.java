package org.example.springprojektzespolowy.dto.documents;

import java.math.BigDecimal;

public record DocumentDto(Long id, String documentName, String path, String fileType) {
}
