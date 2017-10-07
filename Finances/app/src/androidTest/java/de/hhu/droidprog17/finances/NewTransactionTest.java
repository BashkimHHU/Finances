package de.hhu.droidprog17.finances;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import de.hhu.droidprog17.finances.model.Transaction;
import de.hhu.droidprog17.finances.view.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * This Test checks if a new Transaction is created correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewTransactionTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
    }

    @Test
    public void addTransactionTest() {
        ViewInteraction appCompatEditText = onView(
                withId(R.id.finances_amount));
        appCompatEditText.perform(scrollTo(), replaceText("3.25"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.finances_title));
        appCompatEditText2.perform(scrollTo(), replaceText("Mensa"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.finances_add_button), withText("Ok")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open Navigation"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Transaction History"),
                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        onData(hasTitle("Mensa"))
                .inAdapterView(withId(R.id.transactions_listview))
                .atPosition(0)
                .perform(click());
    }

    public static Matcher<Object> hasTitle(final String title) {
        return new BoundedMatcher<Object, Transaction>(Transaction.class) {
            @Override
            public boolean matchesSafely(Transaction transaction) {
                return transaction.getTitle().equals(title);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has title" + title);
            }
        };
    }

    private void clearDataBase() {
        mActivityTestRule.getActivity().clearDataBase();
    }
}
