package com.ex.emailapi.repositories;


import com.ex.emailapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for user data
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
