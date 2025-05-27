package TraineeshipApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import TraineeshipApplication.model.User;
import TraineeshipApplication.service.UserService;

@Controller
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/code")
    public String showCodePage(@RequestParam("email") String email,
                               @RequestParam("redirect") String redirect,
                               Model model) {
        model.addAttribute("email", email);
        model.addAttribute("redirect", redirect);
        return "auth/verify-code"; 
    }

    @PostMapping("/code")
    public String processCode(@RequestParam("email") String email,
                              @RequestParam("code") String code,
                              @RequestParam("redirect") String redirect,
                              Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("errorMessage", "No user found for email: " + email);
            model.addAttribute("email", email);  // ✅ Επαναφορά του email στο μοντέλο
            model.addAttribute("redirect", redirect);
            return "auth/verify-code";
        }

        if (!code.equals(user.getVerificationCode())) {
            model.addAttribute("errorMessage", "Incorrect code.");
            model.addAttribute("email", email);  // ✅ Επαναφορά του email στο μοντέλο
            model.addAttribute("redirect", redirect);
            return "auth/verify-code";
        }

        // Mark verified
        user.setEmailVerified(true);
        userService.saveUser(user);

        return "redirect:" + redirect;
    }
}
