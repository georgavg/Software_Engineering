package TraineeshipApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.CompanyService;
import TraineeshipApplication.service.ProfessorService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.TraineeshipPositionService;

@Controller
@RequestMapping("/informations")
public class InformationsController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProfessorService professorService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TraineeshipPositionService positionService;

    @GetMapping("/companyDetails")
    public String showCompanyDetails(@RequestParam("cid") Long cid, Model model) {
        Company company = companyService.retrieveProfile(cid);
        if (company == null) {
            return "redirect:/error"; // ή ό,τι άλλο θέλεις
        }
        model.addAttribute("company", company);
        return "informations/company-details"; // Το νέο HTML template
    }
    @GetMapping("/professorDetails")
    public String showProfessorDetails(@RequestParam("pid") Long pid, Model model) {
        // Έστω ότι έχουμε professorService.findById(id)
        Professor professor = professorService.retrieveProfileFromUserID(pid);
        if (professor == null) {
            return "redirect:/error";
        }
        model.addAttribute("professor", professor);
        return "informations/professor-details";
    }
    
    // Student Details
    @GetMapping("/studentDetails")
    public String showStudentDetails(@RequestParam("sid") Long sid, Model model) {
        Student student = studentService.retrieveProfileFromUserID(sid);
        if (student == null) {
            return "redirect:/error";
        }
        model.addAttribute("student", student);
        return "informations/student-details";
    }

    // Position Details
    @GetMapping("/positionDetails")
    public String showPositionDetails(@RequestParam("pid") Long pid, Model model) {
        TraineeshipPosition pos = positionService.getPositionById(pid);
        if (pos == null) {
            return "redirect:/error";
        }
        model.addAttribute("position", pos);
        return "informations/position-details";
    }
}
