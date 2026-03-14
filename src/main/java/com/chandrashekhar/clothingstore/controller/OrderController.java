package com.chandrashekhar.clothingstore.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chandrashekhar.clothingstore.model.Order;
import com.chandrashekhar.clothingstore.model.OrderItem;
import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.OrderItemRepository;
import com.chandrashekhar.clothingstore.repository.OrderRepository;
import com.chandrashekhar.clothingstore.repository.UserRepository;

@Controller
public class OrderController {
	
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	
	public OrderController(UserRepository userRepository,OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}
	
	@GetMapping("/orders")
	public String viewOrders(Model model, Authentication authentication) {

	    String email = authentication.getName();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User Not Found!"));

	    List<Order> orders = orderRepository.findByUserId(user.getId())
	            .stream()
	            .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
	            .toList();

	    model.addAttribute("orders", orders);

	    return "orders";
	}
	
	@GetMapping("/orders/{id}")
	public String viewOrderDetails(@PathVariable Long id, Model model,Authentication authentication) {
		String email = authentication.getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found!"));
		
		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order Not Found!"));
		
		if(!order.getUserId().equals(user.getId())) {
			return "redirect:/orders";
		}
		List<OrderItem> items = orderItemRepository.findByOrderId(id);
		
		model.addAttribute("order",order);
		model.addAttribute("items",items);
		
		return "order-details";
	}
	
	
}	



