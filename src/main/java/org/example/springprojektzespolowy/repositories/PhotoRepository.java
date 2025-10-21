package org.example.springprojektzespolowy.repositories;

import org.example.springprojektzespolowy.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
    Photo findPhotoByNameAndGroup_Id(String name, Long groupId);

    void deletePhotosByGroup_Id(Long groupId);

    List<Photo> findPhotoByGroup_Id(Long groupId);

    List<Photo> getPhotoByGroup_Id(Long groupId);
}
