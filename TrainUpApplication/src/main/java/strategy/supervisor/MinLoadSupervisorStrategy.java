package strategy.supervisor;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import TraineeshipApplication.dao.ProfessorDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;

public class MinLoadSupervisorStrategy implements SupervisorAssignmentStrategy {

    @Override
    public Professor assign(List<Professor> allProfs, TraineeshipPosition position) {
        if (position.getSupervisor() != null) {
            // Already assigned, do nothing
            return position.getSupervisor();
        }

        // pick professor with the minimum load
        Professor minLoadProf = null;
        int minCount = Integer.MAX_VALUE;

        for (Professor p : allProfs) {
            int count = (p.getSupervisedPositions() == null) 
                            ? 0 
                            : p.getSupervisedPositions().size();
            if (count < minCount) {
                minCount = count;
                minLoadProf = p;
            }
        }
        return minLoadProf; // could be null if no prof found
    }
}
