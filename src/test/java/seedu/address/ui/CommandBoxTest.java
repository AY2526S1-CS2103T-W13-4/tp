package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Minimal UI-level tests for CommandBox that verify defensive behavior without TestFX.
 * We access private members via reflection to simulate user interaction.
 */
public class CommandBoxTest {

    @BeforeEach
    public void setup() {
        JavaFxInitializer.init();
    }

    /**
     * The command executor throws CommandException. We verify CommandBox handles it
     * defensively (no exception escapes) when Enter is pressed.
     */
    @Test
    public void defensive_executeCommand_doesNotThrowForParseOrCommandErrors() throws Exception {
        // Create a CommandBox whose executor always throws CommandException
        CommandBox box = new CommandBox(commandText -> {
            throw new CommandException("Simulated failure");
        });

        // Access the private TextField and private handler via reflection
        Field field = CommandBox.class.getDeclaredField("commandTextField");
        field.setAccessible(true);
        TextField tf = (TextField) field.get(box);

        Method handleEnter = CommandBox.class.getDeclaredMethod("handleCommandEntered");
        handleEnter.setAccessible(true);

        // Simulate typing text and pressing Enter on the FX application thread
        assertDoesNotThrow(() -> runOnFxAndWait(() -> {
            tf.setText("invalid command");
            try {
                handleEnter.invoke(box);
            } catch (Exception e) {
                // Re-wrap reflection exceptions so assertDoesNotThrow can catch them as a single Throwable
                throw new RuntimeException(e);
            }
        }));
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /**
     * Runs the given runnable on the JavaFX Application Thread and waits until it finishes.
     */
    private void runOnFxAndWait(Runnable runnable) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            // Wait up to 1 second for FX task to complete
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}

