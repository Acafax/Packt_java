package org.example.springprojektzespolowy.dto.userDto;

import java.time.LocalDate;
import java.util.Date;

public record UserDto(Long id, String UId , String userName, String email, String country, LocalDate dateOfBirth, Long profilePhotoId) {
}
