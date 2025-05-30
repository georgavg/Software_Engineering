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

import TraineeshipApplication.dao.CompanyDAORepository;
import TraineeshipApplication.dao.StudentDAORepository;
import TraineeshipApplication.dao.TraineeshipDAORepository;
import TraineeshipApplication.model.Company;
import TraineeshipApplication.model.TraineeshipPosition;
import TraineeshipApplication.service.CompanyServiceImpl;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock private CompanyDAORepository companyDAO;
    @Mock private StudentDAORepository studentDAO;
    @Mock private TraineeshipDAORepository positionDAO;
    @InjectMocks private CompanyServiceImpl service;

    private Company comp;
    private TraineeshipPosition pos;

    @BeforeEach
    void init() {
        comp = new Company(); comp.setId(20L);
        pos = new TraineeshipPosition(); pos.setId(5L);
    }

    // TC-C1: addPosition
    @Test
    void addPosition_valid() {
        when(companyDAO.findByUserId(20L)).thenReturn(Optional.of(comp));

        service.addPosition(20L,pos);

        assertTrue(comp.getPositions().contains(pos));
        assertFalse(pos.isAssigned());
        verify(companyDAO).save(comp);
    }

    // TC-D1: deletePositionApproved valid
    @Test
    void delete_valid() {
        pos.setAssigned(false);
        when(positionDAO.findById(5L)).thenReturn(Optional.of(pos));

        service.deletePositionApproved(5L);
        verify(positionDAO).delete(pos);
    }

    // TC-D2: deletePositionApproved assigned
    @Test
    void delete_assigned() {
        pos.setAssigned(true);
        when(positionDAO.findById(6L)).thenReturn(Optional.of(pos));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> service.deletePositionApproved(6L));
        assertEquals("Cannot delete: Position is assigned to a student.", ex.getMessage());
    }

    // TC-D3: deletePositionApproved nonexistent
    @Test
    void delete_nonexistent() {
        when(positionDAO.findById(999L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.deletePositionApproved(999L));
    }
}
