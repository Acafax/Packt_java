package org.example.springprojektzespolowy.dto.mappers;

import org.springframework.stereotype.Component;

@Component
public class UserEventMapper {

    private final UserDtoMapper userDtoMapper;
    private final EventDtoMapper eventDtoMapper;

    public UserEventMapper(UserDtoMapper userDtoMapper, EventDtoMapper eventDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.eventDtoMapper = eventDtoMapper;
    }


}
