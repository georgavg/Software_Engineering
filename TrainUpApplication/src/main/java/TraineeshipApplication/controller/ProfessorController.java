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

import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.model.User;
import TraineeshipApplication.service.EvaluationService;
import TraineeshipApplication.service.ProfessorService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.TraineeshipPositionService;
import TraineeshipApplication.service.UserService;

@Controller
@RequestMapping("/professor")
@SessionAttributes("professor")
public class ProfessorController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProfessorService professorService;
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private TraineeshipPositionService traineeshipPositionService;
    
    @RequestMapping("/homepage")
    public String getProfessorMainMenu(Model model) {
        // Λήψη authenticated χρήστη
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userService.getUserByUsername(currentUser).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.isEmailVerified()) {
            return "redirect:/verification/code?email=" + user.getEmail() + "&redirect=/professor/homepage";
        }
        
        // Ανάκτηση professor και αρχικοποίηση της συλλογής supervisedPositions για αποφυγή LazyInitializationException
        Professor professor = professorService.retrieveProfileFromUserID(user.getId());
        professor.getSupervisedPositions().size();
        model.addAttribute("professor", professor);
        
        if (professor.getProfessorName() == null) {
            return "professor/EditProfile";
        }
        return "professor/professor-homepage";
    }
    
    // Εμφάνιση φόρμας επεξεργασίας προφίλ
    @GetMapping("/profile")
    public String retrieveProfessorProfile() {
        return "professor/EditProfile";
    }
    
    @PostMapping("/saveProfessor")
    public String saveProfessorProfile(@ModelAttribute("professor") Professor professor,
                                       @RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                professor.setProfileImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        professorService.saveProfile(professor);
        return "redirect:/professor/homepage";
    }

    // Endpoint για επιστροφή της εικόνας
    @GetMapping("/profileImage/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfessorImage(@PathVariable Long userId) {
        Professor prof = professorService.retrieveProfileFromUserID(userId);
        if (prof == null || prof.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = prof.getProfileImage();

        // Παράδειγμα: προσπαθούμε να μαντέψουμε αν είναι jpeg ή png
        String contentType = "image/png"; 
        if (imageBytes.length > 2 
            && imageBytes[0] == (byte) 0xFF 
            && imageBytes[1] == (byte) 0xD8) {
            contentType = "image/jpeg";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
    @GetMapping("/traineeships")
    public String showInProgressTraineeships(Model model, @ModelAttribute("professor") Professor professor) {
        Long profId = professor.getId();
        List<TraineeshipPosition> inProgress = professorService.findInProgressPositionsForProfessor(profId);
        model.addAttribute("positions", inProgress);
        return "professor/View-Supervised-Traineeships";
    }
    
    @GetMapping("/completedTraineeships")
    public String showCompletedTraineeships(Model model, @ModelAttribute("professor") Professor professor) {
        Long profId = professor.getId();
        List<TraineeshipPosition> completed = professorService.findCompletedPositionsForProfessor(profId);
        model.addAttribute("positions", completed);
        return "professor/professor-completed-positions";
    }

    // Εμφάνιση φόρμας αξιολόγησης για συγκεκριμένη θέση από τον καθηγητή
    @GetMapping("/evaluate/{positionId}")
    public String showEvaluationForm(@PathVariable Long positionId, Model model) {
        Evaluation evaluation = evaluationService.getEvaluation(positionId, EvaluationType.PROFESSOR);
        if (evaluation == null) {
            evaluation = new Evaluation();
        }
        TraineeshipPosition position = traineeshipPositionService.getPositionById(positionId);
        model.addAttribute("position", position);
        model.addAttribute("evaluation", evaluation);
        return "professor/Evaluate";
    }
    
    // Αποθήκευση αξιολόγησης από καθηγητή
    @PostMapping("/saveEvaluation/{positionId}")
    public String saveEvaluation(@PathVariable Long positionId,
                                 @ModelAttribute("evaluation") Evaluation evaluation) {
        evaluationService.saveOrUpdateEvaluation(positionId, evaluation, EvaluationType.PROFESSOR);
        return "redirect:/professor/traineeships";
    }
}

