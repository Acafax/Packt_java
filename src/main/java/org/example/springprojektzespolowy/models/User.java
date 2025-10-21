package org.example.springprojektzespolowy.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.example.springprojektzespolowy.models.intermediateTable.UserEvent;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "UId")},
            indexes = {
            @Index(name = "idx_firebase_uid", columnList = "UId"),
            @Index(name = "idx_users_email", columnList = "email")
}

)
public class User {

    public User(String UId,String name, String email , String country, LocalDate dateOfBirth, Long profilePhotoId) {
        this.UId = UId;
        this.name = name;
        this.email = email;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.profilePhotoId = profilePhotoId;
    }

    public User(String UId,String name, String email  , String country, LocalDate dateOfBirth, Long profilePhotoId ,List<UserGroup> groups) {
        this.UId = UId;
        this.name = name;
        this.email = email;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.profilePhotoId = profilePhotoId;
        this.groups = groups;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String UId;

    private String name;
    
    private String email;

    private String country;

    private LocalDate dateOfBirth;

    private Long profilePhotoId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profilePhoto", referencedColumnName = "id"
            , unique = true)
    private ProfilePhotoUser photoUser;

    @OneToMany(mappedBy = "user")
    private List<UserEvent> events;

    @OneToMany(mappedBy = "user")
    private List<ExpensesUser> expenses;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<UserGroup> groups;


}
