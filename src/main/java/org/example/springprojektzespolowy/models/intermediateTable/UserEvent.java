package org.example.springprojektzespolowy.models.intermediateTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.models.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEvent {

    @EmbeddedId
    private UserEventKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(referencedColumnName = "id", name = "event_id")
    Event event;


    String role;
}
