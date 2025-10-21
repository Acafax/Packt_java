package org.example.springprojektzespolowy.repositories.userRepos;

import org.example.springprojektzespolowy.models.intermediateTable.UserEvent;
import org.example.springprojektzespolowy.models.intermediateTable.UserEventKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, UserEventKey> {

    @Query("SELECT ue FROM UserEvent ue JOIN FETCH ue.user JOIN FETCH ue.event WHERE ue.id.eventId=:eventId")
    List<UserEvent> getUserEventByEvent_Id(@Param("eventId") Long eventId);

    void deleteUserEventByEvent_Id(Long eventId);

    boolean existsUserEventByEvent_Id(Long eventId);

    void deleteByUser_UId(String userUId);
}
