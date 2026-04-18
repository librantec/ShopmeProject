package com.shopme.admin.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController // Mao ni ang nag-ingon nga REST API ni, dili Thymeleaf
public class SampleRestController {

	@GetMapping("/api/test") // Mao ni ang URL nga imong i-access
    public Map<String, String> sayHello() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Success");
        response.put("message", "Maayong gabie, bai! Mao ni ang data gikan sa REST API.");
        response.put("stack", "Java Spring Boot + React");
        
        return response; // I-convert ni sa Spring Boot automatically ngadto sa JSON
    }
}