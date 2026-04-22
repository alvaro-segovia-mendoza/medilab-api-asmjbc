package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.communication;

import java.util.Map;

public interface AppUrlService {
    String buildResetUrl(String rawToken);
    String buildUrl(String path, Map<String, String> queryParams);
}
