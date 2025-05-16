package TraineeshipApplication.service;

import java.util.List;

import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;

public interface ProfessorService {
	public void saveProfile(Professor Student);
	public Professor retrieveProfileFromUserID(Long userdId);
	List<TraineeshipPosition> findInProgressPositionsForProfessor(Long profId);
	List<TraineeshipPosition> findCompletedPositionsForProfessor(Long profId);
}
