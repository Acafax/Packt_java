package org.example.springprojektzespolowy.dto.userDto;

import java.time.LocalDate;
import java.util.Date;

public record CreateUserDto(String UId, String userName, String email, String country, LocalDate dateOfBirth, Long profilePhotoId) {
}
