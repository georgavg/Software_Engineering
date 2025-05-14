package TraineeshipApplication.service;

import java.util.List;

import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

public interface StudentService {
	public void saveProfile(Student Student);
	public Student retrieveProfileFromUserID(Long userdId);
	List<TraineeshipPosition> retrieveCompaniesAvailablePositions();
	public void applyForPosition(Long userId, Long positionId);
	void updateStudentLogbook(Long studentId, Long positionId, String logbookContent);
	List<Student> findAssignedStudents();
	List<Student> findInProgressStudents();
	List<Student> findCompletedStudents();
	List<Student> findStudentsWhoApplied();
	///----
	List<Student> sortbyLocation(String Location);
	public void checkDetails(Student Student);
}
