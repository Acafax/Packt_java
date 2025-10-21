package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.expenses.*;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ExpensesDtoMaper {


    private final UserDtoMapper userDtoMapper;
    private final EventDtoMapper eventDtoMapper;
    private final DocumentDtoMapper documentDtoMapper;

    public ExpensesDtoMaper(UserDtoMapper userDtoMapper, EventDtoMapper eventDtoMapper, DocumentDtoMapper documentDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.eventDtoMapper = eventDtoMapper;
        this.documentDtoMapper = documentDtoMapper;
    }

    public ExpenseWithoutDocumentsAndEventsDto convertWithout(Expense expense){
        return new ExpenseWithoutDocumentsAndEventsDto(
                expense.getId(),
                expense.getName(),
                expense.getDescription(),
                expense.getCategory(),
                expense.getPrice(),
                expense.getDateOfExpense(),
                expense.getDateOfAdding(),
                expense.getCreator(),
                userDtoMapper.convertList(expense.getParticipants())
        );
    }

    public ExpenseDto convert(Expense expense){
        return new ExpenseDto(
                expense.getId(),
                expense.getName(),
                expense.getDescription(),
                expense.getCategory(),
                expense.getPrice(),
                expense.getDateOfExpense(),
                expense.getDateOfAdding(),
                expense.getCreator(),
                userDtoMapper.convertList(expense.getParticipants()),
                documentDtoMapper.convertExpDocument(expense.getDocuments()),
                eventDtoMapper.convertExpEvent(expense.getEvents())
        );
    }

    public List<ExpenseDto> convert(List<Expense> expenses){
        return expenses.stream()
                .map(this::convert)
                .toList();
    }

    public ExpenseParticipants convertParticipants(Expense expense,List<ExpensesUser> participants ){
        return new ExpenseParticipants(
                expense.getId(),
                expense.getName(),
                expense.getDescription(),
                expense.getCategory(),
                expense.getPrice(),
                expense.getDateOfExpense(),
                expense.getDateOfAdding(),
                expense.getCreator(),
                userDtoMapper.convertList(participants)
        );
    }

    public Expense convert(CreateExpenseDto expenseDto){
        return new Expense(
                expenseDto.name(),
                expenseDto.description(),
                expenseDto.category(),
                expenseDto.price(),
                expenseDto.dateOfExpense(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public Expense convert(CreateExpenseDto expenseDto, Group group){
        return new Expense(
                expenseDto.name(),
                expenseDto.description(),
                expenseDto.category(),
                expenseDto.price(),
                expenseDto.dateOfExpense(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                group
        );
    }











}
