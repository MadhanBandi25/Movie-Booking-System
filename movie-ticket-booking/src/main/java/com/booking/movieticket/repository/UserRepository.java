package com.booking.movieticket.repository;

import com.booking.movieticket.entity.User;
import com.booking.movieticket.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<User> findByRoleContainingIgnoreCase(UserRole role);

    List<User> findByActiveTrue();
    List<User> findByActiveFalse();

}
