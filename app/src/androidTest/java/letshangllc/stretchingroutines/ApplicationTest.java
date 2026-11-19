package letshangllc.stretchingroutines;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Placeholder instrumented test (replaces deprecated ApplicationTestCase).
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void packageName_isCorrect() {
        assertEquals("letshangllc.stretchingroutines", BuildConfig.APPLICATION_ID);
    }
}
