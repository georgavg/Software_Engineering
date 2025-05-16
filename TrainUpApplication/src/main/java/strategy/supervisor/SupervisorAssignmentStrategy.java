package strategy.supervisor;

import java.util.List;

import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;

public interface SupervisorAssignmentStrategy {
    Professor assign(List<Professor> allProfs, TraineeshipPosition position);
}
