package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.invitation.InvitationDto;
import org.example.springprojektzespolowy.dto.invitation.InvitationDtoWithoutInviter;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.models.Invitation;
import org.springframework.stereotype.Component;

@Component
public class InvitationDtoMapper {


    private final UserDtoMapper userDtoMapper;
    private final GroupDtoMapper groupDtoMapper;

    public InvitationDtoMapper(UserDtoMapper userDtoMapper, GroupDtoMapper groupDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.groupDtoMapper = groupDtoMapper;
    }

    public InvitationDto convert(Invitation invitation, UserDto inviter){
        return new InvitationDto(
                userDtoMapper.convert(invitation.getUser()),
                groupDtoMapper.convert(invitation.getGroup()),
                inviter
        );

    }

    public InvitationDtoWithoutInviter convert(Invitation invitation){
        return new InvitationDtoWithoutInviter(
                groupDtoMapper.convert(invitation.getGroup()),
                userDtoMapper.convert(invitation.getUser())
        );
    }


}
