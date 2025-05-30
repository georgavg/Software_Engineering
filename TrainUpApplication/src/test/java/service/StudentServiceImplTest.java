package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Student;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock private StudentDAORepository studentDAO;
    @Mock private TraineeshipDAORepository positionDAO;
    @InjectMocks private StudentServiceImpl service;

    private Student student;
    private TraineeshipPosition pos;

    @BeforeEach
    void init() {
        student = new Student(); student.setId(10L);
        pos = new TraineeshipPosition(); pos.setId(5L);
    }

    // TC-S1: Valid application
    @Test
    void apply_valid() {
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.of(student));
        when(positionDAO.findById(5L)).thenReturn(Optional.of(pos));

        service.applyForPosition(10L,5L);

        assertTrue(student.getAppliedPositions().contains(pos));
        verify(studentDAO).save(student);
    }

    // TC-S2: Duplicate application (skip save)
    @Test
    void apply_duplicate() {
        student.getAppliedPositions().add(pos);
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.of(student));
        when(positionDAO.findById(5L)).thenReturn(Optional.of(pos));

        service.applyForPosition(10L,5L);

        verify(studentDAO, never()).save(any());
    }

    // TC-S3: Student not found
    @Test
    void apply_noStudent() {
        when(studentDAO.findByUserId(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.applyForPosition(99L,5L));
        assertEquals("Student not found!", ex.getMessage());
    }

    // TC-S4: Position not found
    @Test
    void apply_noPosition() {
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.of(student));
        when(positionDAO.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.applyForPosition(10L,99L));
        assertEquals("Position not found!", ex.getMessage());
    }

    // TC-L1: Valid logbook update
    @Test
    void logbook_valid() {
        student.setAssignedTraineeship(pos);
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.of(student));

        service.updateStudentLogbook(10L,5L,"10h work");

        assertEquals("10h work", pos.getStudentLogbook());
        verify(positionDAO).save(pos);
    }

    // TC-L2: No student for logbook
    @Test
    void logbook_noStudent() {
        when(studentDAO.findByUserId(11L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.updateStudentLogbook(11L,5L,"note"));
        assertEquals("Student not found", ex.getMessage());
    }

    // TC-L3: Invalid position for logbook
    @Test
    void logbook_invalidPosition() {
        student.setAssignedTraineeship(null);
        when(studentDAO.findByUserId(10L)).thenReturn(Optional.of(student));
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.updateStudentLogbook(10L,5L,"note"));
        assertEquals("Invalid traineeship position for this student", ex.getMessage());
    }
}