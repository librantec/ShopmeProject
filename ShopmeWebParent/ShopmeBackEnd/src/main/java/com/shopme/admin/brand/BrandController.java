package com.shopme.admin.brand;

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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/brands")
	public String listAll(Model model) {
		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("listBrands", listBrands);
		
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "brands/brand_form";
	}
	
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, 
	        @RequestParam("fileImage") MultipartFile multipartFile,
	        RedirectAttributes ra) throws IOException {
	    
	    if (!multipartFile.isEmpty()) {
	    	
	        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	        brand.setLogo(fileName);
	        
	        Brand savedBrand = brandService.save(brand);
	        String uploadDir = "../brand-logos/" + savedBrand.getId();
	        
	        // I-delete ang karaan nga folder/files sa dili pa i-save ang bag-o
	        FileUploadUtil.cleanDir(uploadDir);
	        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	        
	    } else {
	    	
	    	brandService.save(brand);
	    }
	    
	    ra.addFlashAttribute("message", "The brand has been saved successfully.");
	    
	    return "redirect:/brands";
	}
	
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			Brand brand = brandService.get(id); // Kinahanglan ka og get() method sa service
	        List<Category> listCategories = categoryService.listCategoriesUsedInForm(); // Para sa dropdown/list sa categories
	        
	        model.addAttribute("brand", brand);
	        model.addAttribute("listCategories", listCategories);
	        model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

	        return "brands/brand_form";
		
		} catch (BrandNotFoundException ex) {
		
			ra.addFlashAttribute("message", ex.getMessage());
	        return "redirect:/brands";
		}
	}
	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable Integer id, RedirectAttributes ra) {
	    try {
	    	brandService.delete(id);
	        
	        // I-delete sab ang folder sa logos para limpyo
	        String brandDir = "../brand-logos/" + id;
	        FileUploadUtil.removeDir(brandDir); 
	        
	        ra.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");
	    
	    } catch (BrandNotFoundException ex) {
	    
	    	ra.addFlashAttribute("message", ex.getMessage());
	    
	    }
	    
	    return "redirect:/brands";
	}

	
}
