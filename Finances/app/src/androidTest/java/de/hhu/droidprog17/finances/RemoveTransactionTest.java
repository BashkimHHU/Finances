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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.model.Transaction;
import de.hhu.droidprog17.finances.view.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * This Test checks if a Transaction is removed correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RemoveTransactionTest {

    private List<String> mTransactionA;
    private List<String> mTransactionB;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
        insertElements();
    }

    @Test
    public void deleteTransactionTest() {
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

        ViewInteraction constraintLayout = onView(
                allOf(childAtPosition(
                        withId(R.id.transactions_listview),
                        0),
                        isDisplayed()));
        constraintLayout.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.transactions_delete_button), withText("Delete"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton2.perform(click());

        onData(hasTitle("Jeans"))
                .inAdapterView(withId(R.id.transactions_listview))
                .atPosition(0)
                .perform(click());
    }

    public static Matcher<View> childAtPosition(
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
        mActivityTestRule.getActivity().returnNewTransaction(mTransactionB);
    }

    private void initializeTransaction() {
        mTransactionA = new ArrayList<>();
        mTransactionA.add("3.49");
        mTransactionA.add("Mensa");
        mTransactionA.add("Food");
        mTransactionA.add(
                mActivityTestRule.getActivity().getResources().getString(R.string.type_spend));
        mTransactionA.add("31.07.2017");
        mTransactionA.add("");
        mTransactionA.add("");

        mTransactionB = new ArrayList<>();
        mTransactionB.add("15.00");
        mTransactionB.add("Jeans");
        mTransactionB.add("Clothes");
        mTransactionB.add(
                mActivityTestRule.getActivity().getResources().getString(R.string.type_spend));
        mTransactionB.add("29.07.2017");
        mTransactionB.add("");
        mTransactionB.add("");
    }
}
