package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaReservableDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReservaDisponibilidadService {
    List<RutaReservableDTO> listReservableRoutes(LocalDate fechaDesde);
}
