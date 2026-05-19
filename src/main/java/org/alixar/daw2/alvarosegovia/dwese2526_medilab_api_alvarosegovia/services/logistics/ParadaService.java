package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato de aplicacion para gestionar paradas y consultar su disponibilidad.
 */
public interface ParadaService {
    /**
     * Lista todas las paradas sin paginacion.
     *
     * @return lista completa de paradas.
     */
    List<ParadaDTO> list();

    /**
     * Lista paradas con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de paradas.
     */
    Page<ParadaDTO> listPaged(Pageable pageable);

    /**
     * Recupera el detalle de una parada.
     *
     * @param id identificador de la parada.
     * @return detalle de la parada.
     */
    ParadaDTO getDetail(Long id);

    /**
     * Crea una nueva parada.
     *
     * @param dto datos de alta de la parada.
     * @return parada creada.
     */
    ParadaDTO create(ParadaCreateDTO dto);

    /**
     * Actualiza una parada existente.
     *
     * @param dto datos actualizados de la parada.
     * @return parada actualizada.
     */
    ParadaDTO update(ParadaUpdateDTO dto);

    /**
     * Elimina una parada.
     *
     * @param id identificador de la parada.
     */
    void delete(Long id);

    /**
     * Lista paradas de una ruta.
     *
     * @param rutaId identificador de la ruta.
     * @return paradas ordenadas de la ruta.
     */
    List<ParadaDTO> listByRuta(Long rutaId);

    /**
     * Lista paradas activas para una fecha concreta.
     *
     * @param fecha fecha a consultar.
     * @return paradas activas.
     */
    List<ParadaDTO> listActiveByFecha(LocalDate fecha);

    /**
     * Lista paradas activas desde una fecha.
     *
     * @param fecha fecha minima a consultar.
     * @return paradas activas futuras o presentes.
     */
    List<ParadaDTO> listActiveFromDate(LocalDate fecha);

    /**
     * Obtiene la disponibilidad agregada de una parada.
     *
     * @param paradaId identificador de la parada.
     * @return resumen de disponibilidad.
     */
    DisponibilidadParadaDTO getDisponibilidad(Long paradaId);

    /**
     * Recupera la entidad de parada para uso interno de negocio.
     *
     * @param id identificador de la parada.
     * @return entidad persistida.
     */
    Parada getEntity(Long id);
}
