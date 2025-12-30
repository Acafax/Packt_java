package org.example.springprojektzespolowy.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.expenses.*;
import org.example.springprojektzespolowy.services.expenseServices.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("budget")
public class ExpensesController {

    private final ExpenseService expenseService;


    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/{groupId}")
    ResponseEntity<List<ExpenseDto>> getExpenses(@PathVariable Long groupId){
        List<ExpenseDto> expenses = expenseService.getExpensesByGroupId(groupId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{groupId}/{expId}")
    ResponseEntity<ExpenseWithoutDocumentsAndEventsDto> getExp(@PathVariable Long groupId, @PathVariable Long expId){
        ExpenseWithoutDocumentsAndEventsDto expense = expenseService.getExpById(expId, groupId);
        return ResponseEntity.ok(expense);
    }

    @PostMapping("/{groupId}")
    ResponseEntity<ExpenseParticipants> createExpenses(@RequestBody CreateExpenseDto createExpenseDto, @PathVariable Long groupId) throws BadRequestException {
        ExpenseParticipants expense = expenseService.createExpense(createExpenseDto, groupId);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("")
    ResponseEntity<ExpenseParticipants> updateExpense(@RequestBody UpdateExpenseDto updateExpenseDto) throws BadRequestException {
        ExpenseParticipants expenseParticipants = expenseService.updateExpense(updateExpenseDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(expenseParticipants);
    }

    @DeleteMapping("/{expId}")
    ResponseEntity<ExpenseWithoutDocumentsAndEventsDto> deleteExp(@PathVariable Long expId){
        ExpenseWithoutDocumentsAndEventsDto expenseDto = expenseService.deleteExpById(expId);
        return ResponseEntity.ok(expenseDto);
    }

}
