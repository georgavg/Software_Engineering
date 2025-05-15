package TraineeshipApplication.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TraineeshipApplication.dao.CompanyDAORepository;
import TraineeshipApplication.dao.EvaluationDAORepository;
import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

@Service
public class CompanyServiceImpl implements CompanyService{
	
	@Autowired
	CompanyDAORepository companyDAORepository;
	
	@Autowired
	StudentDAORepository studentDAORepository;
	
	@Autowired
	TraineeshipDAORepository traineeshipDAORepository;
    
	@Override
	public Company retrieveProfile(Long userId) {
		return companyDAORepository.findByUserId(userId).get();
	}

	@Override
	public void saveProfile(Company company) {
		companyDAORepository.save(company);
	}

	@Override
	public List<TraineeshipPosition> retrieveAvailablePositions(Long userId) {
	    Company company = retrieveProfile(userId);
	    return company.getPositions()
	                  .stream()
	                  .filter(p -> !p.isAssigned()) // Φιλτράρει τις μη ανατεθειμένες θέσεις
	                  .collect(Collectors.toList()); // Μετατρέπει το Stream σε List
	}

    @Override
    public void addPosition(Long userId, TraineeshipPosition position) {
        Company company = retrieveProfile(userId);
        position.setCompany(company);
        position.setAssigned(false); // default if you like
        company.getPositions().add(position);
        // by saving the company, we also save the new position because of cascade
        companyDAORepository.save(company);
    }
    
   

    @Override
    public void deletePositionApproved(Long positionId) {
        // 1) Find the position
        TraineeshipPosition pos = traineeshipDAORepository.findById(positionId).orElse(null);
        if (pos == null) return;

        // 2) Check if it’s assigned
        if (pos.isAssigned()) {
            throw new IllegalStateException("Cannot delete: Position is assigned to a student.");
        }

        // 3) Remove references from all students who have applied
        List<Student> allStudents = studentDAORepository.findAll();
        for (Student s : allStudents) {
            if (s.getAppliedPositions().contains(pos)) {
                
                s.getAppliedPositions().remove(pos);
                studentDAORepository.save(s);
            }
        }
       
        // 4) Now safe to delete
        traineeshipDAORepository.delete(pos);
    }


 // 1) Retrieve assigned positions
    @Override
    public List<TraineeshipPosition> retrieveAssignedPositions(Long userId) {
        Company company = retrieveProfile(userId);
        // from that company’s positions, filter only the assigned
        return company.getPositions()
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

	@Override
	public List<TraineeshipPosition> findCompletedPositionsForCompany(Long compId) {
		Company comp = companyDAORepository.findById(compId).orElse(null);
        if (comp == null) return Collections.emptyList();

        return comp.getPositions()
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