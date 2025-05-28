package TraineeshipApplication.controller;

import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.EvaluationService;
import TraineeshipApplication.service.ProfessorService;
import TraineeshipApplication.service.StudentService;
import TraineeshipApplication.service.TraineeshipPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/committee")
@PreAuthorize("hasAuthority('TRAINEESHIP_COMMITTEE')")
@SessionAttributes({"currentFilter", "currentSort"})
public class CommitteeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TraineeshipPositionService positionService;

    @Autowired
    private EvaluationService evaluationService;


    // Default filter/sort
    @ModelAttribute("currentFilter")
    public String initFilter() {
        return "all"; // can be "all", "applied", "assigned", "inprogress", "completed" ...
    }
    @ModelAttribute("currentSort")
    public String initSort() {
        return "none"; // can be "none", "name", "am", etc.
    }

    // 1) Committee Homepage: shows a list of students, filtered & sorted
    @GetMapping("/homepage")
    public String showCommitteeHomepage(
            @RequestParam(name="filter", required=false) String filter,
            @RequestParam(name="sort", required=false) String sort,
            @ModelAttribute("currentFilter") String currentFilter,
            @ModelAttribute("currentSort") String currentSort,
            Model model) 
    {
        // If user passed ?filter=..., update currentFilter
        if (filter != null) {
            currentFilter = filter;
            model.addAttribute("currentFilter", currentFilter);
        }
        // If user passed ?sort=..., update currentSort
        if (sort != null) {
            currentSort = sort;
            model.addAttribute("currentSort", currentSort);
        }

        // 1) Filter the students
        List<Student> filteredStudents = filterStudents(currentFilter);

        // 2) Sort the filtered students
        sortStudents(filteredStudents, currentSort);

        model.addAttribute("students", filteredStudents);
        model.addAttribute("defaultFilter", currentFilter);
        model.addAttribute("defaultSort", currentSort);

        return "committee/committee-homepage";
    }

    // Helper method: filter students based on filter key
    private List<Student> filterStudents(String filter) {
        switch (filter) {
            case "applied":
                return studentService.findStudentsWhoApplied(); // e.g. appliedPositions not empty
            case "assigned":
                return studentService.findAssignedStudents();   // assignedTraineeship != null, in progress
            case "inprogress":
                return studentService.findInProgressStudents(); // assigned but not completed
            case "completed":
                return studentService.findCompletedStudents();  // traineeshipCompleted != null
            default:
                return studentService.findStudentsWhoApplied();
                // or just findAll() if you want truly "all" students
        }
    }

    private void sortStudents(List<Student> students, String sort) {
        switch (sort) {
            case "posEndDate":
                // If a student's assignedTraineeship is null, treat them as "infinite" date
                students.sort((s1, s2) -> {
                    TraineeshipPosition p1 = s1.getAssignedTraineeship();
                    TraineeshipPosition p2 = s2.getAssignedTraineeship();
                    if (p1 == null && p2 == null) return 0;
                    if (p1 == null) return 1; // put no assignment last
                    if (p2 == null) return -1;
                    return p1.getToDate().compareTo(p2.getToDate());
                });
                break;
            default:
                // "none" => no sort
                break;
        }
    }


    // 2) Show a page to pick a strategy for searching positions for a specific student
    @GetMapping("/searchPositions")
    public String showSearchPositionsForm(@RequestParam("studentId") Long sid, Model model) {
        Student student = studentService.retrieveProfileFromUserID(sid);
        if (student == null) {
            return "redirect:/committee/homepage";
        }
        model.addAttribute("student", student);
        return "committee/SearchPositions";
    }

    @PostMapping("/doSearchPositions")
    public String doSearchPositions(
            @RequestParam("studentId") Long sid,
            @RequestParam("strategy") String strategyKey,
            Model model) 
    {
        // find student
        Student student = studentService.retrieveProfileFromUserID(sid);
        if (student == null) {
            return "redirect:/committee/homepage";
        }

        // find matches (the service uses the static factory)
        List<TraineeshipPosition> matches = positionService.findMatchesForStudent(sid, strategyKey);

        model.addAttribute("matchingPositions", matches);
        model.addAttribute("student", student);
        return "committee/FindMatches";
    }

    // 4) Assign a position to the student
    @PostMapping("/assignPosition")
    public String assignPosition(
            @RequestParam("studentId") Long sid,
            @RequestParam("positionId") Long posId) 
    {
        positionService.assignPositionToStudent(sid, posId);
        return "redirect:/committee/homepage";
    }


    // 6) View progress of a single traineeship (read-only evaluations, pass/fail button)
    @GetMapping("/viewProgress")
    public String viewProgress(@RequestParam("posId") Long posId, Model model) {
        TraineeshipPosition position = positionService.getPositionById(posId);
        if (position == null) {
            return "redirect:/committee/homepage";
        }
        // assigned student
        Student assignedStudent = position.getStudent();
        // get evaluations
        List<Evaluation> evals = evaluationService.findByTraineeshipPosition(posId);

        model.addAttribute("position", position);
        model.addAttribute("assignedStudent", assignedStudent);
        model.addAttribute("evaluations", evals);

        return "committee/ViewProgress";
    }

    // Assign a supervisor
    @PostMapping("/assignSupervisor")
    public String assignSupervisor(
            @RequestParam("positionId") Long posId,
            @RequestParam("strategy") String strategyKey) 
    {
        positionService.assignSupervisor(posId, strategyKey);
        return "redirect:/committee/viewProgress?posId=" + posId;
    }

    // Finalize pass/fail
    @PostMapping("/completeTraineeship")
    public String completeTraineeship(
            @RequestParam("positionId") Long posId,
            @RequestParam("passFail") boolean passOrFail,
            RedirectAttributes redirectAttributes) 
    {
        try {
            positionService.completeTraineeship(posId, passOrFail);
            // success => no error
        } catch (IllegalStateException e) {
            // if missing evaluations => show error
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // redirect back to the progress page (or homepage) so we can display an error
            return "redirect:/committee/viewProgress?posId=" + posId;
        }
        return "redirect:/committee/homepage";
    }
    
    /// ----- ////
    @GetMapping("/sendEmail")
    public String sendCompletitionEmail(Model model, @ModelAttribute("student") Student student) {
		return null;
    	
    }
}