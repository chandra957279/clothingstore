package com.chandrashekhar.clothingstore.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chandrashekhar.clothingstore.model.OrderItem;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
	List<OrderItem> findByOrderId(Long orderId);
}
