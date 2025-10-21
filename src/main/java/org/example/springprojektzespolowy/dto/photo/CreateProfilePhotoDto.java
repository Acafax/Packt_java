package org.example.springprojektzespolowy.dto.photo;

import com.google.firebase.database.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateProfilePhotoDto(String photoType, MultipartFile file) {
}
