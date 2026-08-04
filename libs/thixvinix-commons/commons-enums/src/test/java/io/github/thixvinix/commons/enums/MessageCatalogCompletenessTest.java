package io.github.thixvinix.commons.enums;

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

@DisplayName("Message Catalog Completeness Tests")
class MessageCatalogCompletenessTest {

    private static final String BASENAME = "io/github/thixvinix/commons/enums/messages";

    @Test
    @DisplayName("Every EnumMessageKeys constant resolves in the root, en and pt_BR bundles")
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
        for (Field field : EnumMessageKeys.class.getDeclaredFields()) {
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
