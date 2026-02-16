package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    Optional<Cita> findById(Long id);
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.tecnico WHERE c.id = :id")
    Optional<Cita> findByIdWithTecnicos(@Param("id") Long id);

}
