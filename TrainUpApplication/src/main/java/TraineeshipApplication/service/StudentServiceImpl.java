package TraineeshipApplication.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDAORepository studentDAORepository;
    
    @Autowired
    private TraineeshipDAORepository traineeshipDAORepository;
    @Override
    public void saveProfile(Student student) {
        studentDAORepository.save(student);
    }
    
    @Override
    public Student retrieveProfileFromUserID(Long userId) {
        return studentDAORepository.findByUserId(userId).get();
    }
    
    @Override
	public List<TraineeshipPosition> retrieveCompaniesAvailablePositions() {
    	List<TraineeshipPosition> positions = traineeshipDAORepository.findAll();
	    return	positions
	                  .stream()
	                  .filter(p -> !p.isAssigned()) // Φιλτράρει τις μη ανατεθειμένες θέσεις
	                  .collect(Collectors.toList()); // Μετατρέπει το Stream σε List
	}

    @Override
    public void applyForPosition(Long userId, Long positionId) {
        // 1) Φέρνουμε Student
        Student student = studentDAORepository.findByUserId(userId).orElseThrow(
            () -> new RuntimeException("Student not found!")
        );
        // 2) Φέρνουμε τη θέση
        TraineeshipPosition pos = traineeshipDAORepository.findById(positionId)
            .orElseThrow(() -> new RuntimeException("Position not found!"));

        // 3) Αν δεν την έχει ήδη στη λίστα appliedPositions, την προσθέτουμε
        if (!student.getAppliedPositions().contains(pos)) {
            student.getAppliedPositions().add(pos);
            studentDAORepository.save(student);
        }
        // διαφορετικά, κάνουμε skip ή ρίχνουμε exception κλπ
    }
    @Override
    public void updateStudentLogbook(Long studentId, Long positionId, String logbookContent) {
        // Retrieve the student
        Student student = studentDAORepository.findByUserId(studentId).orElse(null);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        // Check if the student has this position assigned
        TraineeshipPosition position = student.getAssignedTraineeship();
        if (position == null || !position.getId().equals(positionId)) {
            throw new RuntimeException("Invalid traineeship position for this student");
        }

        // Update the logbook content
        position.setStudentLogbook(logbookContent);
        traineeshipDAORepository.save(position);
    }
    
    
   
    @Override
    public List<Student> findStudentsWhoApplied() {
        // e.g. those with appliedPositions not empty
        return studentDAORepository.findAll()
            .stream()
            .filter(stu -> !stu.getAppliedPositions().isEmpty() || stu.getTraineeshipCompleted() != null )
            .collect(Collectors.toList());
    }

    @Override
    public List<Student> findAssignedStudents() {
        // assignedTraineeship != null, but not completed
        return studentDAORepository.findAll()
            .stream()
            .filter(stu -> stu.getAssignedTraineeship() != null)
            .filter(stu -> stu.getTraineeshipCompleted() == null)
            .collect(Collectors.toList());
    }

    @Override
    public List<Student> findInProgressStudents() {
        // assignedTraineeship != null, and no traineeshipCompleted
        return studentDAORepository.findAll().stream()
            .filter(stu -> stu.getAssignedTraineeship() != null)
            .filter(stu -> stu.getTraineeshipCompleted() == null)
            .collect(Collectors.toList());
    }

    @Override
    public List<Student> findCompletedStudents() {
        // traineeshipCompleted != null
        return studentDAORepository.findAll()
            .stream()
            .filter(stu -> stu.getTraineeshipCompleted() != null)
            .collect(Collectors.toList());
    }
    @Override
    public List<Student> sortbyLocation(String Location){
    	return studentDAORepository.findAll()
                .stream()
                .filter(stu -> stu.getPreferredLocation().matches(Location))
                .collect(Collectors.toList());
    }
    @Override
	public void checkDetails(Student Student) {
    	
    }
}

