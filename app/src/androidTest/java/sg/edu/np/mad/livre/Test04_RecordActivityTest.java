package sg.edu.np.mad.livre;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class Test04_RecordActivityTest {
    @Rule
    public ActivityScenarioRule<RecordActivity> rule = new ActivityScenarioRule<>(RecordActivity.class);

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @Test
    public void loadActivity() {
        onView(withId(R.id.recordFrame)).check(matches(isDisplayed()));
    }

    @Test
    public void testTag(){
        onView(withId(R.id.recordLibraryTag)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        intended(hasComponent(LibraryActivity.class.getName()));
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}
