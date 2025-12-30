package org.example.springprojektzespolowy.services.userServices;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.userDto.*;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.example.springprojektzespolowy.repositories.userRepos.UserRepository;
import org.example.springprojektzespolowy.services.GroupService;
import org.example.springprojektzespolowy.services.PhotoService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.example.springprojektzespolowy.dto.mappers.UserDtoMapper;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final GroupService groupService;
    private final PhotoService photoService;

    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper, GroupService groupService, PhotoService photoService) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper ;
        this.groupService = groupService;
        this.photoService = photoService;
    }

    public User getUserByUId(String UId){
        return userRepository.findByUId(UId);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public UserDto getUserByUIdForController(String UId){
        boolean userExists = userRepository.existsUserByUId(UId);
        if (userExists){
            User user = userRepository.findByUId(UId);
            return userDtoMapper.convert(user);
        }
        throw new EntityNotFoundException("User not found");
    }

//    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public UserDto getUserDtoByUId(String UId){
        boolean userExists = userRepository.existsUserByUId(UId);
        if (userExists){
            User user = userRepository.findByUId(UId);
            return userDtoMapper.convert(user);
        }
        throw new EntityNotFoundException();
    }

    //@PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public Boolean userExistsByUId(String UId){
        return userRepository.existsUserByUId(UId);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name, #UId)")
    public UserWithGroupsDto getUserWithGroupsByUId(String UId){
        User user = userRepository.findUserWithGroupsByUId(UId);
        int size = user.getGroups().size();log.info("userGroup size: {}", size);
        return userDtoMapper.convertToUserWithGroups(user);

    }



    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public UserWithAllDetailsDto getUserWithAllDetails(String UId){//users/1/groups/details
        try {
            User user = userRepository.findUserWithGroupsByUId(UId);

            List<UserGroup> groups1 = user.getGroups();

            List<GroupDetailsDto> groupDetailsDtos= new ArrayList<>();

            groups1.forEach(group -> {
                GroupDetailsDto groupDetailsById = groupService.getGroupDetailsById(group.getGroup().getId());
                groupDetailsDtos.add(groupDetailsById);

            });

            return userDtoMapper.convertToUserWithAllDetailsDto(user, groupDetailsDtos);
        }catch (NullPointerException e){
            throw new EntityNotFoundException("User Not found: "+ e.getMessage());
        }
    }

    @PreAuthorize("@securityService.isDeveloper(authentication.name)")
    public List<UserDto> getUsers(){
        try {
            List<User> all = userRepository.findAll();
            return all.stream()
                    .map(userDtoMapper::convert)
                    .toList();
        }catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Empty User Table");

        }
    }


    @PreAuthorize("@securityService.isDeveloper(authentication.name)")
    public List<UserWithGroupsDto> getUsersWithGroups(){
        try {
            List<User> all = userRepository.findAllWithGroups();
            return all.stream()
                    .map(userDtoMapper::convertToUserWithGroups)
                    .toList();
        }catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Empty User Table");

        }
    }


    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public void deleteUser(String UId){
        userRepository.deleteUserByUId(UId);
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto){
        if (createUserDto.profilePhotoId()!= null && !photoService.photoExistById(createUserDto.profilePhotoId())) throw new EntityNotFoundException();

        User user = userDtoMapper.convert(createUserDto);
        User save = userRepository.save(user);

        return userDtoMapper.convert(save);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#userDto.UId())")
    public UserDto updateUser(UserDto userDto){
        User user = userRepository.findByUId(userDto.UId());

        user.setName(userDto.userName());
        user.setEmail(userDto.email());
        user.setDateOfBirth(userDto.dateOfBirth());
        user.setCountry(userDto.country());

        userRepository.save(user);
        return userDtoMapper.convert(user);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    public UserDto patchUser(UserDto patchUser, String UId){
        User user = userRepository.findByUId(UId);

        if (patchUser.userName() != null) user.setName(patchUser.userName());
        if (patchUser.email() != null) user.setEmail(patchUser.email());
        if (patchUser.country() != null) user.setCountry(patchUser.country());
        if (patchUser.dateOfBirth() != null) user.setDateOfBirth(patchUser.dateOfBirth());

        User newUser = userRepository.save(user);
        return userDtoMapper.convert(newUser);

    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name, #Uid)")
    public boolean pathUserProfilePhoto(String Uid, Long photoId){
        try {
            userRepository.findByUId(Uid).setProfilePhotoId(photoId);
            return true;
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        return true;
    }


    public User getUserByEmail(String email) {
       return userRepository.findUserByEmail(email);
    }
}