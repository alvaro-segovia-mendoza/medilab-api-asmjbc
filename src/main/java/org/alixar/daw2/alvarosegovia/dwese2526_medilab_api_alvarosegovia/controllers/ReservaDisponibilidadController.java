package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaReservableDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.ReservaDisponibilidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para exponer disponibilidad de rutas y paradas reservables.
 */
@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Consultas agregadas para el flujo de reserva del frontend")
public class ReservaDisponibilidadController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaDisponibilidadController.class);

    @Autowired
    private ReservaDisponibilidadService reservaDisponibilidadService;

    /**
     * Devuelve rutas activas con paradas reservables desde una fecha dada.
     *
     * @param fechaDesde fecha minima opcional para filtrar disponibilidad.
     * @return lista de rutas con paradas y slots reservables.
     */
    @GetMapping("/disponibilidad")
    @Operation(
            summary = "Rutas y paradas reservables",
            description = "Devuelve las rutas activas con sus próximas paradas que aún tienen slots persistidos reservables desde una fecha dada."
    )
    public ResponseEntity<List<RutaReservableDTO>> getDisponibilidadReservas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde) {
        logger.info("Consultando disponibilidad reservable desde fecha={}", fechaDesde);
        return ResponseEntity.ok(reservaDisponibilidadService.listReservableRoutes(fechaDesde));
    }
}
