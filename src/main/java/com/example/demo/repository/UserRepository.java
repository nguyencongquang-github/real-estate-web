package com.example.demo.repository;


import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
//    @NonNull
//    Optional<User> findById(@NonNull Integer id);
    boolean existsByUsername(String username);
    List<User> findByUsernameContainingOrEmailContaining(String username, String password);

}
