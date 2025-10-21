package org.example.springprojektzespolowy.repositories;

import org.example.springprojektzespolowy.models.ProfilePhotoGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoGroupRepository extends JpaRepository<ProfilePhotoGroup, Long> {



}
