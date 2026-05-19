package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de acceso a datos para la entidad Trailer.
 */
public interface TrailerRepository extends JpaRepository<Trailer, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
