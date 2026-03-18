package com.chandrashekhar.clothingstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Long>{

	List<CartItem> findByUserId(Long userId);
	
	int countByUserId(Long userId); 
	
	Optional<CartItem> findByUserIdAndProductId(Long id,Long productId);
}
