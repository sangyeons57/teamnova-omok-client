package com.example.core.dialog;

import androidx.annotation.NonNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry tying enum dialog identifiers to their respective controllers and configuration.
 */
public final class DialogRegistry<T extends Enum<T>> {

    private final Map<T, DialogEntry<T>> entries;

    public DialogRegistry(@NonNull Class<T> keyType) {
        this.entries = new EnumMap<>(keyType);
    }

    public void register(@NonNull T type, @NonNull DialogController<T> controller) {
        register(type, controller, DialogConfig.DEFAULT);
    }

    public void register(
            @NonNull T type,
            @NonNull DialogController<T> controller,
            @NonNull DialogConfig config
    ) {
        entries.put(type, new DialogEntry<>(controller, config));
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
