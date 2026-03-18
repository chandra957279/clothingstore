package com.chandrashekhar.clothingstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.UserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public String profile(Authentication auth , Model model) {
		String email = auth.getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(null);
		
		model.addAttribute("user", user);
		
		return "profile";
	}
	
	@PostMapping("/update")
	public String updateProfile(@ModelAttribute User formUser, Authentication auth) {
		String email = auth.getName();
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		
			
			user.setMobileNumber(formUser.getMobileNumber());
			user.setAddressLine1(formUser.getAddressLine1());
			user.setAddressLine2(formUser.getAddressLine2());
			user.setCity(formUser.getCity());
			user.setState(formUser.getState());
			user.setPincode(formUser.getPincode());
			
			userRepository.save(user);
		
		return "redirect:/profile";
	}
}
