package org.example.springprojektzespolowy.repositories;

import org.example.springprojektzespolowy.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface DocumentsRepository extends JpaRepository<Document, Long> {


    @Query("SELECT d FROM Document d WHERE d.group.id =:groupId AND d.name =:name")
    Document getDocumentFromGroupByIdAndByDocName(@Param("groupId") Long groupId, @Param("name") String name);

    Set<Document> findAllByGroup_Id(Long groupId);

    Set<Document> deleteTicketsByGroup_Id(Long groupId);

    Document findByIdAndGroup_Id(Long id, Long groupId);
}
