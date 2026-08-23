package io.github.clientlibrary.client_library.service.cache;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicConfigHolderTest {

    private final DynamicConfigHolder holder = new DynamicConfigHolder();

    @Test
    void shouldStoreAndReadValueScopedByNamespaceAndEnvironment() {
        holder.put("my-app", "dev", "store-name", "Loja Dev");
        holder.put("my-app", "prod", "store-name", "Loja Prod");

        assertThat(holder.get("my-app", "dev", "store-name")).isEqualTo("Loja Dev");
        assertThat(holder.get("my-app", "prod", "store-name")).isEqualTo("Loja Prod");
    }

    @Test
    void shouldReturnNullForUnknownKey() {
        assertThat(holder.get("my-app", "dev", "missing-key")).isNull();
    }

    @Test
    void shouldRemoveOnlyTheRequestedEntry() {
        holder.put("my-app", "dev", "currency", "BRL");
        holder.put("my-app", "dev", "store-name", "Loja Dev");

        holder.remove("my-app", "dev", "currency");

        assertThat(holder.get("my-app", "dev", "currency")).isNull();
        assertThat(holder.get("my-app", "dev", "store-name")).isEqualTo("Loja Dev");
    }
}
