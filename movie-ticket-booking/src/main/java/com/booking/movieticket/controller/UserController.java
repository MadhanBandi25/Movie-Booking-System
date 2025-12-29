package com.booking.movieticket.controller;

import com.booking.movieticket.dto.request.UserRequest;
import com.booking.movieticket.dto.response.UserResponse;
import com.booking.movieticket.entity.enums.UserRole;
import com.booking.movieticket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-users")
public class UserController {

    @Autowired
    private UserService userService;

    // user is created
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        UserResponse response= userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        List<UserResponse> list = userService.getAllUsers();
        return ResponseEntity.ok(list);
    }

    // find by user role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUserByRole(@PathVariable UserRole  role){
        List<UserResponse> responses = userService.getUserByRole(role);
        return ResponseEntity.ok(responses);
    }

    // find active and inactive users
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers(){
        return ResponseEntity.ok(userService.getActiveUsers());
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<UserResponse>> getInactiveUsers(){
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // update a user to activate or deactivate
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id){

        UserResponse response= userService.deactivateUser(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
