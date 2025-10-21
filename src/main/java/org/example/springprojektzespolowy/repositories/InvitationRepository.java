package org.example.springprojektzespolowy.repositories;


import org.example.springprojektzespolowy.models.Invitation;
import org.example.springprojektzespolowy.models.intermediateTable.InvitationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, InvitationKey> {

    @Query("SELECT i from  Invitation i LEFT JOIN FETCH i.user iu left JOIN fetch i.group ig where i.user.UId=:userUId")
    List<Invitation> getInvitationsByUser_UId(@Param("userUId")String userUId);

    boolean existsInvitationByGroup_IdAndUser_UId(Long groupId, String userUId);

    boolean existsInvitationByUser_UIdAndGroup_Id(String userUId, Long groupId);

    void deleteInvitationByUser_UIdAndGroup_Id(String userUId, Long groupId);

    Invitation findByUser_UIdAndGroup_Id(String userUId, Long groupId);

    void deleteByUser_UId(String userUId);
}
