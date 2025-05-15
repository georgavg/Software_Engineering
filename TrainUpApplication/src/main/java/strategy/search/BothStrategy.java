package strategy.search;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

/**
 * A strategy that returns positions that match BOTH the interests-based approach
 * AND the location-based approach (intersection of results).
 */
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

public class BothStrategy implements PositionsSearchStrategy {

    private final PositionsSearchStrategy interestsStrategy;
    private final PositionsSearchStrategy locationStrategy;

    public BothStrategy(PositionsSearchStrategy interestsStrategy,
                        PositionsSearchStrategy locationStrategy) {
        this.interestsStrategy = interestsStrategy;
        this.locationStrategy = locationStrategy;
    }

    @Override
    public List<TraineeshipPosition> searchPositions(Student student, List<TraineeshipPosition> availablePositions) {
        List<TraineeshipPosition> byInterests = interestsStrategy.searchPositions(student, availablePositions);
        Set<Long> interestIds = byInterests.stream()
            .map(TraineeshipPosition::getId)
            .collect(Collectors.toSet());

        List<TraineeshipPosition> byLocation = locationStrategy.searchPositions(student, availablePositions);

        // Intersection
        return byLocation.stream()
            .filter(pos -> interestIds.contains(pos.getId()))
            .collect(Collectors.toList());
    }
}

