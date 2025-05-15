package TraineeshipApplication.service;

import java.util.List;
import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.TraineeshipPosition;

public interface CompanyService {
	
	public Company retrieveProfile(Long userId);
	public void saveProfile(Company company);
	public List<TraineeshipPosition> retrieveAvailablePositions(Long userId);
	public void addPosition(Long userId, TraineeshipPosition position);
    List<TraineeshipPosition> retrieveAssignedPositions(Long userId);
	void deletePositionApproved(Long positionId);
	public List<TraineeshipPosition> findCompletedPositionsForCompany(Long compId);

}