package TraineeshipApplication.service;

import java.util.List;

import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;

public interface EvaluationService {
    // Επιστρέφει την αξιολόγηση για μια θέση και συγκεκριμένο τύπο
    Evaluation getEvaluation(Long posId, EvaluationType type);
    
    // Αποθηκεύει ή ενημερώνει την αξιολόγηση για μια θέση και συγκεκριμένο τύπο
    void saveOrUpdateEvaluation(Long posId, Evaluation evaluation, EvaluationType type);

	List<Evaluation> findByTraineeshipPosition(Long posId);
}

