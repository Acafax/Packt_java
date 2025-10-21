package org.example.springprojektzespolowy.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProfilePhotoUser {

    public ProfilePhotoUser(byte[] photoFile, String fileType, User user) {
        this.photoFile = photoFile;
        this.fileType = fileType;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] photoFile;

    private String fileType;

    @OneToOne(mappedBy = "photoUser", fetch = FetchType.LAZY)
    private User user;


}
