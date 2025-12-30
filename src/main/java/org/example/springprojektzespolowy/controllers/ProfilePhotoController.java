package org.example.springprojektzespolowy.controllers;

import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.photo.*;
import org.example.springprojektzespolowy.services.ProfilePhotoService;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class ProfilePhotoController {
    private final ProfilePhotoService profilePhotoService;
    private final ImageUtils imageUtils;

    public ProfilePhotoController(ProfilePhotoService profilePhotoService, ImageUtils imageUtils) {
        this.profilePhotoService = profilePhotoService;
        this.imageUtils = imageUtils;
    }


    @PostMapping(value = "/user/upload/{Uid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadUserProfilePhoto(@ModelAttribute CreateProfilePhotoDto createPhotoDto, @PathVariable String Uid) throws BadRequestException, IOException {
        if (createPhotoDto.photoType().isEmpty() || createPhotoDto.file().isEmpty()) {
            throw new BadRequestException("Photo type and file are required");
        }
        Long photoId = profilePhotoService.uploadUserProfilePhoto(createPhotoDto, Uid);
        return ResponseEntity.ok(photoId);
    }

    @GetMapping("/user/{photoId}")
    public ResponseEntity<byte[]> getUserProfile(@PathVariable Long photoId) throws BadRequestException {
        ProfilePhotoDto userProfile = profilePhotoService.getUserProfile(photoId);

        imageUtils.photoValidator(userProfile.photoFile(), userProfile.fileType());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userProfile.fileType()))
                .body(userProfile.photoFile());
    }


    @PostMapping(value = "/group/upload/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadGroupProfilePhoto(@ModelAttribute CreateProfilePhotoDto createPhotoDto, @PathVariable Long groupId) throws BadRequestException, IOException {
        if (createPhotoDto.photoType().isEmpty() || createPhotoDto.file().isEmpty()) {
            throw new BadRequestException("Photo type and file are required");
        }
        Long photoId = profilePhotoService.uploadGroupProfilePhoto(createPhotoDto, groupId);
        return ResponseEntity.ok(photoId);
    }

    @GetMapping("/group/{photoId}")
    public ResponseEntity<byte[]> getGroupProfile(@PathVariable Long photoId) throws BadRequestException {
        ProfilePhotoDto groupProfile = profilePhotoService.getGroupProfile(photoId);

        imageUtils.photoValidator(groupProfile.photoFile(), groupProfile.fileType());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(groupProfile.fileType()))
                .body(groupProfile.photoFile());
    }

}
