package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Evaluation;
import TraineeshipApplication.model.EvaluationType;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.EvaluationServiceImpl;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    @Mock private TraineeshipDAORepository posDAO;
    @InjectMocks private EvaluationServiceImpl service;
    private TraineeshipPosition pos;

    @BeforeEach
    void setup() {
        pos = new TraineeshipPosition(); pos.setId(7L);
        pos.setEvaluations(new ArrayList<>());
    }

    // getEvaluation when none exists
    @Test
    void getEvaluation_none() {
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        assertNull(service.getEvaluation(7L, EvaluationType.COMPANY));
    }

    // saveOrUpdateEvaluation: add new
    @Test
    void saveOrUpdate_add() {
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        Evaluation eval = new Evaluation(); eval.setMotivation(3); eval.setEfficiency(4); eval.setEffectiveness(5);

        service.saveOrUpdateEvaluation(7L, eval, EvaluationType.COMPANY);

        assertEquals(1, pos.getEvaluations().size());
        Evaluation saved = pos.getEvaluations().get(0);
        assertEquals(EvaluationType.COMPANY, saved.getEvaluationType());
        verify(posDAO).save(pos);
    }

    // saveOrUpdateEvaluation: update existing
    @Test
    void saveOrUpdate_update() {
        Evaluation existing = new Evaluation(); existing.setEvaluationType(EvaluationType.PROFESSOR);
        existing.setMotivation(1); pos.getEvaluations().add(existing);
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        Evaluation update = new Evaluation(); update.setMotivation(5); update.setEfficiency(5); update.setEffectiveness(5);

        service.saveOrUpdateEvaluation(7L, update, EvaluationType.PROFESSOR);

        assertEquals(1, pos.getEvaluations().size());
        assertEquals(5, pos.getEvaluations().get(0).getMotivation());
        verify(posDAO).save(pos);
    }

    // findByTraineeshipPosition
    @Test
    void findByTraineeshipPosition() {
        Evaluation eval = new Evaluation(); pos.getEvaluations().add(eval);
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        List<Evaluation> list = service.findByTraineeshipPosition(7L);
        assertEquals(1, list.size());
    }
}
