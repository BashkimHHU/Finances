package de.hhu.droidprog17.finances.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.UserFeedbackService;
import de.hhu.droidprog17.finances.model.Account;
import de.hhu.droidprog17.finances.model.AccountBalanceAdapter;
import de.hhu.droidprog17.finances.model.AccountBalanceDataManager;

/**
 * This Activity displays all Account with regard to their current balance
 * Enables the User to delete Accounts
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class AccountBalanceActivity extends AppCompatActivity implements
        Button.OnClickListener,
        ListView.OnItemClickListener,
        AccountBalanceDataManager.AccountBalanceDataManagerInterface,
        DeleteDialogFragment.DeleteDialogInterface {

    private AccountBalanceDataManager mDataManager;
    private ListView mAccountListView;
    private AccountBalanceAdapter mAccountBalanceAdapter;
    private Account mAccountSelected;

    private UserFeedbackService mUserFeedbackService;
    private boolean mServiceBound;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_accountbalance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeActionButtons();
        mDataManager = new AccountBalanceDataManager(this, this);

        mAccountListView = (ListView) findViewById(R.id.balance_listview);
        registerForContextMenu(mAccountListView);
        mAccountListView.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UserFeedbackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mDataManager.startQuery();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onDestroy() {
        mDataManager.getFinancesDbHelper().close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.balance_delete_button) {
            new DeleteDialogFragment().show(getFragmentManager(), "del_dialog");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAccountSelected = mAccountBalanceAdapter.getItem(position);
        view.setSelected(true);
        activateDeleteButton();
    }

    @Override
    public void onQueryFinished() {
        mAccountBalanceAdapter = new AccountBalanceAdapter(this, mDataManager.getQueryResult());
        mAccountListView.setAdapter(mAccountBalanceAdapter);
    }

    @Override
    public void onDeleteFinished(int status) {
        if (status == 1) {
            mAccountBalanceAdapter.notifyDataSetChanged();
            disableDeleteButton();
            mUserFeedbackService.showToast(
                    getResources().getString(R.string.toast_deletion_acc_successfully));
        } else {
            // should never be reached, since we use the Primary-Key of the database for deletion
        }
    }

    @Override
    public void onDeletionConfirmed() {
        mDataManager.deleteAccount(mAccountSelected);
    }

    private void initializeActionButtons() {
        findViewById(R.id.balance_delete_button).setOnClickListener(this);
        disableDeleteButton();
    }

    private void disableDeleteButton() {
        findViewById(R.id.balance_delete_button).setClickable(false);
    }

    private void activateDeleteButton() {
        findViewById(R.id.balance_delete_button).setClickable(true);
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
