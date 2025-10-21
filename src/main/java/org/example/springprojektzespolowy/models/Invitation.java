package org.example.springprojektzespolowy.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.intermediateTable.InvitationKey;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Invitation {

    @EmbeddedId
    private InvitationKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    User user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(referencedColumnName = "id", name = "group_id")
    Group group;

    @Column(name = "inviter_uid")
    private String inviterUId;

    




}
