package org.example.springprojektzespolowy.dto.documents;

import java.math.BigDecimal;

public record DocumentDtoWithFile(Long id, String documentName, String path, String fileType, byte[] file) {
}
