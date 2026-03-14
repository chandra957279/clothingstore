package com.chandrashekhar.clothingstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findByUserId(Long userId);
	List<Order> findAllByOrderByOrderDateDesc();
}
