package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
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
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipart) throws IOException {
		
			// 1. I-set ang Main Image name kon naay gi-upload
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
	        product.setMainImage(fileName);
		}
		// 2. I-set ang Extra Images names (I-update ang Product object)
	    // Note: Kinahanglan naa kay 'setExtraImages' logic sa imong ProductService/Entity
	    setExtraImageNames(extraImageMultipart, product);
	    
	    // 3. I-save ang Product sa DB para makakuha ta og ID
	    Product savedProduct = productService.save(product);
	    
	    // 4. I-save ang mga Files sa folder
	    saveUploadedImages(mainImageMultipart, extraImageMultipart, savedProduct);
	    ra.addFlashAttribute("message", "The product has been saved successfully.");
	    
	    return "redirect:/products";
	}
	
	// Helper method para limpyo ang saveProduct
	private void setExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
	    if (extraImageMultipart.length > 0) {
	        for (MultipartFile multipartFile : extraImageMultipart) {
	            if (!multipartFile.isEmpty()) {
	                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	                product.addExtraImage(fileName); // Siguroha nga naa ni sa imong Product Entity
	            }
	        }
	    }
	}
	
	private void saveUploadedImages(MultipartFile mainImageMultipart, 
	        MultipartFile[] extraImageMultipart, Product savedProduct) throws IOException {
	    
	    String uploadDir = "../product-images/" + savedProduct.getId();
	    
	    // I-save ang Main Image
	    if (!mainImageMultipart.isEmpty()) {
	        String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
	        FileUploadUtil.cleanDir(uploadDir); // Limpyo una
	        FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
	    }

	    // I-save ang Extra Images
	    if (extraImageMultipart.length > 0) {
	        String extraUploadDir = uploadDir + "/extras";
	        for (MultipartFile multipartFile : extraImageMultipart) {
	            if (multipartFile.isEmpty()) continue;
	            
	            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	            FileUploadUtil.saveFile(extraUploadDir, fileName, multipartFile);
	        }
	    }
	}
	
	
	/* Enabled status method */
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	// Delete method
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable Integer id,
			Model model, RedirectAttributes redirectAttributes) {
		
		try {
			productService.delete(id);
			
			// KANI NGA MGA STATEMENT ANG I-DUGANG:
	        String productExtraImagesDir = "../product-images/" + id + "/extras";
	        String productImagesDir = "../product-images/" + id;
	        
	        // 1. Limpyohan ug tangtangon ang 'extras' folder
	        FileUploadUtil.removeDir(productExtraImagesDir);
	        
	        // 2. Limpyohan ug tangtangon ang main product folder
	        FileUploadUtil.removeDir(productImagesDir);
			
	        redirectAttributes.addFlashAttribute("message", "The Product ID " + id + " has been deleted successfully.");
		
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/products";
	}
	
}
