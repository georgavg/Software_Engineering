package TraineeshipApplication.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import TraineeshipApplication.model.Evaluation;


public interface EvaluationDAORepository extends JpaRepository<Evaluation, Long> {
	List<Evaluation> findByTraineeshipPositionId(Long positionId);
}
