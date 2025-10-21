package org.example.springprojektzespolowy.dto.userDto;

import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record UserWithAllDetailsDto(Long id, String UId , String userName, String email, String country,
                                    LocalDate dateOfBirth, Long profilePhotoId ,List<GroupDetailsDto> groupDetails) {
}
