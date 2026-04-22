package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserService service;
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		
		model.addAttribute("user", user);
		
		return "users/account_form"; // Mao ni ang HTML file sa imong account details
	}
	
	
	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
	        @RequestParam("image") MultipartFile multipartFile) {
	    
	    try {
	        if (!multipartFile.isEmpty()) {
	            // 1. Limpyohan ang filename
	            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	            user.setPhotos(fileName);
	            
	            // 2. I-save ang user (Kausa ra ni, bai!)
	            User savedUser = service.updateAccount(user);
	            
	            // 3. I-handle ang folder ug file
	            String uploadDir = "../user-photos/" + savedUser.getId();
	            FileUploadUtil.cleanDir(uploadDir);
	            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
	            
	        } else {
	            // 4. Kon walay bag-ong photo, i-save ra gihapon ang user details
	            service.updateAccount(user);
	        }
	        
	        loggedUser.setFirstName(user.getFirstName());
	        loggedUser.setLastName(user.getLastName());
	        
	        // Kung gusto nimo i-update sab ang photo sa top right:
	        //loggedUser.setPhotos(user.getPhotos());
	        // Kon molampus, ipakita ang success message
	        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");
	        
	    } catch (Exception ex) {
	    	String message = "Could not save the user.";
	    	
	    	// I-check kung ang error ba kay "Data too long"
	    	if (ex.getMessage().contains("Data truncation")) {
	    		message = "The photo File Name is too long. Please rename the file and try again.";
				
			} else {
				
				/* Para sa ubang errors, generic message lang para dili shock ang user */
		        message = "There was a problem updating the user. Please contact support.";
			}
	    	
			/*
			 * KANI ANG SAFETY NET, Imbis "White Page Error", i-redirect balik sa
			 * listahan nga naay error message.
			 */
	        redirectAttributes.addFlashAttribute("message", message);
	        
			/*
			 * KANI ANG IMPORTANTE: Ibalik siya sa form (pananglitan sa /users/new) Imbis
			 * "return "redirect:/users";", ato kining i-return sulod sa catch.
			 */
	        return "redirect:/account";
	    }

	    return "redirect:/account";
	}
}
