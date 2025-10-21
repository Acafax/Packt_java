package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Response;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.example.springprojektzespolowy.dto.photo.CreatePhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDtoWithoutFile;
import org.example.springprojektzespolowy.services.PhotoService;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.PayloadTooLargeException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

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
        try {
            PhotoDto photo = photoService.getPhotoById(photoId);
            return ResponseEntity.ok(photo);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/raw")
    public ResponseEntity<byte[]> getPhotoFile(@PathVariable Long id) {
        try {
            PhotoDto photo = photoService.getPhotoById(id);

            boolean b = imageUtils.photoValidator(photo.photoFile(), photo.fileType());

           return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(photo.fileType()))
                    .body(photo.photoFile());

        }catch (BadRequestException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (UnsupportedMediaTypeStatusException ex){
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROOOOORR ", id,ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{photoName}/{groupId}")
    public ResponseEntity<PhotoDto> getPhotoByNameAndGroupId(@PathVariable String photoName, @PathVariable Long groupId){
        try {
            PhotoDto photoByNameAndGroupId = photoService.getPhotoByNameAndGroupId(photoName, groupId);
            return ResponseEntity.ok(photoByNameAndGroupId);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/in-group/{groupId}")
    public ResponseEntity<List<PhotoDtoWithoutFile>> getPhotosInGroup(@PathVariable Long groupId){
        try {
            List<PhotoDtoWithoutFile> photosInGroup = photoService.getPhotosInGroup(groupId);
            return ResponseEntity.ok(photosInGroup);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{photoId}")
    public ResponseEntity<PhotoDtoWithoutFile> patchPhotoName(@PathVariable Long photoId, @RequestBody String newPhotoName){
        try {
            PhotoDtoWithoutFile photoDto = photoService.patchPhotoName(photoId, newPhotoName);
            return ResponseEntity.ok(photoDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/upload/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDtoWithoutFile> uploadPhoto(@ModelAttribute CreatePhotoDto createPhotoDto, @PathVariable Long groupId){
        try {
            //boolean validator = photoCreateValidator(createPhotoDto);

            PhotoDtoWithoutFile photoDto = photoService.uploadPhoto(createPhotoDto, groupId);
            return ResponseEntity.status(HttpStatus.CREATED).body(photoDto);

        }catch (FileSizeLimitExceededException ex){
            log.warn("Za duży plik: ", ex.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }catch (BadRequestException ex){
            log.warn("Plik jest pusty i nie zawiera zdjęcia: ", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PhotoDtoWithoutFile> deletePhoto(@PathVariable Long id){
        try {
            PhotoDtoWithoutFile photo = photoService.deletePhotoById(id);
            log.info("Pomyślnie usunięto zdjęcie o id: " ,id);
            return ResponseEntity.ok(photo);
        }catch (EntityNotFoundException ex){
            log.warn("Nie znaleziono Zdjecia");
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private boolean photoCreateValidator(CreatePhotoDto createPhotoDto) throws BadRequestException, FileSizeLimitExceededException, UnsupportedMediaTypeStatusException {
        MultipartFile file = createPhotoDto.file();
        String photoName = createPhotoDto.photoName();
        String contentType = file.getContentType();

        if (file.isEmpty()) throw new BadRequestException();

        if (contentType == null || !contentType.equals("image/webp")){
            throw new UnsupportedMediaTypeStatusException("Only WEBP files are allowed.");
        }

        long maxFileSize = (10 * 1024 * 1024); //10 MB
        if (file.getSize() > maxFileSize ){
            throw new FileSizeLimitExceededException("File can have a maximalli of 10 MB size",file.getSize(),maxFileSize);
        }

        return true;

    }








}
