package com.chandrashekhar.clothingstore.config;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService	{

	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not found"));
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole())));
		
	}
}
