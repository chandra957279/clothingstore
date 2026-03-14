package com.chandrashekhar.clothingstore.controller;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequestMapping("/cart")
public class CartController {
	
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	
	
	public CartController(CartRepository cartRepository, UserRepository userRepository,ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}
	
	@PostMapping("/add")
	public String addToCart(@RequestParam Long productId, Authentication authentication) {
		
		String email = authentication.getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));
		
		Optional<CartItem> existingItem = cartRepository.findByUserIdAndProductId(user.getId(), productId);
		
		if(existingItem.isPresent()) {
			CartItem item = existingItem.get();
			item.setQuantity(item.getQuantity()+1);
			cartRepository.save(item);
		}else {
		CartItem newitem = new CartItem();
		newitem.setProductId(productId);
		newitem.setUserId(user.getId());
		newitem.setQuantity(1);
		
		cartRepository.save(newitem);
		}
		return "redirect:/";
	}
	
	@GetMapping("/view")
	public String viewCart(Model model, Authentication authentication) {
		String email = authentication.getName();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    java.util.List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

	    double total = 0;

	    for (CartItem item : cartItems) {
	        Product product = productRepository.findById(item.getProductId()).orElse(null);
	        if (product != null) {
	            item.setProduct(product); // temporary attach product
	            total += product.getPrice() * item.getQuantity();
	        }
	    }

	    model.addAttribute("cartItems", cartItems);
	    model.addAttribute("totalAmount", total);

	    return "cart";
	}
	
	@GetMapping("/remove/{id}")
	public String  removeFromCart(@PathVariable Long id) {
		cartRepository.deleteById(id);
		
		return "redirect:/cart/view";
	}
	
	@GetMapping("/increase/{id}")
	public String increaseQuantity(@PathVariable Long id) {
		CartItem item = cartRepository.findById(id).orElseThrow();
		item.setQuantity(item.getQuantity()+1);
		cartRepository.save(item);
		return "redirect:/cart/view";
	}
	
	@GetMapping("/decrease/{id}")
	public String decreaseQuantity(@PathVariable Long id) {

	    CartItem item = cartRepository.findById(id).orElseThrow();

	    if (item.getQuantity() > 1) {
	        item.setQuantity(item.getQuantity() - 1);
	        cartRepository.save(item);
	    } else {
	        cartRepository.deleteById(id);
	    }

	    return "redirect:/cart/view";
	}
	
	@PostMapping("/checkout")
	public String checkout(Authentication authentication) {

	    String email = authentication.getName();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User Not Found!"));

	    List<CartItem> cartItems = cartRepository.findByUserId(user.getId());

	    if (cartItems.isEmpty()) {
	        return "redirect:/cart/view";
	    }

	    double total = 0;

	    Order order = new Order();
	    order.setUserId(user.getId());
	    order.setOrderDate(LocalDateTime.now());
	    order.setStatus(OrderStatus.PENDING);

	    // Save order first to generate order ID
	    order = orderRepository.save(order);

	    for (CartItem item : cartItems) {

	        Product product = productRepository.findById(item.getProductId())
	                .orElseThrow(() -> new RuntimeException("Product not found"));

	        // Check stock
	        if (product.getStock() < item.getQuantity()) {
	            return "redirect:/cart/view?error=stock";
	        }

	        // Reduce stock
	        product.setStock(product.getStock() - item.getQuantity());
	        productRepository.save(product);

	        // Create order item
	        OrderItem orderItem = new OrderItem();
	        orderItem.setOrderId(order.getId());
	        orderItem.setProduct(product);
	        orderItem.setQuantity(item.getQuantity());
	        orderItem.setPrice(product.getPrice());

	        orderItemRepository.save(orderItem);

	        total += product.getPrice() * item.getQuantity();
	    }

	    // Update order total
	    order.setTotalAmount(total);
	    orderRepository.save(order);

	    // Clear cart
	    cartRepository.deleteAll(cartItems);

	    return "redirect:/payment/" + order.getId();
	}
}
