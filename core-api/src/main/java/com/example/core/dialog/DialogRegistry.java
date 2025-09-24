package com.example.core.dialog;

import androidx.annotation.NonNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry tying enum dialog identifiers to their respective controllers and configuration.
 */
public final class DialogRegistry<T extends Enum<T>> {

    private final Class<T> keyType;
    private final Map<T, DialogEntry<T>> entries;

    public DialogRegistry(@NonNull Class<T> keyType) {
        this.keyType = keyType;
        this.entries = new EnumMap<>(keyType);
    }

    @NonNull
    public Class<T> getKeyType() {
        return keyType;
    }

    public void registerProvider(@NonNull DialogProvider<T> provider) {
        Class<T> providerKeyType = provider.getDialogKeyType();
        if (!keyType.equals(providerKeyType)) {
            throw new IllegalArgumentException(
                    "Provider key type does not match registry key type. Expected " +
                            keyType.getName() + " but was " + providerKeyType.getName()
            );
        }

        T key = provider.getDialogKey();
        if (entries.containsKey(key)) {
            throw new IllegalStateException("Dialog already registered for key: " + key.name());
        }

        DialogController<T> controller = provider.createController();
        DialogConfig config = provider.getDialogConfig();
        entries.put(key, new DialogEntry<>(controller, config));
    }

    /* package */ @NonNull
    DialogEntry<T> getEntry(@NonNull T type) {
        DialogEntry<T> entry = entries.get(type);
        if (entry == null) {
            throw new IllegalArgumentException("No dialog registered for type: " + type.name());
        }
        return entry;
    }

    public boolean isRegistered(@NonNull T type) {
        return entries.containsKey(type);
    }

    static final class DialogEntry<T extends Enum<T>> {
        private final DialogController<T> controller;
        private final DialogConfig config;

        DialogEntry(@NonNull DialogController<T> controller, @NonNull DialogConfig config) {
            this.controller = controller;
            this.config = config;
        }

        @NonNull
        DialogController<T> getController() {
            return controller;
        }

        @NonNull
        DialogConfig getConfig() {
            return config;
        }
    }
}
