package com.shopme.admin.category;

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
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories", listCategories);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		// 1. Kuhaon nato tanang kategorya para sa Parent dropdown
		List<Category> listCategories = service.listCategoriesUsedInForm();
	    
	    model.addAttribute("category", new Category());
	    model.addAttribute("listCategories", listCategories);
	    model.addAttribute("pageTitle", "Create New Category");
	    
	    return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
	        @RequestParam("fileImage") MultipartFile multipartFile,
	        RedirectAttributes ra) throws IOException {
	    
	    if (!multipartFile.isEmpty()) {
	    	
	        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	        category.setImage(fileName);
	        
	        Category savedCategory = service.save(category);
	        String uploadDir = "../category-images/" + savedCategory.getId();
	        
	        // I-delete ang karaan nga folder/files sa dili pa i-save ang bag-o
	        FileUploadUtil.cleanDir(uploadDir);
	        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	        
	    } else {
	    	
	        service.save(category);
	    }
	    
	    ra.addFlashAttribute("message", "The category has been saved successfully.");
	    
	    return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable Integer id, Model model, RedirectAttributes ra) {
	    try {
	        Category category = service.get(id); // Kinahanglan ka og get() method sa service
	        List<Category> listCategories = service.listCategoriesUsedInForm();

	        model.addAttribute("category", category);
	        model.addAttribute("listCategories", listCategories);
	        model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

	        return "categories/category_form";
	    } catch (CategoryNotFoundException ex) {
	        ra.addFlashAttribute("message", ex.getMessage());
	        return "redirect:/categories";
	    }
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable Integer id,
	        @PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes) {
	    
	    service.updateCategoryEnabledStatus(id, enabled);
	    String status = enabled ? "enabled" : "disabled";
	    String message = "The category ID " + id + " has been " + status;
	    
	    redirectAttributes.addFlashAttribute("message", message);
	    
	    return "redirect:/categories";
	}
	
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
	    try {
	        service.delete(id);
	        
	        // I-delete sad ang folder sa images (kon naa na kay FileUploadUtil)
	        String categoryDir = "../category-images/" + id;
	        FileUploadUtil.removeDir(categoryDir);
	        
	        redirectAttributes.addFlashAttribute("message", 
	            "The category ID " + id + " has been deleted successfully");
	            
	    } catch (CategoryNotFoundException ex) {
	        redirectAttributes.addFlashAttribute("message", ex.getMessage());
	    } catch (Exception ex) {
	        // DIRI NIMO DAKPON ANG FOREIGN KEY ERROR, BAI!
	        redirectAttributes.addFlashAttribute("message", 
	            "Could not delete the category (ID: " + id + ") because it has sub-categories.");
	    }

	    return "redirect:/categories";
	}

}
