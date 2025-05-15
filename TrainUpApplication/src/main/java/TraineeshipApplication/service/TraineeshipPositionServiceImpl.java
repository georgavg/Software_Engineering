package TraineeshipApplication.service;

import TraineeshipApplication.dao.ProfessorDAORepository;
import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import strategy.search.PositionsSearchFactory;
import strategy.search.PositionsSearchStrategy;
import strategy.supervisor.SupervisorAssignmentFactory;
import strategy.supervisor.SupervisorAssignmentStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraineeshipPositionServiceImpl implements TraineeshipPositionService {

    @Autowired
    private TraineeshipDAORepository traineeshipDAORepository;
    
    @Autowired
    private StudentDAORepository studentDAORepository;
    
    @Autowired
    private ProfessorDAORepository professorRepo;
    
    @Override
    public TraineeshipPosition getPositionById(Long id) {
        return traineeshipDAORepository.findById(id).orElse(null);
    }
    
    
    @Override
    public List<TraineeshipPosition> findMatchesForStudent(Long studentId, String strategyName) {
        // 1) load student
        Student stu = studentDAORepository.findByUserId(studentId).orElse(null);
        if (stu == null) {
            return Collections.emptyList();
        }

        // 2) get unassigned
        List<TraineeshipPosition> unassigned = traineeshipDAORepository.findAll()
            .stream()
            .filter(pos -> !pos.isAssigned())
            .collect(Collectors.toList());

        // 3) create strategy instance
        PositionsSearchStrategy strategy = PositionsSearchFactory.createStrategy(strategyName);

        // 4) do the search
        return strategy.searchPositions(stu, unassigned);
    }

    // =========== The assign position method (story #2) =============
    @Override
    public void assignPositionToStudent(Long studentId, Long positionId) {
        Student student = studentDAORepository.findById(studentId).orElse(null);
        TraineeshipPosition pos = traineeshipDAORepository.findById(positionId).orElse(null);
        if (student == null || pos == null) {
            return;
        }
        // mark assigned
        pos.setAssigned(true);
        // optionally link them if you want single assignment:
        student.setAssignedTraineeship(pos);
        student.setLookingForTraineeship(false);

        // save
        traineeshipDAORepository.save(pos);
        studentDAORepository.save(student);
    }

    // =========== The assign supervisor method (story #3) =============
    @Override
    public void assignSupervisor(Long positionId, String strategyName) {
        TraineeshipPosition pos = traineeshipDAORepository.findById(positionId).orElse(null);
        if (pos == null) return;

        // Create a strategy object
        SupervisorAssignmentStrategy strategy = SupervisorAssignmentFactory.createStrategy(strategyName);

        // get all professors from DB
        List<Professor> allProfs = professorRepo.findAll();

        // let the strategy pick one
        Professor chosen = strategy.assign(allProfs, pos);

        if (chosen != null) {
            pos.setSupervisor(chosen);
            traineeshipDAORepository.save(pos);
        }
    }

    // =========== The in-progress list (story #4) =============
    @Override
    public List<TraineeshipPosition> getInProgressPositions() {
        // e.g. assigned but passFailGrade=false means "in-progress"
        return traineeshipDAORepository.findAll().stream()
                .filter(pos -> pos.isAssigned() && !pos.isPassFailGrade())
                .collect(Collectors.toList());
    }
    
    @Override
    public void completeTraineeship(Long posId, boolean passOrFail) {
        TraineeshipPosition pos = traineeshipDAORepository.findById(posId).orElse(null);
        if (pos == null) return;

        // gather evaluations
        List<Evaluation> evals = pos.getEvaluations();
        boolean hasProfEval = evals.stream().anyMatch(e -> e.getEvaluationType() == EvaluationType.PROFESSOR);
        boolean hasCompanyEval = evals.stream().anyMatch(e -> e.getEvaluationType() == EvaluationType.COMPANY);

        if (!hasProfEval || !hasCompanyEval) {
            // Missing one => can't finalize
            throw new IllegalStateException("Cannot finalize pass/fail: missing evaluations from both supervisor & company.");
        }

        // If both present => finalize
        pos.setPassFailGrade(passOrFail);
        Student st = pos.getStudent();
        if (st != null) {
            st.setTraineeshipCompleted(passOrFail ? "Pass" : "Fail");
            studentDAORepository.save(st);
        }
        traineeshipDAORepository.save(pos);
    }
   
}

