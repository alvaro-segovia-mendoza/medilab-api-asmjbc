package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    private static final String NOMINATIM_URL =
            "https://nominatim.openstreetmap.org/search?q={q}&format=json&limit=1&countrycodes=es";

    @Value("${geocoding.nominatim.user-agent}")
    private String userAgent;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal[] geocode(String direccion, String municipio, String provincia) {
        String queryParts = (direccion != null && !direccion.isBlank())
                ? direccion + ", " + municipio + ", " + provincia
                : municipio + ", " + provincia;
        String query = queryParts + ", España";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);
        headers.set("Accept-Language", "es");

        RequestEntity<Void> request = RequestEntity
                .get(NOMINATIM_URL, query)
                .headers(headers)
                .build();

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(request, new ParameterizedTypeReference<>() {});

        if (response.getBody() == null || response.getBody().isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(422),
                    "No se pudieron obtener coordenadas para la dirección indicada. Proporcione latitud y longitud manualmente.");
        }

        Map<String, Object> result = response.getBody().get(0);
        BigDecimal lat = new BigDecimal((String) result.get("lat")).setScale(6, RoundingMode.HALF_UP);
        BigDecimal lon = new BigDecimal((String) result.get("lon")).setScale(6, RoundingMode.HALF_UP);
        return new BigDecimal[]{lat, lon};
    }
}
