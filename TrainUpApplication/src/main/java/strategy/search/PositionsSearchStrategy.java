package strategy.search;

import java.util.List;

import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

public interface PositionsSearchStrategy {
    List<TraineeshipPosition> searchPositions(Student student, List<TraineeshipPosition> availablePositions);
}
