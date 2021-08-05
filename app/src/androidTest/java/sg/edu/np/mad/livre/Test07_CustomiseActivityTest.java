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

public class Test07_CustomiseActivityTest {
    @Rule
    public ActivityScenarioRule<CustomiseBook> rule = new ActivityScenarioRule<>(CustomiseBook.class);

    @Test
    public void pageLoad() {
        onView(withId(R.id.customiseText)).check(matches(isDisplayed()));
    }

}