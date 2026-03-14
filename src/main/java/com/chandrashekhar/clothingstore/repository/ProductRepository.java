package com.chandrashekhar.clothingstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	List<Product> findByCategory(String category);
	
	List<Product> findByNameContainingIgnoreCase(String keyword);
	
	List<Product> findByCategoryAndNameContainingIgnoreCase(String category, String keyword);
}
