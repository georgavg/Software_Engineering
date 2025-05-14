package TraineeshipApplication.service;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import TraineeshipApplication.dao.*;
import TraineeshipApplication.model.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private StudentDAORepository studentRepository;
    
    @Autowired
    private UserDAORepository userDAORepository;
    
    @Autowired
    private CompanyDAORepository companyRepository;
    
    @Autowired
    private ProfessorDAORepository professorRepository;
    
    @Override
    public boolean existsByUsername(String username) {
        return userDAORepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDAORepository.existsByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        // Only encode if it's not already hashed
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userDAORepository.save(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userDAORepository.findByUsername(username);
    }

    @Override
    public boolean isUserPresent(User user) {
        return userDAORepository.findByUsername(user.getUsername()).isPresent();
    }

    @Override
    public User findById(Long id) {
        return userDAORepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userDAORepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // Fetch user
        Optional<User> userOpt = userDAORepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        User user = userOpt.get();

        // Determine role and delete associated entity
        String role = user.getRole().toString();  // Assuming role is stored as a String
        
        switch (role) {
            case "STUDENT":
                studentRepository.findByUserId(userId).ifPresent(studentRepository::delete);
                break;
            case "PROFESSOR":
                professorRepository.findByUserId(userId).ifPresent(professorRepository::delete);
                break;
            case "COMPANY":
                companyRepository.findByUserId(userId).ifPresent(companyRepository::delete);
                break;
            default:
                throw new RuntimeException("Unknown role: " + role);
        }

        // Now delete the user
        userDAORepository.deleteById(userId);
    }

    // Load for Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDAORepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND " + username));
    }
    @Override
    public void checkDetails(User user) {}
    
}

