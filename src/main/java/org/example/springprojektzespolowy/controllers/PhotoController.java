package org.example.springprojektzespolowy.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.example.springprojektzespolowy.dto.photo.CreatePhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDtoWithoutFile;
import org.example.springprojektzespolowy.services.PhotoService;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/photo")
public class PhotoController {


    private final PhotoService photoService;
    private final ImageUtils imageUtils;

    public PhotoController(PhotoService photoService, ImageUtils imageUtils) {
        this.photoService = photoService;
        this.imageUtils = imageUtils;
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoDto> getPhotoById(@PathVariable Long photoId){
        PhotoDto photo = photoService.getPhotoById(photoId);
        return ResponseEntity.ok(photo);
    }

    @GetMapping("/{id}/raw")
    public ResponseEntity<byte[]> getPhotoFile(@PathVariable Long id) throws BadRequestException {
        PhotoDto photo = photoService.getPhotoById(id);

        imageUtils.photoValidator(photo.photoFile(), photo.fileType());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.fileType()))
                .body(photo.photoFile());
    }

    @GetMapping("/{photoName}/{groupId}")
    public ResponseEntity<PhotoDto> getPhotoByNameAndGroupId(@PathVariable String photoName, @PathVariable Long groupId){
        PhotoDto photoByNameAndGroupId = photoService.getPhotoByNameAndGroupId(photoName, groupId);
        return ResponseEntity.ok(photoByNameAndGroupId);
    }

    @GetMapping("/in-group/{groupId}")
    public ResponseEntity<List<PhotoDtoWithoutFile>> getPhotosInGroup(@PathVariable Long groupId){
        List<PhotoDtoWithoutFile> photosInGroup = photoService.getPhotosInGroup(groupId);
        return ResponseEntity.ok(photosInGroup);
    }

    @PatchMapping("/{photoId}")
    public ResponseEntity<PhotoDtoWithoutFile> patchPhotoName(@PathVariable Long photoId, @RequestBody String newPhotoName){
        PhotoDtoWithoutFile photoDto = photoService.patchPhotoName(photoId, newPhotoName);
        return ResponseEntity.ok(photoDto);
    }

    @PostMapping(value = "/upload/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDtoWithoutFile> uploadPhoto(@ModelAttribute CreatePhotoDto createPhotoDto, @PathVariable Long groupId) throws BadRequestException, FileSizeLimitExceededException {
        PhotoDtoWithoutFile photoDto = photoService.uploadPhoto(createPhotoDto, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(photoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PhotoDtoWithoutFile> deletePhoto(@PathVariable Long id){
        PhotoDtoWithoutFile photo = photoService.deletePhotoById(id);
        log.info("Pomyślnie usunięto zdjęcie o id: {}", id);
        return ResponseEntity.ok(photo);
    }

}
