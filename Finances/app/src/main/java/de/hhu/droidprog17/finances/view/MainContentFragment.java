package de.hhu.droidprog17.finances.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.R;

/**
 * This Fragment provides the functionality to create a transaction
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see MainActivity
 * @see TransactionsUpdateActivity
 */

public class MainContentFragment extends Fragment implements
        View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "Main_Frag";
    private View mView;
    private MainContentFragment.MainContentFragmentInterface mCallback;
    private boolean isAttachedOnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_content_fragment, container, false);
        initialWork(mView);
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttachedOnUpdate = context instanceof TransactionsUpdateActivity;
        if (Build.VERSION.SDK_INT > 22) {
            try {
                mCallback = (MainContentFragment.MainContentFragmentInterface) context;
            } catch (ClassCastException e) {
                Log.e(TAG, context.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isAttachedOnUpdate = activity instanceof TransactionsUpdateActivity;
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (MainContentFragment.MainContentFragmentInterface) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, activity.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finances_add_button:
                List<String> entry = new ArrayList<>();
                String amount = ((EditText) getActivity()
                        .findViewById(R.id.finances_amount)).getText().toString().trim();
                if (!amount.equals("")) {
                    entry.add(amount);
                    String title = ((EditText) getActivity()
                            .findViewById(R.id.finances_title)).getText().toString().trim();
                    entry.add(title);
                    String category = ((EditText) getActivity()
                            .findViewById(R.id.finances_category)).getText().toString().trim();
                    entry.add(category);
                    String status = ((Spinner) getActivity()
                            .findViewById(R.id.finances_type)).getSelectedItem().toString().trim();
                    entry.add(status);
                    String date = ((EditText) getActivity().findViewById(R.id.finances_date))
                            .getText().toString().trim();
                    entry.add(date);
                    String account = ((EditText) getActivity()
                            .findViewById(R.id.finances_account)).getText().toString().trim();
                    entry.add(account);
                    String information = ((EditText) getActivity()
                            .findViewById(R.id.finances_notes)).getText().toString().trim();
                    entry.add(information);
                    mCallback.returnNewTransaction(entry);
                } else {
                    mCallback.handleMissingInput();
                }
                break;
            case R.id.finances_delete_button:
                if (!isAttachedOnUpdate) {
                    ((EditText) getActivity().findViewById(R.id.finances_amount)).setText("");
                    ((EditText) getActivity().findViewById(R.id.finances_title)).setText("");
                    ((EditText) getActivity().findViewById(R.id.finances_category)).setText("");
                    ((EditText) getActivity().findViewById(R.id.finances_date)).setText("");
                    ((EditText) getActivity().findViewById(R.id.finances_account)).setText("");
                    ((EditText) getActivity().findViewById(R.id.finances_notes)).setText("");
                }
                mCallback.onAbort();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.finances_category:
                    mCallback.openCategoryFragment();
                    break;
                case R.id.finances_date:
                    mCallback.openDatePickerDialogFragment();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * Set the TextField holding the category to a specified value
     *
     * @param category category chosen by user
     */
    public void updateCategory(String category) {
        EditText categoryEditText = (EditText) mView.findViewById(R.id.finances_category);
        categoryEditText.setText(category);
    }

    /**
     * Fills all TextFields with the corresponding information.
     *
     * @param amount      transaction amount
     * @param title       transaction title
     * @param category    transaction category
     * @param date        transaction date
     * @param account     transaction account
     * @param information transaction information
     * @param type        transaction type
     * @see TransactionsUpdateActivity
     */
    public void setText(String amount, String title, String category, String date, String account,
                        String information, String type) {
        ((EditText) mView.findViewById(R.id.finances_amount)).setText(amount);
        ((EditText) mView.findViewById(R.id.finances_title)).setText(title);
        ((EditText) mView.findViewById(R.id.finances_category)).setText(category);
        ((EditText) mView.findViewById(R.id.finances_date)).setText(date);
        ((EditText) mView.findViewById(R.id.finances_account)).setText(account);
        ((EditText) mView.findViewById(R.id.finances_notes)).setText(information);

        Spinner financeTypeSpinner = (Spinner) mView.findViewById(R.id.finances_type);
        if (type.equals(getResources().getString(R.string.type_spend))) {
            financeTypeSpinner.setSelection(0);
        } else {
            financeTypeSpinner.setSelection(1);
        }
    }

    /**
     * Set the TextField holding the date to a specified value
     *
     * @param date date chosen by user
     */
    public void setDate(String date) {
        ((EditText) getActivity().findViewById(R.id.finances_date)).setText(date);
    }

    private void initialWork(View view) {
        initializeSpinner(view);
        implementListeners(view);
    }

    private void initializeSpinner(View view) {
        Spinner financeTypeSpinner = (Spinner) view.findViewById(R.id.finances_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getActivity(),
                R.array.finances_type,
                R.layout.spinner_itemsize);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        financeTypeSpinner.setAdapter(adapter);
    }

    private void implementListeners(View view) {
        Button addButton = (Button) view.findViewById(R.id.finances_add_button);
        addButton.setOnClickListener(this);
        Button deleteButton = (Button) view.findViewById(R.id.finances_delete_button);
        deleteButton.setOnClickListener(this);
        EditText categoryEditText = (EditText) view.findViewById(R.id.finances_category);
        categoryEditText.setOnTouchListener(this);
        EditText dateEditText = (EditText) view.findViewById(R.id.finances_date);
        dateEditText.setOnTouchListener(this);
    }


    /**
     * This Interface gives report to attached Activity that the user created a Transaction
     */
    public interface MainContentFragmentInterface {
        /**
         * Reports the attached Activity to open the CategoryFragment
         *
         * @see CategoryFragment
         */
        void openCategoryFragment();

        /**
         * Reports the attached Activity to open the DatePicker
         *
         * @see DatePickerDialogFragment
         */
        void openDatePickerDialogFragment();

        /**
         * Reports the attached Activity that information is missing
         */
        void handleMissingInput();

        /**
         * Returns a List that represents the user input
         *
         * @param entry user input
         */
        void returnNewTransaction(List<String> entry);

        /**
         * Reports the attached Activity that the abort Button was pressed
         */
        void onAbort();
    }
}
