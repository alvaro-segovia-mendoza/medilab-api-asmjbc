package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageBundleConsistencyTest {

    private static final Path ES_BUNDLE = Path.of("src/main/resources/messages_es.properties");
    private static final Path EN_BUNDLE = Path.of("src/main/resources/messages_en.properties");
    private static final Pattern BRACED_KEY_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_.-]+)}");
    private static final Pattern STRING_KEY_PATTERN = Pattern.compile("\"((?:api\\.auth|api\\.error|validation|security|mail|view|spring\\.security)\\.[^\"]+)\"");

    @Test
    void bundlesShouldStayAlignedAndLegacyFree() throws IOException {
        BundleData es = parseBundle(ES_BUNDLE);
        BundleData en = parseBundle(EN_BUNDLE);

        assertTrue(es.duplicateKeys().isEmpty(), () -> "Duplicate keys in ES bundle: " + es.duplicateKeys());
        assertTrue(en.duplicateKeys().isEmpty(), () -> "Duplicate keys in EN bundle: " + en.duplicateKeys());
        assertEquals(es.keys(), en.keys(), "Spanish and English bundles must expose the same key set");
        assertTrue(es.keys().stream().noneMatch(key -> key.startsWith("msg.")), "Legacy msg.* keys must not remain in ES bundle");
        assertTrue(en.keys().stream().noneMatch(key -> key.startsWith("msg.")), "Legacy msg.* keys must not remain in EN bundle");
        assertTrue(es.values().stream().noneMatch(this::looksLikeMojibake), "ES bundle contains damaged encoding");
        assertTrue(en.values().stream().noneMatch(this::looksLikeMojibake), "EN bundle contains damaged encoding");
    }

    @Test
    void allReferencedKeysInMainCodeShouldExistInBothBundles() throws IOException {
        BundleData es = parseBundle(ES_BUNDLE);
        BundleData en = parseBundle(EN_BUNDLE);
        Set<String> referencedKeys = collectReferencedKeys();

        Set<String> missingInEs = new LinkedHashSet<>(referencedKeys);
        missingInEs.removeAll(es.keys());

        Set<String> missingInEn = new LinkedHashSet<>(referencedKeys);
        missingInEn.removeAll(en.keys());

        assertTrue(missingInEs.isEmpty(), () -> "Missing keys in messages_es.properties: " + missingInEs);
        assertTrue(missingInEn.isEmpty(), () -> "Missing keys in messages_en.properties: " + missingInEn);
    }

    private Set<String> collectReferencedKeys() throws IOException {
        Set<String> keys = new LinkedHashSet<>();
        for (Path path : listJavaFiles(Path.of("src/main/java"))) {
            String content = Files.readString(path);
            Matcher bracedMatcher = BRACED_KEY_PATTERN.matcher(content);
            while (bracedMatcher.find()) {
                String key = bracedMatcher.group(1);
                if (isTrackedKey(key)) {
                    keys.add(key);
                }
            }

            Matcher stringMatcher = STRING_KEY_PATTERN.matcher(content);
            while (stringMatcher.find()) {
                String key = stringMatcher.group(1);
                if (isTrackedKey(key)) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    private boolean isTrackedKey(String key) {
        return key.startsWith("api.auth.")
                || key.startsWith("api.error.")
                || key.startsWith("validation.")
                || key.startsWith("security.")
                || key.startsWith("mail.")
                || key.startsWith("view.")
                || key.startsWith("spring.security.");
    }

    private List<Path> listJavaFiles(Path root) throws IOException {
        try (var stream = Files.walk(root)) {
            return stream.filter(path -> path.toString().endsWith(".java")).toList();
        }
    }

    private BundleData parseBundle(Path path) throws IOException {
        List<String> duplicates = new ArrayList<>();
        Set<String> keys = new LinkedHashSet<>();
        List<String> values = new ArrayList<>();

        int lineNumber = 0;
        for (String line : Files.readAllLines(path)) {
            lineNumber++;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                continue;
            }

            String[] parts = trimmed.split("=", 2);
            String key = parts[0].trim();
            String value = parts[1];
            if (!keys.add(key)) {
                duplicates.add(key + " at line " + lineNumber);
            }
            values.add(value);
        }

        return new BundleData(keys, values, duplicates);
    }

    private boolean looksLikeMojibake(String value) {
        return value.contains("Ã") || value.contains("â") || value.contains("\uFFFD");
    }

    private record BundleData(Set<String> keys, List<String> values, List<String> duplicateKeys) {
    }
}
