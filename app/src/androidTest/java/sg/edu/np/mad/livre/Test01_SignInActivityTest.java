package sg.edu.np.mad.livre;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
public class Test01_SignInActivityTest {

    @Rule
    public ActivityScenarioRule<SignInActivity> rule = new ActivityScenarioRule<>(SignInActivity.class);

    @Before
    public void setUp() throws Exception{
        Intents.init();
    }

    @Test
    public void testLogin() {
        onView(withId(R.id.signinEmailEditText)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.signinPasswordEditText)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.signinBtn)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        intended(hasComponent(LibraryActivity.class.getName()));
    }

    @After
    public void tearDown() throws Exception{
        Intents.release();
    }

}