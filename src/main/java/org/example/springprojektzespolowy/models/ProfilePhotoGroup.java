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
public class ProfilePhotoGroup {
    public ProfilePhotoGroup(byte[] photoFile, String fileType, Group group) {
        this.photoFile = photoFile;
        this.fileType = fileType;
        this.group = group;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] photoFile;

    private String fileType;

    @OneToOne(mappedBy = "photoGroup")
    private Group group;


}
