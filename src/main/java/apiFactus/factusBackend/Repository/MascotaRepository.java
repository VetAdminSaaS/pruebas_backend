package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.Entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByApoderadosContaining(Apoderado apoderado);
}
