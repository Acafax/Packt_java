package org.example.springprojektzespolowy.repositories;

import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.example.springprojektzespolowy.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.users eu LEFT JOIN FETCH e.group eg WHERE e.group.id=:groupId ")
    Set<Event> getEventsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.users eu LEFT JOIN FETCH e.group eg WHERE e.name=:eventName AND e.group.id=:groupId")
    Set<Event> getEventsByNameAndGroupId(@Param("eventName") String eventName, @Param("groupId") Long groupId);

    @Query("SELECT e FROM Event e WHERE e.name=:eventName AND e.group.name=:groupName")
    Set<Event> getEventsAllByName(@Param("eventName") String eventName, @Param("groupName") String groupName);

    @Query("SELECT e FROM Event e WHERE e.name=:eventName")
    Event getEventByName(@Param("eventName") String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM Event e where e.group.id=:groupId")
    void deleteEventByGroup_Id(@Param("groupId") Long groupId);

    boolean existsEventByName(String name);

    Set<Event> findByGroup_Id(Long groupId);
}
