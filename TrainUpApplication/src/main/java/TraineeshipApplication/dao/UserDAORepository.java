package TraineeshipApplication.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TraineeshipApplication.model.*;

@Repository
public interface UserDAORepository extends JpaRepository<User, Long>{
	 Optional <User> findByUsername(String username);
	 Optional <User>  findById(Long id);
	 boolean existsByUsername(String username);
	 boolean existsByEmail(String email);
	 User findByEmail(String email);
}
