package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.UserRequest;
import com.booking.movieticket.dto.response.UserResponse;
import com.booking.movieticket.entity.User;
import com.booking.movieticket.entity.enums.UserRole;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.InvalidRequestException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.UserRepository;
import com.booking.movieticket.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public UserResponse createUser(UserRequest request) {

        if(request.getEmail() != null && userRepository.existsByEmail(request.getEmail())){
            throw  new DuplicateResourceException("Email already exists");
        }
        if(userRepository.existsByPhone(request.getPhone())){
            throw new DuplicateResourceException("Phone Number already exists");
        }

        User user = modelMapper.map(request,User.class);
        user.setActive(true);
        User saved= userRepository.save(user);
        return modelMapper.map(saved,UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user=  getUser(id);
        return modelMapper.map(user,UserResponse.class);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map( user -> modelMapper.map(user,UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUserByRole(UserRole role) {
        return userRepository.findByRoleContainingIgnoreCase(role)
                .stream()
                .map(user-> modelMapper.map(user,UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByActiveTrue()
                .stream()
                .map(user -> modelMapper.map(user,UserResponse.class))
                .toList();
    }

    @Override
    public List<UserResponse> getInactiveUsers() {
        return userRepository.findByActiveFalse()
                .stream()
                .map(user -> modelMapper.map(user,UserResponse.class))
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {

        User user= getUser(id);

        if (!user.getActive()) {
            throw new InvalidRequestException("Inactive user cannot be updated");
        }

        String email = request.getEmail().toLowerCase().trim();

        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone already exists");
        }
        user.setName(request.getName());
        user.setEmail(email);
        user.setPhone(request.getPhone());

        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    public UserResponse activateUser(Long id) {
        User user= getUser(id);

        if (user.getActive()) {
            throw new InvalidRequestException("User already active");
        }
        user.setActive(true);
        User update=userRepository.save(user);

        return modelMapper.map(update,UserResponse.class);
    }

    @Override
    public UserResponse deactivateUser(Long id) {
        User user = getUser(id);

        if (!user.getActive()) {
            throw new InvalidRequestException("User already deactivated");
        }
        user.setActive(false);
        User updateUser= userRepository.save(user);

        return modelMapper.map(updateUser,UserResponse.class);
    }

    @Override
    public void deleteUser(Long userId) {
        User user= getUser(userId);

        userRepository.delete(user);
    }

    private User getUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " +id));
    }
}
