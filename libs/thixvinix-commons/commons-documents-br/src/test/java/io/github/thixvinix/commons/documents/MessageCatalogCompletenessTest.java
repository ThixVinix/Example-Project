package io.github.thixvinix.commons.documents;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prevents catalog drift: every key declared in {@link DocumentMessageKeys} must exist in the
 * root, {@code _en} and {@code _pt_BR} bundle files, and the three files must declare exactly the
 * same key set. This is what turns a typo'd or forgotten translation into a build failure instead
 * of a silently unresolved message at runtime.
 */
@DisplayName("Message Catalog Completeness Tests")
class MessageCatalogCompletenessTest {

    private static final String BASENAME = "io/github/thixvinix/commons/documents/messages";

    @Test
    @DisplayName("Every DocumentMessageKeys constant resolves in the root, en and pt_BR bundles")
    void everyDeclaredKeyResolvesInEveryBundle() throws Exception {
        List<String> declaredKeys = declaredMessageKeys();
        assertThat(declaredKeys).isNotEmpty();

        for (String suffix : List.of("", "_en", "_pt_BR")) {
            Properties bundle = loadBundle(suffix);
            for (String key : declaredKeys) {
                assertThat(bundle.getProperty(key))
                        .as("key '%s' missing from messages%s.properties", key, suffix)
                        .isNotNull();
            }
        }
    }

    @Test
    @DisplayName("The root, en and pt_BR bundles declare exactly the same key set")
    void bundlesShareTheSameKeySet() throws Exception {
        Properties root = loadBundle("");
        Properties en = loadBundle("_en");
        Properties ptBr = loadBundle("_pt_BR");

        assertThat(en.stringPropertyNames()).isEqualTo(root.stringPropertyNames());
        assertThat(ptBr.stringPropertyNames()).isEqualTo(root.stringPropertyNames());
    }

    private static List<String> declaredMessageKeys() throws IllegalAccessException {
        List<String> keys = new ArrayList<>();
        for (Field field : DocumentMessageKeys.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                keys.add((String) field.get(null));
            }
        }
        return keys;
    }

    private static Properties loadBundle(String suffix) throws IOException {
        Properties properties = new Properties();
        String resource = BASENAME + suffix + ".properties";
        try (InputStream in = MessageCatalogCompletenessTest.class.getClassLoader().getResourceAsStream(resource)) {
            assertThat(in).as("resource '%s' must exist on the classpath", resource).isNotNull();
            properties.load(in);
        }
        return properties;
    }
}
