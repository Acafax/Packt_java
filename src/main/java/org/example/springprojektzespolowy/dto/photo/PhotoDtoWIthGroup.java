package org.example.springprojektzespolowy.dto.photo;

import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.models.Group;

public record PhotoDtoWIthGroup(Long id, String photoName, byte[] photoFile, GroupDto groupDto) {
}
