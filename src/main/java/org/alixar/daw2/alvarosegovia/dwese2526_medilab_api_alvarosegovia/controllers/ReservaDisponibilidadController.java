package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaReservableDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.ReservaDisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Consultas agregadas para el flujo de reserva del frontend")
public class ReservaDisponibilidadController {

    @Autowired
    private ReservaDisponibilidadService reservaDisponibilidadService;

    @GetMapping("/disponibilidad")
    @Operation(
            summary = "Rutas y paradas reservables",
            description = "Devuelve las rutas activas con sus próximas paradas que aún tienen slots reservables desde una fecha dada."
    )
    public ResponseEntity<List<RutaReservableDTO>> getDisponibilidadReservas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde) {
        return ResponseEntity.ok(reservaDisponibilidadService.listReservableRoutes(fechaDesde));
    }
}
