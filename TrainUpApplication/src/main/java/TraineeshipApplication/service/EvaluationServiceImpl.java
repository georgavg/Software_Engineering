package TraineeshipApplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;
import TraineeshipApplication.model.TraineeshipPosition;
@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private TraineeshipDAORepository positionRepo;
    
    @Override
    public Evaluation getEvaluation(Long posId, EvaluationType type) {
        TraineeshipPosition pos = positionRepo.findById(posId).orElse(null);
        if (pos == null) return null;
        return pos.getEvaluations()
                  .stream()
                  .filter(e -> e.getEvaluationType() == type)
                  .findFirst()
                  .orElse(null);
    }
    
    @Override
    public void saveOrUpdateEvaluation(Long posId, Evaluation evaluation, EvaluationType type) {
        TraineeshipPosition pos = positionRepo.findById(posId).orElse(null);
        if (pos == null) return;
        
        // Ψάχνουμε για υπάρχουσα αξιολόγηση του συγκεκριμένου τύπου
        Evaluation existingEvaluation = pos.getEvaluations()
            .stream()
            .filter(e -> e.getEvaluationType() == type)
            .findFirst()
            .orElse(null);
        
        if(existingEvaluation != null) {
            // Ενημέρωση των κριτηρίων
            existingEvaluation.setMotivation(evaluation.getMotivation());
            existingEvaluation.setEfficiency(evaluation.getEfficiency());
            existingEvaluation.setEffectiveness(evaluation.getEffectiveness());
        } else {
            // Αν δεν υπάρχει, δημιουργούμε νέα αξιολόγηση
            evaluation.setEvaluationType(type);
            evaluation.setTraineeshipPosition(pos);
            pos.getEvaluations().add(evaluation);
        }
        
        positionRepo.save(pos);
    }

	
	@Override
	public List<Evaluation> findByTraineeshipPosition(Long posId) {
		TraineeshipPosition pos = positionRepo.findById(posId).orElse(null);
		 if (pos == null) return null;
	     return (List<Evaluation>) pos.getEvaluations();
	    }
}
