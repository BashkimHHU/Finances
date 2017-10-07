package de.hhu.droidprog17.finances;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.model.Transaction;
import de.hhu.droidprog17.finances.view.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * This Test checks if the Incognito-Mode is working correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class IncognitoTest {

    private List<String> mTransactionA;
    private List<String> mTransactionSecret;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
        insertElements();
    }

    @After
    public void clean() {
        mActivityTestRule.getActivity().setIncognitoMode(false);
    }

    @Test
    public void incognitoTest() throws InterruptedException {

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

        pressBack();
        Thread.sleep(1000);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Settings"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        withId(android.R.id.list),
                        2),
                        isDisplayed()));
        linearLayout.perform(click());

        pressBack();

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open Navigation"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Transaction History"),
                        isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        onData(hasTitle("Super-Secret"))
                .inAdapterView(withId(R.id.transactions_listview))
                .atPosition(0)
                .perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
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

    private void insertElements() {
        initializeTransaction();
        mActivityTestRule.getActivity().returnNewTransaction(mTransactionA);
        mActivityTestRule.getActivity().returnNewTransaction(mTransactionSecret);
    }

    private void initializeTransaction() {
        mTransactionSecret = new ArrayList<>();
        mTransactionSecret.add("100.00");
        mTransactionSecret.add("Super-Secret");
        mTransactionSecret.add("Excursion");
        mTransactionSecret.add(
                mActivityTestRule.getActivity().getResources().getString(R.string.type_spend));
        mTransactionSecret.add("31.07.2017");
        mTransactionSecret.add("");
        mTransactionSecret.add("");
        mTransactionSecret.add("true");

        mTransactionA = new ArrayList<>();
        mTransactionA.add("3.50");
        mTransactionA.add("Mensa");
        mTransactionA.add("Food");
        mTransactionA.add(
                mActivityTestRule.getActivity().getResources().getString(R.string.type_spend));
        mTransactionA.add("30.07.2017");
        mTransactionA.add("");
        mTransactionA.add("");
        mTransactionSecret.add("false");
    }
}
