package iiitb.messagingservice.repo;

import iiitb.messagingservice.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmployeeRepo extends JpaRepository<Employees,Long> {
    //Optional<Employees> findByEmployeeId(Long employeeId);
    Optional<Employees> findByEmail(String email);
    Optional<Employees> findById(Long Id);
}
