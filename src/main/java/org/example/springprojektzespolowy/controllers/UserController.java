package org.example.springprojektzespolowy.controllers;


import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.example.springprojektzespolowy.dto.userDto.*;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController()
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final DeleteEntityService deleteEntityService;

    public UserController(UserService userService, DeleteEntityService deleteEntityService) {
        this.userService = userService;
        this.deleteEntityService = deleteEntityService;
    }

    @GetMapping("/all/groups")
    public ResponseEntity<List<UserWithGroupsDto>> getAllUsersWithGroups(){
        try {
            List<UserWithGroupsDto> users = userService.getUsersWithGroups();
            return ResponseEntity.ok(users);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        try {
            List<UserDto> users = userService.getUsers();
            return ResponseEntity.ok(users);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{UId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String UId){
        try {
            UserDto user = userService.getUserByUIdForController(UId);
            return ResponseEntity.ok(user);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{UId}/groups/details")
    public ResponseEntity<UserWithAllDetailsDto> getUserWithAllDetails(@PathVariable String UId){
        try {
            UserWithAllDetailsDto userWithAllDetails = userService.getUserWithAllDetails(UId);
            return ResponseEntity.ok(userWithAllDetails);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{UId}/groups")
    public ResponseEntity<UserWithGroupsDto> getUserWithGroupsById(@PathVariable String UId){
        try {
            UserWithGroupsDto user = userService.getUserWithGroupsByUId(UId);
            return ResponseEntity.ok(user);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }



    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto){
        try {
            UserDto user = userService.createUser(createUserDto);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("{UId}/")
                    .buildAndExpand(user.id())
                    .toUri();


            return ResponseEntity.created(uri).body(user);

        }catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }catch (DataIntegrityViolationException | EntityExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user){
        try {
            UserDto userDto = userService.updateUser(user);
            return ResponseEntity.ok(userDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/patch/{UId}")
    public ResponseEntity<UserDto> patchUser(@RequestBody UserDto user,@PathVariable String UId){
        try {
            UserDto userDto = userService.patchUser(user, UId);
            return ResponseEntity.ok(userDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{UId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String UId){
        try {
            UserDto userDto = deleteEntityService.deleteUser(UId);
            return ResponseEntity.ok(userDto);
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
