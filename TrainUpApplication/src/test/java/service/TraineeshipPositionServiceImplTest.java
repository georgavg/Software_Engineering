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
import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Professor;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.TraineeshipPositionServiceImpl;


@ExtendWith(MockitoExtension.class)
class TraineeshipPositionServiceImplTest {

    @Mock private TraineeshipDAORepository posDAO;
    @Mock private StudentDAORepository studentDAO;
    @Mock private ProfessorDAORepository profDAO;
    @InjectMocks private TraineeshipPositionServiceImpl service;

    private Student student;
    private TraineeshipPosition pos;

    @BeforeEach
    void init() {
        student = new Student(); student.setId(10L);
        pos = new TraineeshipPosition(); pos.setId(7L);
    }

    // assignPositionToStudent
    @Test
    void assignPositionToStudent_valid() {
        when(studentDAO.findById(10L)).thenReturn(Optional.of(student));
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));

        service.assignPositionToStudent(10L,7L);
        assertTrue(pos.isAssigned());
        assertEquals(pos, student.getAssignedTraineeship());
        verify(posDAO).save(pos);
        verify(studentDAO).save(student);
    }

    // assignSupervisor
    @Test
    void assignSupervisor_valid() {
        Professor prof = new Professor(); prof.setId(3L);
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        when(profDAO.findAll()).thenReturn(List.of(prof));

        service.assignSupervisor(7L, "any");
        assertEquals(prof, pos.getSupervisor());
        verify(posDAO).save(pos);
    }

    // completeTraineeship missing evals
    @Test
    void complete_missingEvals() {
        when(posDAO.findById(7L)).thenReturn(Optional.of(pos));
        RuntimeException ex = assertThrows(IllegalStateException.class,
            () -> service.completeTraineeship(7L,true));
        assertTrue(ex.getMessage().contains("missing evaluations"));
    }

    // getInProgressPositions
    @Test
    void getInProgressPositions_filters() {
        pos.setAssigned(true); pos.setPassFailGrade(false);
        when(posDAO.findAll()).thenReturn(List.of(pos));
        List<TraineeshipPosition> list = service.getInProgressPositions();
        assertEquals(1, list.size());
    }

    // findMatchesForStudent no student
    @Test
    void findMatches_noStudent() {
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.empty());
        assertTrue(service.findMatchesForStudent(10L,"s").isEmpty());
    }
}

