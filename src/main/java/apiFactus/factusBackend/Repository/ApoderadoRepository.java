package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.Entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApoderadoRepository extends JpaRepository<Apoderado, Long> {
    Optional<Apoderado> findByEmail(String email);
    Optional<Apoderado> findByNombreAndApellido(String nombre, String apellido);
    @Modifying
    @Query("DELETE FROM Mascota m WHERE :apoderado MEMBER OF m.apoderados")
    void eliminarRelationMascotas(@Param("apoderado") Apoderado apoderado);




}
