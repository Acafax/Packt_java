package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.photo.*;
import org.example.springprojektzespolowy.models.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PhotoDtoMapper {


    public PhotoDto convert(Photo photo){
        return new PhotoDto(
                photo.getId(),
                photo.getName(),
                photo.getFileType(),
                photo.getPhotoFile()
        );
    }
    public PhotoDtoWithoutFile convertWithoutFile(Photo photo){
        return new PhotoDtoWithoutFile(
                photo.getId(),
                photo.getName()
        );
    }
    public List<PhotoDtoWithoutFile> convertWithoutFile(List<Photo> photos){
       return photos.stream().map(this::convertWithoutFile).toList();
    }

    public PhotoDtoWithoutFile convertWithoutFile(PhotoDto photo){
        return new PhotoDtoWithoutFile(
                photo.id(),
                photo.photoName()
        );
    }


    public List<PhotoDto> convert(List<Photo> photos){
        return photos.stream()
                .map(this::convert)
                .toList();
    }

    public Photo convert(CreatePhotoDto createPhotoDto, Group group) throws IOException {
        return new Photo(
                createPhotoDto.photoName(),
                createPhotoDto.photoType(),
                createPhotoDto.file().getBytes(),
                group
        );
    }

    public ProfilePhotoDto convert(ProfilePhotoUser photoUser){
        return new ProfilePhotoDto(
                photoUser.getId(),
                photoUser.getFileType(),
                photoUser.getPhotoFile()
        );
    }

    public ProfilePhotoDto convert(ProfilePhotoGroup photoGroup){
        return new ProfilePhotoDto(
                photoGroup.getId(),
                photoGroup.getFileType(),
                photoGroup.getPhotoFile()
        );
    }

    public ProfilePhotoUser convert(CreateProfilePhotoDto createProfilePhotoDto, User user) throws IOException {
        return new ProfilePhotoUser(
                createProfilePhotoDto.file().getBytes(),
                createProfilePhotoDto.photoType(),
                user
        );
    }

    public ProfilePhotoGroup convert(CreateProfilePhotoDto createProfilePhotoDto, Group group) throws IOException {
        return new ProfilePhotoGroup(
                createProfilePhotoDto.file().getBytes(),
                createProfilePhotoDto.photoType(),
                group
        );
    }



}
