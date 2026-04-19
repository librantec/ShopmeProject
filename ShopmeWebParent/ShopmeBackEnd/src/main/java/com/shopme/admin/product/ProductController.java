package com.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired private ProductService productService;
	@Autowired private BrandService brandService; // I-add ni para sa brands dropdown
	
	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);
		
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		// 1. Kuhaa ang lightweight list sa brands (ID ug Name ra)
		List<Brand> listBrands = brandService.listAll();
		
		// 2. Paghimo og bag-ong Product object para sa form binding
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);
        
        // 3. I-pass ang data sa Thymeleaf
        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        
        return "products/product_form";
	}
}
