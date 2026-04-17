package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// Ang RestController mo-return lang og "OK" o "Duplicated" (text), dili HTML page.
@RestController
public class UserRestController {
	@Autowired
	private UserService service;
	
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}
}
