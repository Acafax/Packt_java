package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.groupDto.CreateGroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.models.Group;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupDtoMapper {
    private final UserGroupMapper userGroupMapper;
    private final DocumentDtoMapper documentDtoMapper;
    private final EventDtoMapper eventDtoMapper;


    public GroupDtoMapper(UserGroupMapper userGroupMapper, DocumentDtoMapper ticketDtoMapper, EventDtoMapper eventDtoMapper) {
        this.userGroupMapper = userGroupMapper;
        this.documentDtoMapper = ticketDtoMapper;
        this.eventDtoMapper = eventDtoMapper;

    }

    public List<GroupDto> convert(List<Group> groups){
        return groups.stream()
                .map(this::convert)
                .toList();
    }

    public GroupDto convert(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                group.getMaxBudget(),
                group.getStartDate(),
                group.getEndDate(),
                group.getProfilePhotoId()
        );
    }
    public Group convert(GroupDto group){
        return new Group(
                group.id(),
                group.groupName(),
                group.description(),
                group.currency(),
                group.maxBudget(),
                group.startDate(),
                group.endDate()
        );
    }

    public Group convert(CreateGroupDto group) {
        return new Group(
                group.groupName(),
                group.description(),
                group.currency(),
                group.maxBudget(),
                group.startDate(),
                group.endDate()
        );
    }


    public GroupDetailsDto convertGroupDetailsDto(Group group, Integer numbreOfMembers) {
        return new GroupDetailsDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                group.getStartDate(),
                group.getEndDate(),
                group.getMaxBudget(),
                numbreOfMembers,
                group.getProfilePhotoId(),
                documentDtoMapper.convert(group.getDocuments()),
                eventDtoMapper.convert(group.getEvents())
        );

    }
}


















