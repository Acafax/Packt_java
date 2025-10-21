package org.example.springprojektzespolowy.repositories;

import org.example.springprojektzespolowy.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.users ug LEFT JOIN FETCH ug.user JOIN FETCH ug.group WHERE ug.group.id=:groupId" )
    Group findGroupWithUsersById(@Param("groupId") Long groupId);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.events LEFT JOIN FETCH g.documents WHERE g.id=:id")
    Group findGroupDetailsById(@Param("id") Long id);

}



































