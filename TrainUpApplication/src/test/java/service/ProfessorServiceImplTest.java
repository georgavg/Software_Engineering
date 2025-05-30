package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import TraineeshipApplication.dao.ProfessorDAORepository;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.ProfessorServiceImpl;


@ExtendWith(MockitoExtension.class)
class ProfessorServiceImplTest {

    @Mock private ProfessorDAORepository profDAO;
    @InjectMocks private ProfessorServiceImpl service;

    private Professor prof;
    private TraineeshipPosition p1, p2;

    @BeforeEach
    void setup() {
        prof = new Professor(); prof.setId(1L);
        p1 = new TraineeshipPosition(); p1.setId(1L); p1.setAssigned(false);
        p2 = new TraineeshipPosition(); p2.setId(2L); p2.setAssigned(true); p2.setPassFailGrade(true);
        prof.setSupervisedPositions(List.of(p1,p2));
    }

    @Test
    void findInProgress_noCompleted() {
        when(profDAO.findById(1L)).thenReturn(Optional.of(prof));
        List<TraineeshipPosition> list = service.findInProgressPositionsForProfessor(1L);
        assertTrue(list.contains(p1)); assertFalse(list.contains(p2));
    }

    @Test
    void findCompleted_onlyCompleted() {
        when(profDAO.findById(1L)).thenReturn(Optional.of(prof));
        List<TraineeshipPosition> list = service.findCompletedPositionsForProfessor(1L);
        assertTrue(list.contains(p2)); assertFalse(list.contains(p1));
    }

    @Test
    void noProfessor_emptyLists() {
        when(profDAO.findById(2L)).thenReturn(Optional.empty());
        assertTrue(service.findInProgressPositionsForProfessor(2L).isEmpty());
        assertTrue(service.findCompletedPositionsForProfessor(2L).isEmpty());
    }
}
