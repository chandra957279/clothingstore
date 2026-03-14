package com.chandrashekhar.clothingstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	List<Review> findByProductId(Long productId);
}
