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

import de.hhu.droidprog17.finances.model.Account;
import de.hhu.droidprog17.finances.view.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * This Test checks if a Account is removed correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RemoveAccountTest {

    private List<String> mAccountA;
    private List<String> mAccountB;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startUp() {
        clearDataBase();
        insertElements();
    }

    @Test
    public void deleteAccountTest() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open Navigation"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Account Balance"),
                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(childAtPosition(
                        withId(R.id.balance_listview),
                        0),
                        isDisplayed()));
        constraintLayout.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.balance_delete_button), withText("Delete"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(is("android.widget.LinearLayout")),
                                withParent(withClassName(is("android.widget.LinearLayout"))))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onData(hasTitle("Mensa"))
                .inAdapterView(withId(R.id.balance_listview))
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

    private void insertElements() {
        initializeTransaction();
        mActivityTestRule.getActivity().returnNewAccount(mAccountA);
        mActivityTestRule.getActivity().returnNewAccount(mAccountB);
    }

    private void initializeTransaction() {
        mAccountA = new ArrayList<>();
        mAccountA.add("Sparkasse");
        mAccountA.add("100.0");

        mAccountB = new ArrayList<>();
        mAccountB.add("Mensa");
        mAccountB.add("20.50");
    }
}
