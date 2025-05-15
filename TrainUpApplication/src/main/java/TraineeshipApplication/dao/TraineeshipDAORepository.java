	package TraineeshipApplication.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import TraineeshipApplication.model.TraineeshipPosition;

public interface TraineeshipDAORepository extends JpaRepository<TraineeshipPosition, Long> {}