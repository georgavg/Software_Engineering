package strategy.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;

public class PositionsSearchFactory {

    private PositionsSearchFactory() {
        // private constructor to prevent instantiation
    }

    public static PositionsSearchStrategy createStrategy(String strategyType) {
        if ("INTERESTS".equalsIgnoreCase(strategyType)) {
            return new InterestsStrategy(); 
        } else if ("LOCATION".equalsIgnoreCase(strategyType)) {
            return new LocationStrategy();
        } else if ("BOTH".equalsIgnoreCase(strategyType)) {
            return new BothStrategy(new InterestsStrategy(), new LocationStrategy());
        } else {
            throw new IllegalArgumentException("Invalid positions search strategy: " + strategyType);
        }
    }
}

