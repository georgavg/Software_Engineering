package strategy.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

/**
 * Strategy that finds unassigned positions whose "topics" 
 * have a Jaccard similarity >= threshold with the student's "interests".
 * Also requires that the student's skill set covers the position's required skills.
 */
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

public class InterestsStrategy implements PositionsSearchStrategy {

    private double threshold = 0.3; // or pass in constructor if needed

    @Override
    public List<TraineeshipPosition> searchPositions(Student student, List<TraineeshipPosition> availablePositions) {
        // Logic: Jaccard between student's interests and position's topics
        Set<String> studentInterests = new HashSet<>(student.getInterests());
        return availablePositions.stream()
            .filter(pos -> {
                double jaccard = jaccardSimilarity(studentInterests, new HashSet<>(pos.getTopics()));
                return jaccard >= threshold && studentHasRequiredSkills(student, pos);
            })
            .collect(Collectors.toList());
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

    private boolean studentHasRequiredSkills(Student s, TraineeshipPosition p) {
        for (String skill : p.getSkills()) {
            if (!s.getSkills().contains(skill)) {
                return false;
            }
        }
        return true;
    }
}


