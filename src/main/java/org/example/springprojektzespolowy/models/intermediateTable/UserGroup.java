package org.example.springprojektzespolowy.models.intermediateTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class UserGroup  {

    @EmbeddedId
    private UserGroupKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    Group group;

    String role;

}