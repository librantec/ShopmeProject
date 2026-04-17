package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TestEntityManager entityManager; // Gamiton nato ni para pag-find sa Role
	
	@Test
	public void testCreateNewUserWithOneRole() {
		// 1. Kuhaon nato ang 'Admin' role gikan sa DB (ID = 1 base sa imong gihimo ganina)
		Role roleAdmin = entityManager.find(Role.class, 1);
		
		// 2. Maghimo og bag-ong User object
		User userNamHM = new User("nam@codejava.net", "nam2020", "Nam", "Ha Minh");
		
		// 3. I-assign ang Role ngadto sa User
		userNamHM.addRole(roleAdmin);
		
		// 4. I-save sa database
		User savedUser = userRepo.save(userNamHM);
		
		// 5. I-verify kon na-save ba gyud
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userRavi = new User("ravi@gmail.com", "ravi2020", "Ravi", "Kumar");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		
		userRavi.addRole(roleEditor);
		userRavi.addRole(roleAssistant);
		
		
		// 4. I-save sa database
		User savedUser = userRepo.save(userRavi);
		
		// 5. I-verify kon na-save ba gyud
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		// 1. Kuhaon ang tanang users gikan sa database
	    Iterable<User> listUsers = userRepo.findAll();
	    
	    // 2. I-print sa console ang matag user para makita nimo ang details
	    listUsers.forEach(user -> System.out.println(user));
	    
	    // 3. I-assert (check) kon naa ba gyu'y sulod ang listahan
	    assertThat(listUsers).hasSizeGreaterThan(0);
	}
	
	@Test
	public void testGetUserById() {
		// 1. Sulayi pagkuha ang user nga naay ID = 1 (si Nam Ha Minh)
	    // Ang findById mobalik og Optional, maong mogamit ta og .get()
	    User userNam = userRepo.findById(1).get();
	    
	    // 2. I-print sa console para makita nimo ang details
	    System.out.println(userNam);
	    
	    // 3. I-assert (check) kon tinuod ba nga nakuha ang user
	    assertThat(userNam).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		// 1. Kuhaon una nato ang user gikan sa database (ID 1 - Nam Ha Minh)
		User userNam = userRepo.findById(1).get();
		
		// 2. Usbon nato ang iyang status (Enabled) ug ang email
		userNam.setEnabled(true);
		userNam.setEmail("nam_updated@codejava.net");
		
		// 3. I-save balik. Ang save() method sa Spring Data JPA 
		// maoy mo-detect kon ang ID naa na sa DB, UPDATE ang iyang buhaton.
		userRepo.save(userNam);
	}
	
	@Test
	public void testUpdateUserRoles() {
		// 1. Kuhaon nato si Ravi (ID 2)
	    User userRavi = userRepo.findById(2).get();
	    
	    // 2. Kuhaon nato ang bag-ong Role (pananglitan: Editor - ID 3)
	    // Siguruha nga naa kay RoleRepository o kaila ang imong test sa Roles
	    Role roleEditor = new Role(3); 
	    Role roleSalesperson = new Role(2);
	    
	    // 3. Tangtangon ang daan nga roles ug pulihan og bag-o
	    userRavi.getRoles().remove(roleEditor); // Pwede nimo i-remove ang usa
	    userRavi.addRole(roleSalesperson);      // O pun-an nimo og bag-o
	    
	    // 4. I-save ang kausaban
	    userRepo.save(userRavi);
	}
	
	@Test
	public void testDeleteUser() {
		// 1. I-specify ang ID sa user nga gusto nimo papason (pananglitan ID 2 si Ravi)
	    Integer userId = 2;
	    
	    // 2. Tawgon ang deleteById gikan sa repository
	    userRepo.deleteById(userId);
	    
	    // 3. I-verify kon wala na ba gyud ang user sa database
	    Optional<User> user = userRepo.findById(userId);
	    
	    // Ang assertThat gikan sa AssertJ (standard sa Spring Boot tests)
	    assertThat(user).isNotPresent();
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "ravi@gmail.com";
		User user = userRepo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
}

