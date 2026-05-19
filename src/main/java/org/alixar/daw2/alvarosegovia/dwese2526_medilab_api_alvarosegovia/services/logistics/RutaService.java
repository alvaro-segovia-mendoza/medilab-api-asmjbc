package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de aplicacion para gestionar rutas operativas de trailers.
 */
public interface RutaService {
    /**
     * Lista todas las rutas sin paginacion.
     *
     * @return lista completa de rutas.
     */
    List<RutaDTO> list();

    /**
     * Lista rutas con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de rutas.
     */
    Page<RutaDTO> listPaged(Pageable pageable);

    /**
     * Lista las rutas marcadas como activas.
     *
     * @return rutas activas.
     */
    List<RutaDTO> listActive();

    /**
     * Recupera el detalle de una ruta.
     *
     * @param id identificador de la ruta.
     * @return detalle de la ruta.
     */
    RutaDTO getDetail(Long id);

    /**
     * Crea una nueva ruta operativa.
     *
     * @param dto datos de alta de la ruta.
     * @return ruta creada.
     */
    RutaDTO create(RutaCreateDTO dto);

    /**
     * Actualiza una ruta existente.
     *
     * @param dto datos actualizados de la ruta.
     * @return ruta actualizada.
     */
    RutaDTO update(RutaUpdateDTO dto);

    /**
     * Elimina una ruta.
     *
     * @param id identificador de la ruta.
     */
    void delete(Long id);
}
