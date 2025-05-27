package TraineeshipApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;

import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Role;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.User;
import TraineeshipApplication.service.CompanyService;
import TraineeshipApplication.service.EmailService;
import TraineeshipApplication.service.ProfessorService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ProfessorService professorService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EmailService emailService;

    @RequestMapping("/login")
    public String login() {
        return "auth/Login_Page";
    }

    @RequestMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "auth/Register";
    }

    @RequestMapping("/save")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Validate password
        if (!isValidPassword(user.getPassword())) {
            model.addAttribute("errorMessage",
                "Password must have min 6 chars, 1 uppercase, 1 digit, 1 special char.");
            return "auth/Register";
        }

        // Check uniqueness
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Username already in use!");
            return "auth/Register";
        }
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already in use!");
            return "auth/Register";
        }

        // Generate 4-digit code
        String verificationCode = emailService.generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setEmailVerified(false);

        // Save user (will encode password)
        userService.saveUser(user);

        // Send code via email
        try {
            emailService.sendVerificationCode(user.getEmail(), user.getVerificationCode());
        } catch (Exception ex) {
            model.addAttribute("errorMessage",
                "Failed to send verification email: " + ex.getMessage());
            return "auth/Register";
        }

        // If Student, create Student
        if (user.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setUser(user);
            studentService.saveProfile(student);
        }else if (user.getRole() == Role.PROFESSOR) {
            Professor professor = new Professor();
            professor.setUser(user);
            professorService.saveProfile(professor);
        }else if (user.getRole() == Role.COMPANY) {
            Company company = new Company();
            company.setUser(user);
            companyService.saveProfile(company);
        }

        model.addAttribute("successMessage", "User created! You can log in now.");
        return "auth/Login_Page";
    }
    
    @RequestMapping("/deleteAccount")
    public String deleteMyAccount(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println(currentPrincipalName);

        Long userId = userService.getUserByUsername(currentPrincipalName)
                                 .map(User::getId)
                                 .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(userId);
        return "redirect:/logout";
    }
    
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[^a-zA-Z0-9 ].*")) return false;
        return true;
    }
}
