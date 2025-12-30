package org.example.springprojektzespolowy.services.expenseServices;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.expenses.*;
import org.example.springprojektzespolowy.dto.mappers.ExpensesDtoMaper;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUserKey;
import org.example.springprojektzespolowy.repositories.expenseRepos.ExpensesUserRepository;
import org.example.springprojektzespolowy.repositories.expenseRepos.ExpensesRepository;
import org.example.springprojektzespolowy.services.GroupService;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ExpenseService {

    
    private final ExpensesRepository expesnsesRepository;
    private final UserService userService;
    private final ExpensesDtoMaper expensesDtoMaper;
    private final GroupService groupService;
    private final ExpensesUserRepository expensesUserRepository;


    public ExpenseService(ExpensesRepository expesnsesRepository, UserService userService, ExpensesDtoMaper expensesDtoMaper, GroupService groupService, ExpensesUserRepository expensesUserRepository) {
        this.expesnsesRepository = expesnsesRepository;
        this.userService = userService;
        this.expensesDtoMaper = expensesDtoMaper;
        this.groupService = groupService;
        this.expensesUserRepository = expensesUserRepository;
    }


    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public ExpenseParticipants createExpense(CreateExpenseDto createExpenseDto, Long groupId) throws BadRequestException {
        if (createExpenseDto.price().compareTo(BigDecimal.ZERO)<0) throw new BadRequestException();

        String creatorUId =  SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = groupService.getGroupById(groupId);
        Expense convert = expensesDtoMaper.convert(createExpenseDto,group);
        Expense expense = expesnsesRepository.save(convert);
        expense.setCreator(creatorUId);

        List<ExpensesUser> participants = new ArrayList<>();
        ExpensesUser expensesUser = addParticipant(expense, creatorUId, "CREATOR");
        participants.add(expensesUser);
        createExpenseDto.participants().forEach(Uid -> {
            if (!Uid.equals(creatorUId)) {
                ExpensesUser participant = addParticipant(expense, Uid, "PAYER");
                participants.add(participant);
            }
        });

        return expensesDtoMaper.convertParticipants(expense, participants);
    }

    @Transactional
    @PreAuthorize("@securityService.isExpenseCreator(authentication.name, #updateExpenseDto.creator())")
    public ExpenseParticipants updateExpense(UpdateExpenseDto updateExpenseDto) throws BadRequestException {
        if (containCreatorAndIsNotEmpty(updateExpenseDto)) throw new BadRequestException();
        Expense expense = expesnsesRepository.findById(updateExpenseDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        expense.setName(updateExpenseDto.name());
        expense.setDescription(updateExpenseDto.description());
        expense.setCategory(updateExpenseDto.category());
        expense.setPrice(updateExpenseDto.price());
        expense.setDateOfExpense(updateExpenseDto.dateOfExpense());

        expense.getParticipants().forEach(participant -> expensesUserRepository.deleteByUser_UIdAndExpense_Id(participant.getUser().getUId(), expense.getId()));

        List<ExpensesUser> updatedParticipants = new ArrayList<>();
        expense.getParticipants().clear();
        updateExpenseDto.participants().forEach(participant -> {
            if (participant.equals(expense.getCreator())){
                ExpensesUser creator = addParticipant(expense, participant, "CREATOR");
                updatedParticipants.add(creator);
            }else{
                ExpensesUser payer = addParticipant(expense, participant, "PAYER");
                updatedParticipants.add(payer);
            }
        });
        return expensesDtoMaper.convertParticipants(expense,updatedParticipants);
    }

    @Transactional
    @PreAuthorize("@securityService.isExpenseCreatorByExpId(authentication.name, #expId)")
    public ExpenseWithoutDocumentsAndEventsDto deleteExpById(Long expId){
        Expense expense = expesnsesRepository.findById(expId).orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        expensesUserRepository.deleteByExpense_Id(expId);
        expesnsesRepository.deleteById(expId);
        return expensesDtoMaper.convertWithout(expense);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public List<ExpenseDto> getExpensesByGroupId(Long groupId){
        if(!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group not found");
        List<Expense> expenseByGroupId = expesnsesRepository.getExpenseByGroup_Id(groupId);
        List<Expense> sortedExpenses = expenseByGroupId.stream()
                .sorted(Comparator.comparing(Expense::getId))
                .toList();
        return expensesDtoMaper.convert(sortedExpenses);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public ExpenseWithoutDocumentsAndEventsDto getExpById(Long expId, Long groupId){
        Expense expense = expesnsesRepository.findById(expId).orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        System.out.println(expense.getParticipants().size());
        return expensesDtoMaper.convertWithout(expense);
    }

    public ExpensesUser addParticipant(Expense expense, String uId, String role){
        User user = userService.getUserByUId(uId);
        if (user==null) throw new EntityNotFoundException("User not found");
        ExpensesUserKey key = new ExpensesUserKey(user.getId(),expense.getId());
        ExpensesUser expensesUser = new ExpensesUser(key, user, expense, role);
        return expensesUserRepository.save(expensesUser);
    }

    private boolean containCreatorAndIsNotEmpty(UpdateExpenseDto updateExpenseDto){
        return !updateExpenseDto.participants().contains(updateExpenseDto.creator());
    }




}
