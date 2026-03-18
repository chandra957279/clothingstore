package com.chandrashekhar.clothingstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.chandrashekhar.clothingstore.model.Order;
import com.chandrashekhar.clothingstore.model.OrderItem;
import com.chandrashekhar.clothingstore.model.OrderStatus;
import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.OrderItemRepository;
import com.chandrashekhar.clothingstore.repository.OrderRepository;
import com.chandrashekhar.clothingstore.repository.ProductRepository;
import com.chandrashekhar.clothingstore.repository.UserRepository;
import com.razorpay.RazorpayClient;


@Controller
public class PaymentController {
		
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	
	@Autowired
	private UserRepository  userRepository;
	
	@Value("${razorpay.key}")
	private String razorpayKey;
	
	@Value("${razorpay.secret}")
	private String razorpaySecret;
	
	public PaymentController(OrderRepository orderRepository, OrderItemRepository orderItemRepository,  ProductRepository productRepository) {
		this.orderRepository = orderRepository; 
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}
	@GetMapping("/payment/{orderId}")
	public String paymentPage(@PathVariable Long orderId, Model model, Authentication auth) {
		Order order = orderRepository.findById(orderId).orElseThrow();
		
		String email = auth.getName();
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		if(user == null || user.getAddressLine1() == null || user.getAddressLine1().isEmpty()) {
			return 	"redirect:/profile";
		}
		
		List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
		
		model.addAttribute("order",order);
		model.addAttribute("items", items);
		
		return "payment";
	}
	
	@GetMapping("/payment/cod/{orderId}")
	public String codPayment(@PathVariable Long orderId){

	    Order order = orderRepository.findById(orderId).orElseThrow();

	    order.setStatus(OrderStatus.PENDING);

	    orderRepository.save(order);

	    return "order-success";
	}
	
	@PostMapping("/payment/process")
	public String processPayment(@RequestParam Long orderId,
	                             @RequestParam String method){

	    Order order = orderRepository.findById(orderId).orElseThrow();

	    if(method.equals("COD")){

	        order.setStatus(OrderStatus.PENDING);
	        orderRepository.save(order);

	        return "order-success";
	    }
	    
	    if(method.equals("ONLINE")) {
	    	
	    	return "redirect:/payment/online/" + orderId;
	    }

	    return "redirect:/";
	}
	@GetMapping("/payment/online/{orderId}")
	public String onlinePayment(@PathVariable Long orderId, Model model) {
		
		Order order = orderRepository.findById(orderId).orElseThrow();
		
		model.addAttribute("order",order);
		
		return "online-payment";
	}
	
	@PostMapping("/create-order")
	@ResponseBody
	public Map<String, Object> createOrder(@RequestParam int amount) throws Exception {

	    RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

	    JSONObject options = new JSONObject();
	    options.put("amount", amount * 100);
	    options.put("currency", "INR");
	    options.put("receipt", "order_rcptid_11");

	    com.razorpay.Order order = client.orders.create(options);

	    Map<String, Object> response = new HashMap<>();
	    response.put("id", order.get("id"));
	    response.put("amount", order.get("amount"));
	    response.put("currency", order.get("currency"));

	    return response;
	}
}
