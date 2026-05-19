package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaReservableDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato de aplicacion para exponer rutas y paradas reservables al frontend.
 */
public interface ReservaDisponibilidadService {
    /**
     * Obtiene rutas activas con paradas que todavia tienen slots reservables.
     *
     * @param fechaDesde fecha minima opcional para filtrar resultados.
     * @return rutas reservables agrupadas con su disponibilidad.
     */
    List<RutaReservableDTO> listReservableRoutes(LocalDate fechaDesde);
}
