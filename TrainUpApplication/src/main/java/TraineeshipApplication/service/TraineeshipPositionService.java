package TraineeshipApplication.service;

import TraineeshipApplication.model.TraineeshipPosition ;
import java.util.List;

public interface TraineeshipPositionService {
    TraineeshipPosition getPositionById(Long id);         
	void assignPositionToStudent(Long studentId, Long positionId);
	List<TraineeshipPosition> findMatchesForStudent(Long studentId, String strategy);
	void assignSupervisor(Long posId, String strategy);
	List<TraineeshipPosition> getInProgressPositions();
	void completeTraineeship(Long posId, boolean passOrFail);
}
