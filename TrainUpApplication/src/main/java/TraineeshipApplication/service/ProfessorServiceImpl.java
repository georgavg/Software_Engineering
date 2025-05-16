package TraineeshipApplication.service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TraineeshipApplication.dao.ProfessorDAORepository;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

@Service
public class ProfessorServiceImpl implements ProfessorService{
	
	@Autowired
	ProfessorDAORepository professorDAORepository;
	
	@Override
	public void saveProfile(Professor professor) {
		professorDAORepository.save(professor);
	}
	
	@Override
	public Professor retrieveProfileFromUserID(Long userId) {
		return professorDAORepository.findByUserId(userId).get();
	}
	
	@Override
    public List<TraineeshipPosition> findInProgressPositionsForProfessor(Long profId) {
        Professor prof = professorDAORepository.findById(profId).orElse(null);
        if (prof == null) return Collections.emptyList();

        return prof.getSupervisedPositions()
                   .stream()
                   // a position is "completed" if passFailGrade == true 
                   // or the student's traineeshipCompleted != null
                   .filter(pos -> {
                       if (!pos.isAssigned()) return true; // not assigned => not completed
                       Student s = pos.getStudent();
                       boolean posCompleted = pos.isPassFailGrade();
                       boolean studentCompleted = (s != null && s.getTraineeshipCompleted() != null);
                       return !(posCompleted || studentCompleted);
                   })
                   .collect(Collectors.toList());
    }

    // Return only the professor's positions that are COMPLETED
    @Override
    public List<TraineeshipPosition> findCompletedPositionsForProfessor(Long profId) {
        Professor prof = professorDAORepository.findById(profId).orElse(null);
        if (prof == null) return Collections.emptyList();

        return prof.getSupervisedPositions()
                   .stream()
                   .filter(pos -> {
                       if (!pos.isAssigned()) return false; // unassigned => not completed
                       Student s = pos.getStudent();
                       boolean posCompleted = pos.isPassFailGrade();
                       boolean studentCompleted = (s != null && s.getTraineeshipCompleted() != null);
                       return posCompleted || studentCompleted;
                   })
                   .collect(Collectors.toList());
    }
}
