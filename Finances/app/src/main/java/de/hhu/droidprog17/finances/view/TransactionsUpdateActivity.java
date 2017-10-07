package de.hhu.droidprog17.finances.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.List;

import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.UserFeedbackService;
import de.hhu.droidprog17.finances.model.Transaction;

/**
 * This Activity enables the user to update Transactions stored in the database
 * This Activity is started for Result.
 * In case of RESULT_OK the caller expects a updated Transaction
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 */

public class TransactionsUpdateActivity extends AppCompatActivity implements
        MainContentFragment.MainContentFragmentInterface,
        CategoryFragment.CategoryFragmentInterface,
        DatePickerDialogFragment.DatePickerDialogFragmentInterface {

    private Transaction mTransaction;
    private UserFeedbackService mUserFeedbackService;
    private boolean mServiceBound;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_update_transactions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTransaction = getIntent().getParcelableExtra(TransactionsActivity.TRANSACTION_KEY);
        displayFragment(new MainContentFragment(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UserFeedbackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        transferTransactionData();
    }

    @Override
    protected void onStop() {
        if (mServiceBound) {
            unbindService(mConnection);
            mServiceBound = false;
        }
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openCategoryFragment() {
        displayFragment(new CategoryFragment(), true);
    }

    @Override
    public void openDatePickerDialogFragment() {
        new DatePickerDialogFragment().show(getFragmentManager(), "date_dialog");
    }

    @Override
    public void onDateSet(String date) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container_3);
        if (fragment instanceof MainContentFragment) {
            ((MainContentFragment) fragment).setDate(date);
        }
    }

    @Override
    public void returnCategory(String category) {
        getFragmentManager().popBackStackImmediate();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container_3);
        if (fragment instanceof MainContentFragment) {
            ((MainContentFragment) fragment).updateCategory(category);
        }
    }

    @Override
    public void handleMissingInput() {
        if (mServiceBound) {
            mUserFeedbackService.showToast(getResources().getString(R.string.toast_missing_input));
        }
    }

    @Override
    public void returnNewTransaction(List<String> entry) {
        mTransaction.setAmount(Double.parseDouble(entry.get(0)));
        mTransaction.setTitle(entry.get(1));
        mTransaction.setCategory(entry.get(2));
        mTransaction.setType(entry.get(3));
        mTransaction.setDate(entry.get(4));
        mTransaction.setAccount(entry.get(5));
        mTransaction.setInformation(entry.get(6));

        Intent resultIntent = new Intent();
        resultIntent.putExtra(TransactionsActivity.TRANSACTION_KEY, mTransaction);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onAbort() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void displayFragment(Fragment fragment, Boolean pushOnBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_3, fragment);
        if (pushOnBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void transferTransactionData() {
        String amount = Double.toString(mTransaction.getAmount());
        String title = mTransaction.getTitle();
        String category = mTransaction.getCategory();
        String type = mTransaction.getType();
        String date = mTransaction.getDate();
        String account = mTransaction.getAccount();
        String information = mTransaction.getInformation();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container_3);
        if (fragment instanceof MainContentFragment) {
            ((MainContentFragment) fragment)
                    .setText(amount, title, category, date, account, information, type);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UserFeedbackService.LocalBinder binder = (UserFeedbackService.LocalBinder) service;
            mUserFeedbackService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };
}
