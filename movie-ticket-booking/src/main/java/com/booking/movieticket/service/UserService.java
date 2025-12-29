package com.booking.movieticket.service;

import com.booking.movieticket.dto.request.UserRequest;
import com.booking.movieticket.dto.response.UserResponse;
import com.booking.movieticket.entity.enums.UserRole;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();

    List<UserResponse> getUserByRole(UserRole role);

    List<UserResponse> getActiveUsers();
    List<UserResponse> getInactiveUsers();

    UserResponse updateUser(Long id,UserRequest request);

    UserResponse activateUser(Long id);
    UserResponse deactivateUser(Long id);

    void deleteUser(Long userId);
}
