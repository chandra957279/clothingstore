package com.chandrashekhar.clothingstore.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.chandrashekhar.clothingstore.model.Order;
import com.chandrashekhar.clothingstore.model.OrderItem;
import com.chandrashekhar.clothingstore.model.OrderStatus;
import com.chandrashekhar.clothingstore.model.Product;
import com.chandrashekhar.clothingstore.repository.OrderItemRepository;
import com.chandrashekhar.clothingstore.repository.OrderRepository;
import com.chandrashekhar.clothingstore.repository.ProductRepository;
import com.chandrashekhar.clothingstore.service.ImageUploadService;




@Controller		
@RequestMapping("/admin")
public class AdminController {
	
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	
	@Autowired
	private ImageUploadService imageUploadService;

	
	
	public AdminController(ProductRepository productRepository,OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.productRepository =  productRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}
	
	@GetMapping("/dashboard")
	public String deshboard(Model model) {
		model.addAttribute("products",productRepository.findAll());
		model.addAttribute("product",new Product());
		
		return "admin-dashboard";
	}
	
	@PostMapping("/add-product")
	public String addProduct(@ModelAttribute Product product,
	                         @RequestParam("image") MultipartFile file) {

	    String imageUrl = imageUploadService.uploadImage(file);

	    product.setImageUrl(imageUrl);

	    productRepository.save(product);

	    return "redirect:/admin/dashboard";
	}
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable Long id) {
		
		productRepository.deleteById(id);
		
		return "redirect:/admin/dashboard";
	}
	
	
	@GetMapping("/orders")
	public String viewAllOrders(Model model) {
		
		List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();  
		
		 System.out.println("Orders found: " + orders.size());
		
		model.addAttribute("orders",orders);
		
		return "admin-orders";
	}
	
	@GetMapping("/order/ship/{id}")
	public String markShipped(@PathVariable Long id) {
		
	    Order order = orderRepository.findById(id).orElseThrow();
	    
	    order.setStatus(OrderStatus.SHIPPED);
	    
	    orderRepository.save(order);
	    
	    return "redirect:/admin/orders";
	}

	@GetMapping("/order/deliver/{id}")
	public String markDelivered(@PathVariable Long id) {
		
	    Order order = orderRepository.findById(id).orElseThrow();
	    
	    order.setStatus(OrderStatus.DELIVERED);
	    
	    orderRepository.save(order);
	    
	    return "redirect:/admin/orders";
	}
	
	@GetMapping("/order/{id}")
	public String viewOrderDetails(@PathVariable Long id, Model model) {

	    Order order = orderRepository.findById(id).orElseThrow();

	    List<OrderItem> items = orderItemRepository.findByOrderId(id);

	    model.addAttribute("order", order);
	    model.addAttribute("items", items);

	    return "admin-order-details";
	}
	
}
