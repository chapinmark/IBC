package ibcalpha.ibc;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * The property under test is not "does it tick the box" -- that needs a real TWS
 * config dialog -- but "can it ever throw". ConfigurationTask.ConfigTaskRunner
 * calls releaseConfigDialog() after t.get() with no finally, so an exception
 * escaping run() pins the config dialog open and TWS never opens the API port.
 */
public class ConfigureApiLogTaskTest {

    /**
     * A real JDialog cannot be built in this JVM (headless), so the case
     * exercised is the harsher one anyway: configDialog is null because
     * initialise() never ran, which is what a failure earlier in the config
     * sequence looks like. run() must absorb it.
     */
    @Test
    public void neverThrowsWhenTheDialogIsNotWhatWeExpect() {
        Settings.initialise(new DefaultSettings("/nonexistent/ibc.ini"));

        new ConfigureApiLogTask(true).run();
        new ConfigureApiLogTask(false).run();
    }

    @Test
    public void labelMatchesTheStringShippedInTws1045() {
        // ji18n/Language.properties in jts4launch-1045.jar:
        //     Create_raw_API_log = Create API message log file
        assertEquals("Create API message log file", ConfigureApiLogTask.CHECKBOX_LABEL);
    }
}
