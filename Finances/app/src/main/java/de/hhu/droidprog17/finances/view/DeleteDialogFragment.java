package de.hhu.droidprog17.finances.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import de.hhu.droidprog17.finances.R;

/**
 * This Fragment provides a Dialog to determine whether a Transaction/Account should be removed
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 * @see AccountBalanceActivity
 */

public class DeleteDialogFragment extends DialogFragment {

    private static final String TAG = "DeleteDialogFragment";
    private DeleteDialogInterface mCallback;
    private boolean isAttachedOnAccount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttachedOnAccount = context instanceof AccountBalanceActivity;
        if (Build.VERSION.SDK_INT > 22) {
            try {
                mCallback = (DeleteDialogFragment.DeleteDialogInterface) context;
            } catch (ClassCastException e) {
                Log.e(TAG, context.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isAttachedOnAccount = activity instanceof AccountBalanceActivity;
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (DeleteDialogFragment.DeleteDialogInterface) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, activity.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String dialogMessage;

        if (isAttachedOnAccount) {
            dialogMessage = getResources().getString(R.string.dialog_delete_account);
        } else {
            dialogMessage = getResources().getString(R.string.dialog_delete);
        }

        builder.setMessage(dialogMessage)
                .setPositiveButton(getResources().getString(R.string.date_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallback.onDeletionConfirmed();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.date_abort_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing to do here, just close Dialog.
                            }
                        });
        return builder.create();
    }

    /**
     * This Interface gives report to attached Activity that the user really wants
     * to delete the selected item
     */
    public interface DeleteDialogInterface {
        /**
         * This method is called, when the user presses the "OK" Button
         */
        void onDeletionConfirmed();
    }
}
