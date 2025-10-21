package org.example.springprojektzespolowy.repositories.userRepos;

import org.example.springprojektzespolowy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groups ug LEFT JOIN FETCH ug.user LEFT JOIN FETCH ug.group")
    List<User> findAllWithGroups();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groups ug LEFT JOIN FETCH ug.user LEFT JOIN FETCH ug.group" +
            " WHERE u.UId = :UId")
    User findUserWithGroupsByUId(@Param("UId") String UId);

    Boolean existsUserByUId(String uId);

    User findByUId(String uId);

    void deleteUserByUId(String uId);

    User  findUserByEmail(String email);
}
