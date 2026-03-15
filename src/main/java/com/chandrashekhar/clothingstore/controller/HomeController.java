package com.chandrashekhar.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chandrashekhar.clothingstore.model.Product;
import com.chandrashekhar.clothingstore.model.Review;
import com.chandrashekhar.clothingstore.model.User;
import com.chandrashekhar.clothingstore.repository.ProductRepository;
import com.chandrashekhar.clothingstore.repository.ReviewRepository;
import com.chandrashekhar.clothingstore.repository.UserRepository;
import com.chandrashekhar.clothingstore.service.ProductService;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    
    @Autowired
    private ProductService productService;

    public HomeController(ProductRepository productRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    // ================= HOME PAGE =================

    @GetMapping("/")
    public String home(Model model,
                       Authentication authentication,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category) {

        List<Product> products;

        // Search + Category filter
        if(keyword != null && !keyword.isEmpty() && category != null && !category.isEmpty()) {

            products = productRepository
                    .findByCategoryAndNameContainingIgnoreCase(category, keyword);

        } 
        else if(keyword != null && !keyword.isEmpty()) {

            products = productRepository
                    .findByNameContainingIgnoreCase(keyword);

        }
        else if(category != null && !category.isEmpty()) {

            products = productRepository
                    .findByCategory(category);

        }
        else {

            products = productRepository.findAll();

        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        // Logged in user
        if(authentication != null &&
           authentication.isAuthenticated() &&
           !authentication.getName().equals("anonymousUser")) {

            String email = authentication.getName();

            User user = userRepository.findByEmail(email).orElse(null);

            if(user != null) {
                model.addAttribute("loggedInUser", user);
            }
        }

        return "index";
    }


    // ================= PRODUCT DETAILS PAGE =================

    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<Review> reviews = reviewRepository.findByProductId(id);
        
        double avgRating = 0;
        
        if(!reviews.isEmpty()) {
        	avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        }
        
        model.addAttribute("product", product);
        model.addAttribute("reviews",reviews);
        model.addAttribute("avgRating",avgRating);
        
        
        return "product-details";
    }
    
    @PostMapping("/review/add")
    public String addReview(@RequestParam Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Authentication authentication) {

        String userName = authentication.getName();

        Review review = Review.builder()
                .productId(productId)
                .rating(rating)
                .comment(comment)
                .userName(userName)
                .build();

        reviewRepository.save(review);

        return "redirect:/product/" + productId;
    }
}