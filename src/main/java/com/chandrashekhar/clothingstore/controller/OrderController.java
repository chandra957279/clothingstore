package com.chandrashekhar.clothingstore.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chandrashekhar.clothingstore.model.CartItem;
import com.chandrashekhar.clothingstore.model.Order;
import com.chandrashekhar.clothingstore.model.OrderItem;
import com.chandrashekhar.clothingstore.model.OrderStatus;
import com.chandrashekhar.clothingstore.model.Product;
import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.CartRepository;
import com.chandrashekhar.clothingstore.repository.OrderItemRepository;
import com.chandrashekhar.clothingstore.repository.OrderRepository;
import com.chandrashekhar.clothingstore.repository.ProductRepository;
import com.chandrashekhar.clothingstore.repository.UserRepository;

@Controller
public class OrderController {
	
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
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
	
	@GetMapping("/order/place")
	public String placeOrder(Authentication auth) {
		
		String email = auth.getName();
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		if(user.getAddressLine1() == null || user.getAddressLine1().isEmpty()) {
			return "redirect:/profile?addressRequired=true";
		}
		
		return "redirect:/order/create";
	}
	
	@GetMapping("/order/create")
	public String createOrder(Authentication auth){

	    String email = auth.getName();

	    User user = userRepository.findByEmail(email).orElse(null);

	    List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

	    if(cartItems.isEmpty()){
	        return "redirect:/cart";
	    }

	    double total = 0;

	    Order order = new Order();
	    order.setUserId(user.getId());
	    order.setStatus(OrderStatus.PENDING);
	    order.setOrderDate(java.time.LocalDateTime.now());

	    for(CartItem c : cartItems){

	        Product product = productRepository
	                .findById(c.getProductId())
	                .orElseThrow();

	        total += product.getPrice() * c.getQuantity();
	    }

	    order.setTotalAmount(total);

	    orderRepository.save(order);

	    for(CartItem c : cartItems){

	        Product product = productRepository
	                .findById(c.getProductId())
	                .orElseThrow();

	        OrderItem item = new OrderItem();

	        item.setOrderId(order.getId());
	        item.setProduct(product);
	        item.setQuantity(c.getQuantity());
	        item.setPrice(product.getPrice());

	        orderItemRepository.save(item);
	    }

	    cartRepository.deleteAll(cartItems);

	    return "redirect:/payment/" + order.getId();
	}
}	



