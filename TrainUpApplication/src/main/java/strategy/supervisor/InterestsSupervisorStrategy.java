package strategy.supervisor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import TraineeshipApplication.dao.ProfessorDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;

public class InterestsSupervisorStrategy implements SupervisorAssignmentStrategy {

    private double threshold = 0.3;

    @Override
    public Professor assign(List<Professor> allProfs, TraineeshipPosition position) {
        if (position.getSupervisor() != null) {
            // Already assigned
            return position.getSupervisor();
        }
        Set<String> posTopics = new HashSet<>(position.getTopics());

        double bestSim = 0.0;
        Professor bestProfessor = null;

        for (Professor p : allProfs) {
            Set<String> profInterests = new HashSet<>(p.getInterests());
            double jacc = jaccardSimilarity(profInterests, posTopics);

            if (jacc >= threshold && jacc > bestSim) {
                bestSim = jacc;
                bestProfessor = p;
            }
        }
        return bestProfessor;
    }

    private double jaccardSimilarity(Set<String> setA, Set<String> setB) {
        if (setA.isEmpty() && setB.isEmpty()) return 1.0;
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}
