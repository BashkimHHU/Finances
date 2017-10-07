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

import de.hhu.droidprog17.finances.model.Account;
import de.hhu.droidprog17.finances.view.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * This Test checks if a new Account is created correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewAccountTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
    }

    @Test
    public void addAccountTest() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open Navigation"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Create Account"),
                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.create_account_name), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.create_account_name), isDisplayed()));
        appCompatEditText2.perform(replaceText("Mensa"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.create_account_balance), isDisplayed()));
        appCompatEditText3.perform(replaceText("20"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.create_account_add_button), withText("ADD"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open Navigation"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Account Balance"),
                        isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        onData(hasTitle("Mensa"))
                .inAdapterView(withId(R.id.balance_listview))
                .atPosition(0)
                .perform(click());
    }

    public static Matcher<Object> hasTitle(final String title) {
        return new BoundedMatcher<Object, Account>(Account.class) {
            @Override
            public boolean matchesSafely(Account account) {
                return account.getAccountName().equals(title);
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
