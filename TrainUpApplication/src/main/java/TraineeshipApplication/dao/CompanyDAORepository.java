package TraineeshipApplication.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TraineeshipApplication.model.Company;
@Repository
public interface CompanyDAORepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserId(Long userId);
}