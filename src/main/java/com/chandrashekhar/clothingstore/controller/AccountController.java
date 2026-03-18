package com.chandrashekhar.clothingstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.UserRepository;

@Controller
public class AccountController {
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/wishlist")
	public String wishlist() {
		return "wishlist";
	}
	
	@GetMapping("/addresses")
	public String addresses(Authentication auth, Model model) {
		String email = auth.getName();
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		model.addAttribute("user",user);
		
		return "addresses";
	}
	
	public String coupons() {
		return "coupons";
	}
}
