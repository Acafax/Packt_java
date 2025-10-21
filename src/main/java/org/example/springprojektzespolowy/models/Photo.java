package org.example.springprojektzespolowy.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    public Photo(String name, byte[] photoFile, Group group) {
        this.name = name;
        this.photoFile = photoFile;
        this.group = group;
    }

    public Photo(String name, byte[] photoFile) {
        this.name = name;
        this.photoFile = photoFile;
    }

    public Photo(String name, String fileType, byte[] photoFile, Group group) {
        this.name = name;
        this.fileType = fileType;
        this.photoFile = photoFile;
        this.group = group;
    }

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] photoFile;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;


}
