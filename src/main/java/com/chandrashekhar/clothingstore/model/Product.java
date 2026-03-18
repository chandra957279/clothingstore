package com.chandrashekhar.clothingstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Name is required")
	private String name;
	
	@Column(length = 10000)
	private String description; 	
	
	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private double price;
	
	@NotBlank(message = "Category is required")
	private String category;
	
	private String imageUrl;
	
	@NotNull(message = "Stock is required")
	@Min(value = 0, message = "Stock cannot be negative")
	private int stock;
	
	private Double rating;
	
	private Integer reviewCount;
	
	private Double originalPrice;
	
	private Integer discount;
	
	private String offer;
	
	private String deliveryDate;
}
