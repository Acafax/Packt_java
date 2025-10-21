package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.example.springprojektzespolowy.dto.mappers.GroupDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.PhotoDtoMapper;
import org.example.springprojektzespolowy.dto.photo.CreatePhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDtoWithoutFile;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.Photo;
import org.example.springprojektzespolowy.repositories.PhotoRepository;
import org.example.springprojektzespolowy.utils.ImageUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoDtoMapper photoDtoMapper;
    private final GroupService groupService;
    private final GroupDtoMapper groupDtoMapper;
    private final ImageUtils imageUtils;

    public PhotoService(PhotoRepository photoRepository, PhotoDtoMapper photoDtoMapper, GroupService groupService, GroupDtoMapper groupDtoMapper, ImageUtils imageUtils) {
        this.photoRepository = photoRepository;
        this.photoDtoMapper = photoDtoMapper;
        this.groupService = groupService;
        this.groupDtoMapper = groupDtoMapper;
        this.imageUtils = imageUtils;
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupMemberByPhoto(authentication.name, #photoId)")
    public PhotoDto getPhotoById(Long photoId){
        Photo photo = photoRepository.findById(photoId).orElseThrow(EntityNotFoundException::new);

        return photoDtoMapper.convert(photo);
    }

    @Transactional
    public boolean photoExistById(Long id){
        return photoRepository.existsById(id);
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public List<PhotoDtoWithoutFile> getPhotosInGroup(Long groupId){
        List<Photo> photoByGroupId = photoRepository.getPhotoByGroup_Id(groupId);
        return photoDtoMapper.convertWithoutFile(photoByGroupId);
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public PhotoDto getPhotoByNameAndGroupId(String photoName, Long groupId){
        Photo photoByNameAndGroupId = photoRepository.findPhotoByNameAndGroup_Id(photoName, groupId);

        return photoDtoMapper.convert(photoByNameAndGroupId);
    }


    @Transactional
    @PreAuthorize("@securityService.isGroupMemberByPhoto(authentication.name, #id)")
    public PhotoDtoWithoutFile patchPhotoName(Long id, String newPhotoName){
        System.out.println(newPhotoName);
        Photo photo = photoRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        photo.setName(newPhotoName);

        photoRepository.save(photo);

        return photoDtoMapper.convertWithoutFile(photo);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public PhotoDtoWithoutFile uploadPhoto(CreatePhotoDto createPhotoDto, Long groupId) throws BadRequestException, FileSizeLimitExceededException {
        if (photoHasEmptyVariables(createPhotoDto)) throw new BadRequestException();
        if (!isFileSizeIsVald(createPhotoDto.file())) throw new FileSizeLimitExceededException("Plik przekracza dozwoloną wielkość (10MB)", createPhotoDto.file().getSize(), 10);


        MultipartFile file = createPhotoDto.file();
        String originalContentType = file.getContentType();
        byte[] fileBytes;

        try {
            fileBytes = file.getBytes();

            if (originalContentType != null && (originalContentType.equals("image/png") || originalContentType.equals("png"))) {
                log.info("Detected PNG file, attempting conversion to WEBP...");
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


            Group group = groupService.getGroupById(groupId);
            Photo photo = photoDtoMapper.convert(createPhotoDto, group);

            photo.setPhotoFile(fileBytes);
            photo.setFileType(originalContentType);
            photoRepository.save(photo);


            PhotoDtoWithoutFile photoDto = photoDtoMapper.convertWithoutFile(photo);
            return photoDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("@securityService.isGroupMemberByPhoto(authentication.name, #id)")
    @Transactional
    public  PhotoDtoWithoutFile deletePhotoById(Long id){
        PhotoDto photo = getPhotoById(id);

        photoRepository.deleteById(id);

        PhotoDtoWithoutFile photowithoutFile = photoDtoMapper.convertWithoutFile(photo);
        return photowithoutFile;
    }


    @Transactional
    public  List<PhotoDto> deletePhotoByGroupId(Long id){
        List<Photo> photosByGroupId = photoRepository.findPhotoByGroup_Id(id);

        photoRepository.deletePhotosByGroup_Id(id);

        List<PhotoDto> photos = photoDtoMapper.convert(photosByGroupId);

        return photos;
    }


    private boolean photoHasEmptyVariables(CreatePhotoDto createPhotoDto){
        return Stream.of(
                createPhotoDto.photoName().isEmpty(),
                createPhotoDto.file().isEmpty()
        ).anyMatch(Boolean::valueOf);
    }

    private boolean isFileSizeIsVald(MultipartFile file){
        long size = file.getSize();
        return size/(1024.0 * 1024.0)<10;
    }

}
