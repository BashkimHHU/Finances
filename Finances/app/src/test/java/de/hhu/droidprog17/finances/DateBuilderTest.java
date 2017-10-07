package de.hhu.droidprog17.finances;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import de.hhu.droidprog17.finances.view.DatePickerDialogFragment;

import static org.junit.Assert.*;

/**
 * This Test checks if the selected Date is formatted to YYYY.MM.DD correctly
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see DatePickerDialogFragment
 */

public class DateBuilderTest {

    private DatePickerDialogFragment mFragment;

    @Before
    public void defineFragment() {
        mFragment = new DatePickerDialogFragment();
    }

    @Test
    public void checkDaySmaller10() throws ComparisonFailure {
        String date = mFragment.convertDate(2017, 10, 9);
        assertEquals("2017.11.09", date);
    }

    @Test
    public void checkMonthSmaller10() throws ComparisonFailure {
        String date = mFragment.convertDate(2017, 8, 10);
        assertEquals("2017.09.10", date);
    }

    @Test
    public void checkBothSmaller10() throws ComparisonFailure {
        String date = mFragment.convertDate(2017, 0, 1);
        assertEquals("2017.01.01", date);
    }

    @Test
    public void checkBothGreater10() throws ComparisonFailure {
        String date = mFragment.convertDate(2017, 10, 10);
        assertEquals("2017.11.10", date);
    }
}
