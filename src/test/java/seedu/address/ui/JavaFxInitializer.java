package seedu.address.ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 * Ensures that JavaFX toolkit is initialized for UI tests.
 * Works reliably in headless CI environments.
 */
public final class JavaFxInitializer {

    private static boolean initialized = false;

    private JavaFxInitializer() {
        // utility class
    }

    /**
     * Initializes the JavaFX runtime if it hasn't been started yet.
     * This method is safe to call multiple times.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }

        try {
            // JFXPanel triggers the JavaFX platform initialization even in headless mode
            new JFXPanel();

            if (!Platform.isImplicitExit()) {
                Platform.setImplicitExit(false);
            }

            initialized = true;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize JavaFX platform", e);
        }
    }
}

