package seedu.address.ui;

import javafx.embed.swing.JFXPanel;

/**
 * Initializes the JavaFX runtime for headless tests.
 * <p>
 * This utility class ensures that JavaFX components can be safely used
 * in test environments (e.g., JUnit) where the JavaFX Application Thread
 * is not automatically started.
 * </p>
 */
public class JavaFxInitializer {

    private static boolean initialized = false;

    /**
     * Ensures that the JavaFX platform is initialized before running tests.
     * This method is safe to call multiple times.
     */
    public static void init() {
        if (!initialized) {
            // Creating a JFXPanel will start the JavaFX runtime
            new JFXPanel();
            initialized = true;
        }
    }
}

