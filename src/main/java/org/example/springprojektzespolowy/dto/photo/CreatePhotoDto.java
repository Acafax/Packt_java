package org.example.springprojektzespolowy.dto.photo;

import org.springframework.web.multipart.MultipartFile;

public record CreatePhotoDto(String photoName, String photoType, MultipartFile file) {

}
