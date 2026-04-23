package com.shopme.admin.user;

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
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

	@Autowired private UserService service;

	@GetMapping("/users")
	public String listAll(Model model) {
		// 1. Tawgon ang Service para makuha ang listahan sa Users
		 List<User> listUsers = service.listAll(); 
		
		// 2. I-sulod ang listahan sa "Model" (ang basket) para madala sa HTML
		 model.addAttribute("listUsers", listUsers); 
		
		// 3. I-return ang ngalan sa imong HTML file (users.html)
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles(); // Siguruha nga naa sab kay roles
	    
	    User user = new User();
	    user.setEnabled(true); // Default nga naka-check ang enabled
	    
	    model.addAttribute("user", user); // KANI ANG KULANG!
	    model.addAttribute("listRoles", listRoles);
	    model.addAttribute("pageTitle", "Create New User");
	    
		return "users/user_form";
	}
	
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
	        @RequestParam("image") MultipartFile multipartFile) {
	    
	    try {
	        if (!multipartFile.isEmpty()) {
	            // 1. Limpyohan ang filename
	            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	            user.setPhotos(fileName);
	            
	            // 2. I-save ang user (Kausa ra ni, bai!)
	            User savedUser = service.save(user);
	            
	            // 3. I-handle ang folder ug file
	            String uploadDir = "../user-photos/" + savedUser.getId();
	            FileUploadUtil.cleanDir(uploadDir);
	            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	            
	        } else {
	            // 4. Kon walay bag-ong photo, i-save ra gihapon ang user details
	            service.save(user);
	        }

	        // Kon molampus, ipakita ang success message
	        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
	        
	    } catch (Exception ex) {
	    	String message = "Could not save the user.";
	    	
	    	// I-check kung ang error ba kay "Data too long"
	    	if (ex.getMessage().contains("Data truncation")) {
	    		message = "The photo File Name is too long. Please rename the file and try again.";
				
			} else {
				
				/* Para sa ubang errors, generic message lang para dili shock ang user */
		        message = "There was a problem saving the user. Please contact support.";
			}
	    	
			/*
			 * KANI ANG SAFETY NET, BAI! Imbis "White Page Error", i-redirect balik sa
			 * listahan nga naay error message.
			 */
	        redirectAttributes.addFlashAttribute("message", message);
	        
			/*
			 * KANI ANG IMPORTANTE: Ibalik siya sa form (pananglitan sa /users/new) Imbis
			 * "return "redirect:/users";", ato kining i-return sulod sa catch.
			 */
	        return "redirect:/users/new";
	    }

	    return "redirect:/users";
	}
	

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		
		try {
			User user = service.get(id);
	        List<Role> listRoles = service.listRoles();
	        
	        model.addAttribute("user", user);
	        model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
	        model.addAttribute("listRoles", listRoles);
	        
	        return "users/user_form"; // Gamiton nato ang karaan nga form pero naay data
			
		} catch (UserNotFoundException ex) {
			// Kon wala makit-i ang ID, i-redirect balik sa listahan nga naay error message
	        redirectAttributes.addFlashAttribute("message", ex.getMessage());
	        return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		
		try {
			service.delete(id); // I-delete sa DB
			
			// tawagon ang removeDir aron ma delete ang folder sa image.
	        String userPhotosDir = "user-photos/" + id;
	        FileUploadUtil.removeDir(userPhotosDir);
	        
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
			
		} catch (UserNotFoundException ex) {
			
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/users";
	}
	
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable Integer id,
	        @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
	    
	    service.updateUserEnabledStatus(id, enabled);
	    String status = enabled ? "enabled" : "disabled";
	    String message = "The user ID " + id + " has been " + status;
	    
	    redirectAttributes.addFlashAttribute("message", message);
	    
	    return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll(); // Kuhaon tanan users sa DB
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
	}
}
