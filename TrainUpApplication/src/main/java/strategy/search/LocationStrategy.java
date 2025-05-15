package strategy.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

/**
 * Strategy that matches a student's preferred location 
 * with the company's location that offers the position.
 * Also ensures the student's skill set covers the position's required skills.
 */
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

public class LocationStrategy implements PositionsSearchStrategy {

    @Override
    public List<TraineeshipPosition> searchPositions(Student student, List<TraineeshipPosition> availablePositions) {
        String preferredLoc = student.getPreferredLocation();
        if (preferredLoc == null || preferredLoc.isEmpty()) {
            return Collections.emptyList();
        }
        return availablePositions.stream()
            .filter(pos -> {
                // The position must have a Company, 
                // and that company’s location must match student's location
                if (pos.getCompany() == null) return false;
                boolean sameLoc = preferredLoc.equalsIgnoreCase(pos.getCompany().getCompanyLocation());
                return sameLoc && studentHasRequiredSkills(student, pos);
            })
            .collect(Collectors.toList());
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



