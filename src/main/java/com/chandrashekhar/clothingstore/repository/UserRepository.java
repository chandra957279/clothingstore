package com.chandrashekhar.clothingstore.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String email);
	
	Optional<User> findByResetToken(String token);
	
}
