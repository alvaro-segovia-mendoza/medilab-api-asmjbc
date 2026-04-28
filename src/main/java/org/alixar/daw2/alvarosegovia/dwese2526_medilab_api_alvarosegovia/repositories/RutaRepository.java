package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    @Override
    @EntityGraph(attributePaths = {"trailer", "tecnicos", "tecnicos.profile"})
    List<Ruta> findAll();

    @Override
    @EntityGraph(attributePaths = {"trailer"})
    Page<Ruta> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"trailer", "tecnicos", "tecnicos.profile"})
    java.util.Optional<Ruta> findById(Long id);

    @EntityGraph(attributePaths = {"trailer", "tecnicos", "tecnicos.profile"})
    List<Ruta> findByActivaTrueOrderByNombreAsc();

    boolean existsByTecnicosId(Long userId);
}
