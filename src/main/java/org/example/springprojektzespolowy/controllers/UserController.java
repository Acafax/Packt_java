package org.example.springprojektzespolowy.controllers;


import org.example.springprojektzespolowy.dto.userDto.*;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.springframework.http.ResponseEntity;
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
        List<UserWithGroupsDto> users = userService.getUsersWithGroups();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        List<UserDto> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{UId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String UId){
        UserDto user = userService.getUserByUIdForController(UId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{UId}/groups/details")
    public ResponseEntity<UserWithAllDetailsDto> getUserWithAllDetails(@PathVariable String UId){
        UserWithAllDetailsDto userWithAllDetails = userService.getUserWithAllDetails(UId);
        return ResponseEntity.ok(userWithAllDetails);
    }

    @GetMapping("/{UId}/groups")
    public ResponseEntity<UserWithGroupsDto> getUserWithGroupsById(@PathVariable String UId){
        UserWithGroupsDto user = userService.getUserWithGroupsByUId(UId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto){
        UserDto user = userService.createUser(createUserDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{UId}/")
                .buildAndExpand(user.id())
                .toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user){
        UserDto userDto = userService.updateUser(user);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/patch/{UId}")
    public ResponseEntity<UserDto> patchUser(@RequestBody UserDto user, @PathVariable String UId){
        UserDto userDto = userService.patchUser(user, UId);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{UId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String UId){
        UserDto userDto = deleteEntityService.deleteUser(UId);
        return ResponseEntity.ok(userDto);
    }

}
