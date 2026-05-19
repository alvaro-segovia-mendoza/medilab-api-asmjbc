package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de aplicacion para gestionar trailers del sistema.
 */
public interface TrailerService {
    /**
     * Lista todos los trailers sin paginacion.
     *
     * @return lista completa de trailers.
     */
    List<TrailerDTO> list();

    /**
     * Lista trailers con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de trailers.
     */
    Page<TrailerDTO> listPaged(Pageable pageable);

    /**
     * Recupera el detalle de un trailer.
     *
     * @param id identificador del trailer.
     * @return detalle del trailer.
     */
    TrailerDTO getDetail(Long id);

    /**
     * Crea un nuevo trailer.
     *
     * @param dto datos de alta del trailer.
     * @return trailer creado.
     */
    TrailerDTO create(TrailerCreateDTO dto);

    /**
     * Actualiza un trailer existente.
     *
     * @param dto datos actualizados del trailer.
     * @return trailer actualizado.
     */
    TrailerDTO update(TrailerUpdateDTO dto);

    /**
     * Elimina un trailer.
     *
     * @param id identificador del trailer.
     */
    void delete(Long id);
}
