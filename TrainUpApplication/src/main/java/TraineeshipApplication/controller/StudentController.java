package TraineeshipApplication.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.model.User;
import TraineeshipApplication.service.CompanyService;
import TraineeshipApplication.service.ProfessorService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.UserService;

@Controller
@RequestMapping("/student")
@SessionAttributes("student")
public class StudentController {

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ProfessorService professorService;
    @RequestMapping("/homepage")
    public String getStudentMainMenu(Model model) {
        // get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName(); 
        
        User user = userService.getUserByUsername(currentUser).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // If not verified, send to code verification
        if (!user.isEmailVerified()) {
            return "redirect:/verification/code?email=" + user.getEmail() + "&redirect=/student/homepage";
        }

        // Load the student's domain object
        Student student = studentService.retrieveProfileFromUserID(user.getId());
        model.addAttribute("student", student);

        // If no name set, show form
        if (student.getStudentName() == null) {
            return "student/EditProfile";
        }
        return "student/student-homepage";
    }
    
    //edit profile informations
    @GetMapping("/profile")
    public String retrieveStudentProfile() {
        return "student/EditProfile";
    }
    
    @PostMapping("/saveStudent")
    public String saveStudentProfile(@ModelAttribute("student") Student student,
                                     @RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                student.setProfileImage(file.getBytes());
            }
            studentService.saveProfile(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/student/homepage";
    }

    @GetMapping("/profileImage/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        Student student = studentService.retrieveProfileFromUserID(userId);
        if (student == null || student.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = student.getProfileImage();
        String contentType = "image/png";
        if (imageBytes.length > 2 
            && imageBytes[0] == (byte)0xFF 
            && imageBytes[1] == (byte)0xD8) {
            contentType = "image/jpeg";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }

    @GetMapping("/availableTraineeships")
    public String availableCompanyTraineeships(Model model) {

        // 1) Παίρνουμε τις μη-ανατεθειμένες θέσεις (open positions)
        List<TraineeshipPosition> positions = studentService.retrieveCompaniesAvailablePositions();

        // 2) Βρίσκουμε τον τρέχοντα authenticated user -> το Student
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login"; 
        }
        Student student = studentService.retrieveProfileFromUserID(user.getId());

        // 3) Στέλνουμε στο μοντέλο:
        model.addAttribute("positions", positions);
        model.addAttribute("student", student);

        return "student/View-Available-Traineeships"; 
    }
    
    
     //Apply σε μια θέση
    @GetMapping("/applyPosition/{posId}")
    public String applyPosition(@PathVariable("posId") Long posId) {

        // βρίσκουμε τρέχοντα student
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // καλούμε service
        studentService.applyForPosition(user.getId(), posId);

        return "redirect:/student/availableTraineeships";
    }
    
    @GetMapping("/logbook")
    public String viewLogbook(Model model) {
        // 1) Get the current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        // 2) Find the Student domain
        Student student = studentService.retrieveProfileFromUserID(user.getId());
        if (student == null) {
            return "redirect:/login";
        }
        
        // 3) Check if the student has an assignedTraineeship
        TraineeshipPosition position = student.getAssignedTraineeship();
        if (position == null) {
            // No assigned traineeship => cannot fill logbook
            return "redirect:/student/homepage"; 
        }

        // 4) Put the position in the model, so we can display/edit the logbook
        model.addAttribute("position", position);
        // Also add the student, if needed
        model.addAttribute("student", student);

        return "student/manage-my-traineeship-logbook"; 
    }

    
    @PostMapping("/saveLogbook")
    public String saveLogbook(@RequestParam("positionId") Long posId,
                              @RequestParam("logbookContent") String logbookContent) {
        // 1) Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // 2) Call service to update the logbook
        studentService.updateStudentLogbook(user.getId(), posId, logbookContent);

        return "redirect:/student/logbook";
    }
    @GetMapping("/traineeshipResults")
    public String viewTraineeshipResults(Model model) {
        // 1) get current user => student
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Student student = studentService.retrieveProfileFromUserID(user.getId());
        if (student == null) {
            return "redirect:/login";
        }

        TraineeshipPosition position = student.getAssignedTraineeship();
        // if not completed or no assigned, maybe redirect or show error
        if (position == null || student.getTraineeshipCompleted() == null) {
            // e.g. no results yet, or user tries to cheat
            return "redirect:/student/homepage";
        }

        // load any evaluations if not already
        // or assume lazy fetch is fine
        model.addAttribute("position", position);
        model.addAttribute("student", student);

        return "student/student-traineeship-results";
    }
}

