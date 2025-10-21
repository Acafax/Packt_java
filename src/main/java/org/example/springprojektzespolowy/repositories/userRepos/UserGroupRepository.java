package org.example.springprojektzespolowy.repositories.userRepos;

import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroupKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {


    void deleteUserGroupByUser_UId(String userUId);

    boolean existsUserGroupByUser_UIdAndGroup_Id(String userUId, Long groupId);
    
    boolean existsUserGroupByGroup_Id(Long groupId);

    boolean existsUserGroupByUser_UId(String userUId);

    void deleteUserGroupByGroup_Id(Long groupId);

    UserGroup findUserGroupByUser_UIdAndGroup_Id(String userUId, Long groupId);
}