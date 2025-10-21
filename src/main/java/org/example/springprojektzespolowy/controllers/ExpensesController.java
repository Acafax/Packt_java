package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.expenses.*;
import org.example.springprojektzespolowy.services.expenseServices.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
        try {
            List<ExpenseDto> expenses = expenseService.getExpensesByGroupId(groupId);
            return ResponseEntity.ok(expenses);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{groupId}/{expId}")
    ResponseEntity<ExpenseWithoutDocumentsAndEventsDto> getExp(@PathVariable Long groupId, @PathVariable Long expId){
        try {
            ExpenseWithoutDocumentsAndEventsDto expense = expenseService.getExpById(expId, groupId);
            return ResponseEntity.ok(expense);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{groupId}")
    ResponseEntity<ExpenseParticipants> createExpenses(@RequestBody CreateExpenseDto createExpenseDto, @PathVariable Long groupId){
        try {
            ExpenseParticipants expense = expenseService.createExpense(createExpenseDto, groupId);
            return ResponseEntity.ok(expense);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("")
    ResponseEntity<ExpenseParticipants> updateExpense(@RequestBody  UpdateExpenseDto updateExpenseDto){
        try {

            ExpenseParticipants expenseParticipants = expenseService.updateExpense(updateExpenseDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(expenseParticipants);

        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().build();
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }



    @DeleteMapping("/{expId}")
    ResponseEntity<ExpenseWithoutDocumentsAndEventsDto> deleteExp(@PathVariable Long expId){
        try {
            ExpenseWithoutDocumentsAndEventsDto expenseDto = expenseService.deleteExpById(expId);
            return ResponseEntity.ok(expenseDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
