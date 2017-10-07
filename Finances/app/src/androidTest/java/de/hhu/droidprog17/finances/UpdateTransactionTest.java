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

import java.util.ArrayList;
import java.util.List;

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
 * This Test checks if a Transaction is updated correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class UpdateTransactionTest {

    private List<String> mTransaction;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
        insertElements();
    }

    @Test
    public void updateTransactionTest() {
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

        onData(hasTitle("Disel"))
                .inAdapterView(withId(R.id.transactions_listview))
                .atPosition(0)
                .perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.transactions_inspect_button),
                        withText("Inspect"),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(withId(R.id.finances_title));
        appCompatEditText.perform(scrollTo(), replaceText("Diesel"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.finances_add_button), withText("Ok")));
        appCompatButton2.perform(scrollTo(), click());

        onData(hasTitle("Diesel"))
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

    private void insertElements() {
        initializeTransaction();
        mActivityTestRule.getActivity().returnNewTransaction(mTransaction);
    }

    private void initializeTransaction() {
        mTransaction = new ArrayList<>();
        mTransaction.add("50.00");
        mTransaction.add("Disel");
        mTransaction.add("Fuel");
        mTransaction.add(
                mActivityTestRule.getActivity().getResources().getString(R.string.type_spend));
        mTransaction.add("31.07.2017");
        mTransaction.add("");
        mTransaction.add("");
    }
}
