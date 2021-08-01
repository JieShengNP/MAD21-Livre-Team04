package sg.edu.np.mad.livre;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class Test00_SplashScreenActivityTest {
    @Rule
    public ActivityScenarioRule<SplashScreenActivity> rule = new ActivityScenarioRule<>(SplashScreenActivity.class);

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @Test
    public void testLogin() {
        try {
            Thread.sleep(8000);
        } catch (Exception e) {
        }
        intended(hasComponent(SignUpActivity.class.getName()));
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}
