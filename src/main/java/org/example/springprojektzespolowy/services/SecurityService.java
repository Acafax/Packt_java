package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.Photo;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.example.springprojektzespolowy.repositories.DocumentsRepository;
import org.example.springprojektzespolowy.repositories.PhotoRepository;
import org.example.springprojektzespolowy.repositories.expenseRepos.ExpensesRepository;
import org.example.springprojektzespolowy.repositories.userRepos.UserGroupRepository;
import org.example.springprojektzespolowy.repositories.userRepos.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class SecurityService {


    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final DocumentsRepository documentsRepository;
    private final PhotoRepository photoRepository;
    private final ExpensesRepository expesnsesRepository;

    public SecurityService(UserGroupRepository userGroupRepository, UserRepository userRepository, DocumentsRepository documentsRepository, PhotoRepository photoRepository, ExpensesRepository expesnsesRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.documentsRepository = documentsRepository;
        this.photoRepository = photoRepository;
        this.expesnsesRepository = expesnsesRepository;
    }

    @Value("${spring.config.developers.list}")
    private List<String> developerUIdListConfig;

    private Set<String> developerUIdSet;

    @PostConstruct
    public void init() {
        if (this.developerUIdListConfig != null) {
            // Konwertuj listę z konfiguracji na Set
            this.developerUIdSet = new HashSet<>(this.developerUIdListConfig);
            // POPRAWNIE: Dodano {} - teraz lista UID zostanie wypisana w logach podczas startu aplikacji
            log.info("Załadowano deweloperskie UID: {}", this.developerUIdSet);
        } else {
            log.warn("Lista deweloperskich UID nie została załadowana z konfiguracji (app.security.developer-uids). Używanie pustego seta.");
            this.developerUIdSet = Collections.emptySet(); // Użyj pustego seta, aby uniknąć NullPointerException
        }
    }

    public boolean isDeveloper(String userId){
        return userId!= null && developerUIdSet.contains(userId);
    }

    public boolean isGroupMember(String UId, Long groupId){
        if (isDeveloper(UId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        return userGroupRepository.existsUserGroupByUser_UIdAndGroup_Id(UId, groupId);
    }

    public boolean isGroupMemberByDocument(String UId, Long docId){
        Long groupId = documentsRepository.findById(docId).get().getGroup().getId();
        return userGroupRepository.existsUserGroupByUser_UIdAndGroup_Id(UId, groupId);
    }


    public boolean isGroupMemberByPhoto(String UId, Long photoId){
        if (isDeveloper(UId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        log.debug("Sprawdzanie dostępu dla użytkownika '{}' do zdjęcia '{}'", UId, photoId);
        try {
            Optional<Photo> photoOpt = photoRepository.findById(photoId);
            if (photoOpt.isEmpty()) {
                log.warn("Nie znaleziono zdjęcia o id: {}", photoId);
                return false;
            }
            Photo photo = photoOpt.get();

            Group group = photo.getGroup();
            if (group == null) {
                log.warn("Zdjęcie o id '{}' nie jest przypisane do żadnej grupy.", photoId);
                return false;
            }

            Long groupId = group.getId();
            if (groupId == null) {
                log.error("Grupa powiązana ze zdjęciem '{}' ma ID null!", photoId);
                return false;
            }
            log.debug("Zdjęcie '{}' należy do grupy '{}'", photoId, groupId);

            boolean exists = userGroupRepository.existsUserGroupByUser_UIdAndGroup_Id(UId, groupId);
            log.debug("Członkostwo użytkownika '{}' w grupie '{}': {}", UId, groupId, exists);
            return exists;

        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas sprawdzania dostępu dla użytkownika '{}' do zdjęcia '{}'", UId, photoId, e);
            return false;
        }
    }

    public boolean isRequestingUserisAuthorizedForAccount(String authenticationUId,String UId){
        if (isDeveloper(authenticationUId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        return authenticationUId.equals(UId);
    }

    public boolean isGroupAdministrator(String UId, Long groupId){
        if (isDeveloper(UId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        UserGroup userGroup = userGroupRepository.findUserGroupByUser_UIdAndGroup_Id(UId, groupId);
        return userGroup.getRole().equals("ADMIN");
    }


    public Boolean isExpenseCreator(String UId, String creator) {
        if (isDeveloper(UId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        return UId.equals(creator);
    }

    public Boolean isExpenseCreatorByExpId(String UId, Long  expenseId) {
        if (isDeveloper(UId)){
            log.info("Przyznano dostep Dev", UId);
            return true;
        }
        Expense expense = expesnsesRepository.findById(expenseId).orElseThrow(EntityNotFoundException::new);
        return UId.equals(expense.getCreator());
    }
}
