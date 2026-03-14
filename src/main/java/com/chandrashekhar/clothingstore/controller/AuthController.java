package com.chandrashekhar.clothingstore.controller;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.UserRepository;
import com.chandrashekhar.clothingstore.service.EmailService;
import com.chandrashekhar.clothingstore.service.UserService;



@Controller
public class AuthController {
	
	private final UserService userService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	public AuthController(UserService userService,  UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
		this.userService = userService;
		this.userRepository =userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}
	
	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String register(@ModelAttribute User user) {
		userService.registerUser(user);
		
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		return "forgot-password";
	}
	
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email , Model model) {
		
		User user = userRepository.findByEmail(email).orElse(null);
		
		if(user == null) {
			model.addAttribute("error","Email not found");
			return "forgot-password";
		}
		
		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		userRepository.save(user);
		
		emailService.sendResetPasswordEmail(user.getEmail(), token);
		
		model.addAttribute("message", "Password reset link has been sent to your email.");
		
		
		return "forgot-password";
	}
	
	@GetMapping("/reset-password/{token}")
	public String resetPasswordPage(@PathVariable String token , Model model) {
		
		User user = userRepository.findByResetToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
		
		model.addAttribute("token",token);
		
		return "reset-password";
	}
	
	
	@PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password) {

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);

        userRepository.save(user);

        return "redirect:/login?resetSuccess";
    }
	
	
	
}
