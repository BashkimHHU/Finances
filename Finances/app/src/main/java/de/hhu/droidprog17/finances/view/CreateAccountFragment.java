package de.hhu.droidprog17.finances.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.R;

/**
 * This Fragment provides the functionality to create a account
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see MainActivity
 */

public class CreateAccountFragment extends Fragment implements
        View.OnClickListener {

    private static final String TAG = "Account_Frag";
    private View mView;
    private CreateAccountInterface mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.create_account_fragment, container, false);
        initialWork();
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT > 22) {
            try {
                mCallback = (CreateAccountFragment.CreateAccountInterface) context;
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
                mCallback = (CreateAccountFragment.CreateAccountInterface) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, activity.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_account_add_button) {
            String accName = ((EditText) getActivity()
                    .findViewById(R.id.create_account_name)).getText().toString().trim();

            String balance = ((EditText) getActivity()
                    .findViewById(R.id.create_account_balance)).getText().toString().trim();

            if (!accName.equals("")) {
                if (!balance.equals("")) {
                    List<String> values = new ArrayList<>();
                    values.add(accName);
                    values.add(balance);
                    mCallback.returnNewAccount(values);
                } else {
                    mCallback.returnMissingInput(
                            getResources().getString(R.string.toast_acc_balance));
                }
            } else {
                mCallback.returnMissingInput(
                        getResources().getString(R.string.toast_acc_name));
            }
        }
    }

    private void initialWork() {
        implementListeners();
    }

    private void implementListeners() {
        Button addButton = (Button) mView.findViewById(R.id.create_account_add_button);
        addButton.setOnClickListener(this);
    }

    /**
     * This Interface gives report to attached Activity that the User submitted an input
     */
    public interface CreateAccountInterface {
        /**
         * Returns a List that represents the user input
         *
         * @param values user input
         */
        void returnNewAccount(List<String> values);

        /**
         * Returns a information message according the missing input
         *
         * @param message user feedback message
         */
        void returnMissingInput(String message);
    }
}
