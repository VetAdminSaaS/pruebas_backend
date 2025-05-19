package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAll();
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByIdentification(String identification);
    boolean existsByNames(String name);





}
