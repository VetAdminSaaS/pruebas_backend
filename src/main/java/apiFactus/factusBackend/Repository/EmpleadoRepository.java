package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.EmpleadoVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoVeterinario, Long> {
    Optional<EmpleadoVeterinario> findByNombreAndApellido(String nombre, String apellido);
    @Modifying
    @Query("DELETE FROM EmpleadoVeterinario e WHERE e.id = :id")
    void deleteById(@Param("id") Long id);
    Optional<EmpleadoVeterinario> findByUserEmail(String email);



}
