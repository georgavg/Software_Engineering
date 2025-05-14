package TraineeshipApplication.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import TraineeshipApplication.model.*;


public interface UserService {
	public void saveUser(User user);
	
	public boolean isUserPresent(User user);
	
	public User findById(Long id);
	public User findByEmail(String email);
	public void deleteUser(Long userId);
	public Optional<User> getUserByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
	
	public void checkDetails(User user);
	
}
