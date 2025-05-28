package TraineeshipApplication.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.model.User;
import TraineeshipApplication.service.CompanyService;
import TraineeshipApplication.service.EvaluationService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.TraineeshipPositionService;
import TraineeshipApplication.service.UserService;

@Controller
@RequestMapping("/company")
@SessionAttributes("company")
public class CompanyController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TraineeshipPositionService traineeshipPositionService;
    
    @Autowired
    private EvaluationService evaluationService;
    
    // 1) Company Homepage (already existing)
    @GetMapping("/homepage")
    public String getCompanyMainMenu(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        User user = userService.getUserByUsername(currentUser).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.isEmailVerified()) {
            return "redirect:/verification/code?email=" + user.getEmail() + "&redirect=/company/homepage";
        }

        Company company = companyService.retrieveProfile(user.getId());
        model.addAttribute("company", company);
        if (company.getCompanyName() == null) {
            return "company/EditProfile";
        }

        // We want two new buttons on the homepage: "Create Position" & "Available Positions".
        // We'll handle them in the HTML template.

        return "company/company-homepage";
    }
    @GetMapping(value = "/profileImage/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        // Παίρνουμε το αντικείμενο Company
        Company company = companyService.retrieveProfile(userId);
        if (company == null || company.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = company.getProfileImage();

        // Για απλότητα, υποθέτουμε ότι είναι JPG ή PNG. 
        // Αν θέλεις, αποθήκευσε και το content-type σε ξεχωριστό πεδίο.
        HttpHeaders headers = new HttpHeaders();
        // Θα μπορούσες να κάνεις λογική ελέγχου για PNG/JPEG
        // π.χ. headers.setContentType(MediaType.IMAGE_JPEG)
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
    // existing profile methods
    @GetMapping("/profile")
    public String retrieveCompanyProfile() {
        return "company/EditProfile";
    }
    
    @PostMapping("/saveCompany")
    public String saveCompanyProfile(@ModelAttribute("company") Company company,
                                     @RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()){
            try {
                company.setProfileImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        companyService.saveProfile(company);
        return "redirect:/company/homepage";
    }
    // 2) Show the list of positions for the company
    @GetMapping("/positions")
    public String showPositionsList(Model model) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userService.getUserByUsername(currentUser).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Load the company's positions
        Company company = companyService.retrieveProfile(user.getId());
        /////List<TraineeshipPosition> positions = company.getPositions();
        List<TraineeshipPosition> positions = companyService.retrieveAvailablePositions(user.getId());

        model.addAttribute("company", company);
        model.addAttribute("positions", positions);
        return "company/available-positions"; 
    }

    // 3) Show the form to create a new position
    @GetMapping("/createPosition")
    public String showCreatePositionForm(Model model) {
        model.addAttribute("position", new TraineeshipPosition()); 
        return "company/create-position"; 
    }

    // 4) Save the newly created position
    @PostMapping("/savePosition")
    public String savePosition(@ModelAttribute("position") TraineeshipPosition position) {
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userService.getUserByUsername(currentUser).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // use the service to add the position to the company's list
        companyService.addPosition(user.getId(), position);

        // redirect back to the list of positions
        return "redirect:/company/positions";
    }
    
    @GetMapping("/deletePosition/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Long positionId, Model model) {
        model.addAttribute("positionId", positionId);
        return "company/deleteconfirmation";
    }
    
    @GetMapping("/deletePositionApproved/{id}")
    public String deletePositionApproved(@PathVariable("id") Long positionId) {
        // Usually we also check the current user to ensure they own this position
        // For simplicity:
        companyService.deletePositionApproved(positionId);
        return "redirect:/company/positions";
    }
    
    @PostMapping("/deletePositionDenied")
    public String deletePositionDenied() {
        return "company/denied"; // θα πάει στο denied.html
    }
   
    @GetMapping("/assignedPositions")
    public String showAssignedPositions(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userService.getUserByUsername(currentUser).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }

        List<TraineeshipPosition> assignedPositions = companyService.retrieveAssignedPositions(user.getId());

        // Debugging: Print each position and check if the student is null
        for (TraineeshipPosition pos : assignedPositions) {
            System.out.println("Position ID: " + pos.getId() + " | Assigned to: " + 
                               (pos.getStudent() != null ? pos.getStudent().getStudentName() : "No Student"));
        }

        model.addAttribute("assignedPositions", assignedPositions);
        return "company/AssignedPositions";
    }
    
    /*
    @GetMapping("/viewStudent/{studentId}")
    public String viewStudentProfile(@PathVariable("studentId") Long studentId, Model model) {
        Student student = studentService.retrieveProfileFromUserID(studentId);
        if (student == null) {
            return "redirect:/company/assignedPositions";
        }
        model.addAttribute("student", student);
        return "informations/student-details";
    }*/

    // Εμφάνιση φόρμας αξιολόγησης για μια συγκεκριμένη θέση από την εταιρεία
    @GetMapping("/evaluate/{posId}")
    public String showEvaluationForm(@PathVariable Long posId, Model model) {
        TraineeshipPosition position = traineeshipPositionService.getPositionById(posId);
        if (position == null) {
            return "redirect:/company/assignedPositions";
        }
        // Λαμβάνουμε την αξιολόγηση από εταιρεία (FROM_COMPANY)
        Evaluation evaluation = evaluationService.getEvaluation(posId, EvaluationType.COMPANY);
        if (evaluation == null) {
            evaluation = new Evaluation();
        }
        evaluation.setTraineeshipPosition(position);
        model.addAttribute("position", position);
        model.addAttribute("evaluation", evaluation);
        return "company/Evaluate";
    }

    // Αποθήκευση αξιολόγησης από την εταιρεία
    @PostMapping("/saveEvaluation/{posId}")
    public String saveEvaluation(@PathVariable Long posId,
                                 @ModelAttribute("evaluation") Evaluation evaluation) {
        evaluationService.saveOrUpdateEvaluation(posId, evaluation, EvaluationType.COMPANY);
        return "redirect:/company/assignedPositions";
    }
    
    @GetMapping("/sendEmail")
    public String sendEmail(Model model, @ModelAttribute("student") Student student) {
		return null;
    	
    }
    
    @GetMapping("/completedTraineeships")
    public String showCompletedTraineeships(Model model, @ModelAttribute("company") Company company) {
        Long compId = company.getId();
        List<TraineeshipPosition> completed = companyService.findCompletedPositionsForCompany(compId);
        model.addAttribute("positions", completed);
        return "company/company-completed-positions";
    }
}
