package strategy.supervisor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



public class SupervisorAssignmentFactory {

    private SupervisorAssignmentFactory() {
        // private constructor to prevent direct instantiation
    }

    public static SupervisorAssignmentStrategy createStrategy(String strategyType) {
        if ("INTERESTS".equalsIgnoreCase(strategyType)) {
            return new InterestsSupervisorStrategy();
        } else if ("MIN_LOAD".equalsIgnoreCase(strategyType)) {
            return new MinLoadSupervisorStrategy();
        } else {
            throw new IllegalArgumentException("Invalid supervisor strategy type: " + strategyType);
        }
    }
}