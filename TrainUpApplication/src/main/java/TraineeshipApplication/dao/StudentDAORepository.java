package TraineeshipApplication.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TraineeshipApplication.model.Student;

@Repository
public interface StudentDAORepository extends JpaRepository<Student, Long>{
	Optional<Student> findByUserId(Long userId);
	Optional<Student> findAvailableByPreferredLocation(String Location);
}
