package org.example.springprojektzespolowy.models;

import com.google.firebase.database.annotations.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_document_name_in_group", columnNames = {"name", "group_id"})
})
public class Document {

    public Document(String name, String directory, String fileType, byte[] file, Group group) {
        this.name = name;
        this.path = directory;
        this.fileType = fileType;
        this.file = file;
        this.group = group;
    }

    public Document(String name,  String directory, String fileType) {
        this.name = name;
        this.path = directory;
        this.fileType = fileType;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String path;

    private String fileType;
    @NotNull
    @Lob
    private byte[] file;

    @OneToMany(mappedBy = "document")
    List<ExpensesDocument> expenses;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}