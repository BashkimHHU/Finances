package de.hhu.droidprog17.finances.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * This Fragment provides a DatePickerDialog to determine the transactions' date
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see MainContentFragment
 */

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DATE_PICKER_DIALOG";
    private DatePickerDialogFragmentInterface mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT > 22) {
            try {
                mCallback = (DatePickerDialogFragmentInterface) context;
            } catch (ClassCastException e) {
                Log.e(TAG, context.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (DatePickerDialogFragmentInterface) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, activity.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return (new DatePickerDialog(getActivity(), this, year, month, day));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCallback.onDateSet(convertDate(year, month, dayOfMonth));
    }

    /**
     * Converts the chosen date into the format: DD.MM.YYYY
     *
     * @param year       year selected
     * @param month      month selected
     * @param dayOfMonth day selected
     * @return Date formatted into DD.MM.YYYY
     */
    public String convertDate(int year, int month, int dayOfMonth) {
        StringBuilder date = new StringBuilder(10);
        date.append(year + ".");

        if (month < 9) {
            date.append("0" + (month + 1) + ".");
        } else {
            date.append((month + 1) + ".");
        }

        if (dayOfMonth < 10) {
            date.append("0" + dayOfMonth);
        } else {
            date.append(dayOfMonth);
        }

        return date.toString();
    }

    /**
     * This Interface gives report to attached Activity that the user has chosen a date
     */
    public interface DatePickerDialogFragmentInterface {
        /**
         * Returns the selected date
         *
         * @param date selected date
         */
        void onDateSet(String date);
    }
}
