package TraineeshipApplication.dao;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import TraineeshipApplication.model.Professor;


public interface ProfessorDAORepository extends JpaRepository<Professor, Long> {
	Optional<Professor> findByUserId(Long userId);
}
