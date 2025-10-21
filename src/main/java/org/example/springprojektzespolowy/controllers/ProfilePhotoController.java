package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.photo.*;
import org.example.springprojektzespolowy.services.ProfilePhotoService;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

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
    public ResponseEntity<Long> uploadUserProfilePhoto(@ModelAttribute CreateProfilePhotoDto createPhotoDto, @PathVariable String Uid){
        try {
            if (createPhotoDto.photoType().isEmpty() || createPhotoDto.file().isEmpty()) throw new BadRequestException();
            Long photoId = profilePhotoService.uploadUserProfilePhoto(createPhotoDto, Uid);

            return ResponseEntity.ok(photoId);
        }
        catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (UnsupportedMediaTypeStatusException ex){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @GetMapping("/user/{photoId}")
    public ResponseEntity<byte[]> getUserProfile(@PathVariable Long photoId){
        try {
            ProfilePhotoDto userProfile = profilePhotoService.getUserProfile(photoId);

            boolean b = imageUtils.photoValidator(userProfile.photoFile(), userProfile.fileType());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(userProfile.fileType()))
                    .body(userProfile.photoFile());
        }catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UnsupportedMediaTypeStatusException ex){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(value = "/group/upload/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadGroupProfilePhoto(@ModelAttribute CreateProfilePhotoDto createPhotoDto, @PathVariable Long groupId){
        try {
            if (createPhotoDto.photoType().isEmpty() || createPhotoDto.file().isEmpty()) throw new BadRequestException();
            Long photoId = profilePhotoService.uploadGroupProfilePhoto(createPhotoDto, groupId);
            return ResponseEntity.ok(photoId);
        }
        catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (UnsupportedMediaTypeStatusException ex){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/group/{photoId}")
    public ResponseEntity<byte[]> getGroupProfile(@PathVariable Long photoId){
        try {
            ProfilePhotoDto groupProfile = profilePhotoService.getGroupProfile(photoId);

            imageUtils.photoValidator(groupProfile.photoFile(), groupProfile.fileType());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(groupProfile.fileType()))
                    .body(groupProfile.photoFile());
        }catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UnsupportedMediaTypeStatusException ex){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






}
