package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo; // Siguruha nga gi-inject nimo ang RoleRepository

	@Autowired
	private PasswordEncoder passwordEncoder; // Kani ang gikan sa Config sa taas
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	public List<User> listAll() {
		// I-cast nato ang Iterable ngadto sa List para dali i-display sa table
		return (List<User>) userRepo.findAll();
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	// Gikan sa 'public void save' usba ngadto sa 'public User save'
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {
			// 1. Kuhaon ang karaan nga data gikan sa DB
			User existingUser = userRepo.findById(user.getId()).get();

			// 2. I-check kon nag-type ba siya og bag-ong password sa form
			if (user.getPassword().isEmpty()) {

				// Kon BLANK ang form, gamita ang karaan nga password gikan sa DB
				user.setPassword(existingUser.getPassword());

			} else {
				// Kon NAAAY SULOD, i-encode ang bag-ong password
				encodePassword(user);
			}

		} else {
			// Kon BAG-ONG USER (New), i-encode gyud ang password
			encodePassword(user);
		}
		
		// I-return ang result sa repo.save()
		return userRepo.save(user);
	}
	
	public User updateAccount(User userInform) {
		/* Kuhaon ang original nga data gikan sa DB gamit ang ID */
		User userInDB = userRepo.findById(userInform.getId()).get();
		
		/* I-check kung naay bag-ong password nga gi-input */
	    if (!userInform.getPassword().isEmpty()) {
	        // I-encode ang bag-ong password (siguroha nga naay passwordEncoder diri)
	        userInDB.setPassword(userInform.getPassword());
	        encodePassword(userInDB); 
	    }
	    
		/* I-check kung naay bag-ong photo */
	    if (userInform.getPhotos() != null) {
	        userInDB.setPhotos(userInform.getPhotos());
	    }
	    
		/* I-update ang First Name ug Last Name */
	    userInDB.setFirstName(userInform.getFirstName());
	    userInDB.setLastName(userInform.getLastName());
	    
		/* I-save ang gi-update nga userInDB object */
	    return userRepo.save(userInDB);
	}

	// process sa pag encode sa password.
	private void encodePassword(User user) {
		// I-encode ang password sa dili pa i-save sa DB
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	// Kinahanglan i-check kon ang nakit-an nga email iyaha ba sa kaugalingong user
	// (kon nag-edit) o sa lain nga user:
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);

		// Way nakit-an, so unique!
		if (userByEmail == null) {
			return true;
		}

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (userByEmail != null) return false; // Naa nay tag-iya sa email.
		} else {
			if (userByEmail.getId() != id) {
				return false; // Ang tag-iya sa email dili ang user nga gi-edit
			}
		}

		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id); // Kinahanglan ka maghimo ani sa UserRepository
		
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		
		userRepo.deleteById(id);
	}
	
	@Transactional
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
