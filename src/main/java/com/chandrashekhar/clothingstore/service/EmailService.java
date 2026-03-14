package com.chandrashekhar.clothingstore.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender =mailSender; 
	}
	
	public void sendResetPasswordEmail(String toEmail, String token) {
		
		String resetLink = "http://localhost:8080/reset-password/" + token;
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(toEmail);
		message.setSubject("Password Reset Request");
		message.setText("Click this link to reset your password:\n" +  resetLink);
		
		mailSender.send(message);
	}
}
