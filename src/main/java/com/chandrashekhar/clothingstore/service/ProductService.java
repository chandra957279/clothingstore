package com.chandrashekhar.clothingstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chandrashekhar.clothingstore.model.Product;
import com.chandrashekhar.clothingstore.repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	public List<Product> getAllProducts(){
		return productRepository.findAll();
	}
	
	public List<Product> getProductByCategory(String category){
		return productRepository.findByCategory(category);
	}
}