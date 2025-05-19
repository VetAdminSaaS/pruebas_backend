package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.payment_method;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface metodoPagoRepository extends JpaRepository<payment_method, Integer> {
    Optional<payment_method> findByNombre(String nombre);
}
