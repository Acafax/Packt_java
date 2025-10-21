package org.example.springprojektzespolowy.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.springprojektzespolowy.dto.mappers.PhotoDtoMapper;
import org.example.springprojektzespolowy.dto.photo.CreateProfilePhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDtoWithoutFile;
import org.example.springprojektzespolowy.dto.photo.ProfilePhotoDto;
import org.example.springprojektzespolowy.models.*;
import org.example.springprojektzespolowy.repositories.ProfilePhotoGroupRepository;
import org.example.springprojektzespolowy.repositories.ProfilePhotoUserRepository;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Slf4j
@Service
public class ProfilePhotoService {


    private final ProfilePhotoGroupRepository profilePhotoGroupRepository;
    private final ProfilePhotoUserRepository profilePhotoUserRepository;
    private final PhotoDtoMapper photoDtoMapper;
    private final ImageUtils imageUtils;
    private final UserService userService;
    private final GroupService groupService;

    public ProfilePhotoService(ProfilePhotoGroupRepository profilePhotoGroupRepository, ProfilePhotoUserRepository profilePhotoUserRepository, PhotoDtoMapper photoDtoMapper, ImageUtils imageUtils, UserService userService, GroupService groupService) {
        this.profilePhotoGroupRepository = profilePhotoGroupRepository;
        this.profilePhotoUserRepository = profilePhotoUserRepository;
        this.photoDtoMapper = photoDtoMapper;
        this.imageUtils = imageUtils;
        this.userService = userService;
        this.groupService = groupService;
    }


    public ProfilePhotoDto getUserProfile(Long profilePhotoId){
        ProfilePhotoUser profilePhotoUser = profilePhotoUserRepository.findById(profilePhotoId)
                .orElseThrow(EntityNotFoundException::new);

        return photoDtoMapper.convert(profilePhotoUser);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name, #Uid)")
    @Transactional
    public Long uploadUserProfilePhoto(CreateProfilePhotoDto createProfilePhotoDto, String Uid) throws IOException {
        if (!userService.userExistsByUId(Uid)) throw new EntityNotFoundException();
        byte[] fileBytes = photoConverter(createProfilePhotoDto);

        User user = userService.getUserByUId(Uid);
        Long oldProfilePhotoId = user.getProfilePhotoId();

        ProfilePhotoUser photo = photoDtoMapper.convert(createProfilePhotoDto, user);
        photo.setPhotoFile(fileBytes);
        photo.setFileType("image/webp");
        ProfilePhotoUser finalPhoto = profilePhotoUserRepository.save(photo);


        log.info("Deleted Old Photo");
        userService.pathUserProfilePhoto(Uid, finalPhoto.getId());
        deleteUserProfilePhoto(oldProfilePhotoId);
        return finalPhoto.getId();
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    @Transactional
    public Long uploadGroupProfilePhoto(CreateProfilePhotoDto createProfilePhotoDto, Long groupId) throws IOException {
        if (!groupService.groupExists(groupId)) {
            log.info("Group with id: {}",groupId);
            throw new EntityNotFoundException();
        }
        byte[] fileBytes = photoConverter(createProfilePhotoDto);

        Group group = groupService.getGroupById(groupId);
        Long oldGroupProfileId = group.getProfilePhotoId();

        ProfilePhotoGroup photo = photoDtoMapper.convert(createProfilePhotoDto, group);
        photo.setPhotoFile(fileBytes);
        photo.setFileType("image/webp");

        ProfilePhotoGroup finalPhoto = profilePhotoGroupRepository.save(photo);
        groupService.patchGroupProfilePhoto(groupId, finalPhoto.getId());
        deleteGroupProfilePhoto(oldGroupProfileId);

        return finalPhoto.getId();
   }

    public ProfilePhotoDto getGroupProfile(Long profilePhotoId){
        ProfilePhotoGroup profilePhotoUser = profilePhotoGroupRepository.findById(profilePhotoId)
                .orElseThrow(EntityNotFoundException::new);

        return photoDtoMapper.convert(profilePhotoUser);
    }

    private byte[] photoConverter(CreateProfilePhotoDto createProfilePhotoDto){
        MultipartFile file = createProfilePhotoDto.file();
        String originalContentType = file.getContentType();
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
            if (originalContentType != null && (originalContentType.equals("image/png") || originalContentType.equals("png"))) {
                //log.info("Detected PNG file, attempting conversion to WEBP...");
                try {
                    float webpQuality = 0.8f;
                    fileBytes = imageUtils.convertToWebp(fileBytes, webpQuality);
                    originalContentType = "image/webp";
                    log.info("Successfully converted PNG to WEBP.");
                } catch (IOException | IllegalArgumentException e) {
                    log.error("Failed to convert PNG to WEBP. Proceeding with original PNG.", e);
                    throw new IOException("Failed to convert image to WEBP: " + e.getMessage(), e);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBytes;
    }


    private void deleteUserProfilePhoto(Long id){
        if (id!=null){
            profilePhotoUserRepository.deleteById(id);
        }
    }

    private void deleteGroupProfilePhoto(Long id){
        if (id!=null){
            profilePhotoGroupRepository.deleteById(id);
        }
    }

}
