package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByNombre(String nombre);

    @Override
    Optional<Tecnico> findById(Long id);

    @Query("SELECT t FROM Tecnico t LEFT JOIN FETCH t.citas WHERE t.id = :id")
    Optional<Tecnico> findByIdWithCitas(@Param("id") Long id);
}
