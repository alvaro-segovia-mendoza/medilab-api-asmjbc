package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    @Override
    @EntityGraph(attributePaths = "trailer")
    List<Ruta> findAll();

    @EntityGraph(attributePaths = "trailer")
    List<Ruta> findByActivaTrueOrderByNombreAsc();
}
