package sg.edu.np.mad.livre;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
public class Test02_LibraryActivityTest {

    @Rule
    public ActivityScenarioRule<LibraryActivity> rule = new ActivityScenarioRule<>(LibraryActivity.class);

    @Test
    public void testRecyclerViewVisibility() {
        onView(withId(R.id.libraryRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerListContent(){
        for(int i=0; i<1; i++) {
            onView(withId(R.id.libraryRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
        }
    }
}