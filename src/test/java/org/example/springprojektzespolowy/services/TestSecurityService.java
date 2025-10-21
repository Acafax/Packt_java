package org.example.springprojektzespolowy.services;

public class TestSecurityService extends SecurityService {

    public TestSecurityService() {
        super(null, null, null, null, null);
    }

    @Override
    public boolean isDeveloper(String userId) {
        return true;
    }

    @Override
    public boolean isGroupMember(String UId, Long groupId) {
        return true;
    }

    @Override
    public boolean isGroupMemberByDocument(String UId, Long docId) {
        return true;
    }

    @Override
    public boolean isGroupMemberByPhoto(String UId, Long photoId) {
        return true;
    }

    @Override
    public boolean isRequestingUserisAuthorizedForAccount(String authenticationUId, String UId) {
        return true;
    }

    @Override
    public boolean isGroupAdministrator(String UId, Long groupId) {
        return true;
    }

    @Override
    public Boolean isExpenseCreator(String UId, String creator) {
        return true;
    }

    @Override
    public Boolean isExpenseCreatorByExpId(String UId, Long expenseId) {
        return true;
    }
}
