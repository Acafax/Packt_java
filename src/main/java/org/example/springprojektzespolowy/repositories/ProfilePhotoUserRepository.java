package org.example.springprojektzespolowy.repositories;


import org.example.springprojektzespolowy.models.ProfilePhotoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoUserRepository extends JpaRepository<ProfilePhotoUser, Long> {


    Long id(Long id);
}
