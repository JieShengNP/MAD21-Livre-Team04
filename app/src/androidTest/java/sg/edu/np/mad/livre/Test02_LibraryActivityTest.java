package sg.edu.np.mad.livre;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test02_LibraryActivityTest {

    @Rule
    public ActivityScenarioRule<LibraryActivity> rule = new ActivityScenarioRule<>(LibraryActivity.class);

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @Test
    public void test1_RecyclerViewVisibility() {
        onView(withId(R.id.libraryRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void test2_RecyclerListContent() {
        onView(withId(R.id.libraryRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        intended(hasComponent(BookDetails.class.getName()));
    }

    @Test
    public void test3_OpenArchive(){
        onView(withId(R.id.libraryArchiveImage)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        intended(hasComponent(ArchiveActivity.class.getName()));
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}