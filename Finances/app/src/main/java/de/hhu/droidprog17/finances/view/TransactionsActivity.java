package de.hhu.droidprog17.finances.view;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import de.hhu.droidprog17.finances.model.Transaction;
import de.hhu.droidprog17.finances.model.TransactionsAdapter;
import de.hhu.droidprog17.finances.model.TransactionsDataManager;
import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.BatteryStatusReceiver;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;
import de.hhu.droidprog17.finances.controller.UserFeedbackService;


/**
 * This Activity list all Transactions made.
 * It provides several settings according the amount of transactions displayed,
 * a search function and multiple ways to manipulate the data displayed
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class TransactionsActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        ListView.OnItemClickListener,
        SearchView.OnCloseListener,
        Button.OnClickListener,
        TransactionsDataManager.TransactionsDataManagerInterface,
        DeleteDialogFragment.DeleteDialogInterface {

    private static final int REQUEST_CODE = 2;
    public static final String TRANSACTION_KEY = "transaction_key";

    private UserFeedbackService mUserFeedbackService;
    private boolean mServiceBound;

    private SearchView mSearchView;
    private ListView mTransactionsListView;
    private TransactionsAdapter mTransactionsAdapter;
    private Transaction mTransactionSelected;
    private TransactionsDataManager mDataManager;
    private boolean mIsDeleting;
    private boolean mIsUpdating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTransactionsListView = (ListView) findViewById(R.id.transactions_listview);
        registerForContextMenu(mTransactionsListView);
        mTransactionsListView.setOnItemClickListener(this);

        mDataManager = new TransactionsDataManager(this, this);
        mDataManager.setQueryResultSize(7);
        getPrivacyStatus();
        initializeActionButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UserFeedbackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transactions_menu, menu);
        initializeSearchAction(menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.transactions_menuItem_7:
                item.setChecked(true);
                mDataManager.setQueryResultSize(7);
                break;
            case R.id.transactions_menuItem_14:
                item.setChecked(true);
                mDataManager.setQueryResultSize(14);
                break;
            case R.id.transactions_menuItem_30:
                item.setChecked(true);
                mDataManager.setQueryResultSize(30);
                break;
            case R.id.transactions_menuItem_any:
                item.setChecked(true);
                mDataManager.setQueryResultSize(-1);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        requestTransactions();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mDataManager.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ((!BatteryStatusReceiver.isBatteryLevelLow()) && (!mIsDeleting) && (!mIsUpdating)) {
            mDataManager.filter(newText);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTransactionSelected = mTransactionsAdapter.getItem(position);
        view.setSelected(true);
        mTransactionsListView.setItemChecked(position, true);
        activateActionButtons();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactions_inspect_button:
                startUpdateActivity(mTransactionSelected);
                break;
            case R.id.transactions_delete_button:
                new DeleteDialogFragment().show(getFragmentManager(), "del_dialog");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onClose() {
        if ((!mIsDeleting) && (!mIsUpdating)) {
            mDataManager.restoreFilter();
        }
        return false;
    }

    @Override
    public void onQueryFinished() {
        mTransactionsAdapter = new TransactionsAdapter(this, mDataManager.getQueryResults());
        mTransactionsListView.setAdapter(mTransactionsAdapter);
        showCashFlow(mDataManager.getAmountSpend(), mDataManager.getAmountEarned());
    }

    @Override
    public void onUpdateFinished(int status) {
        if (status == 1) {
            mUserFeedbackService.showToast(getResources()
                    .getString(R.string.toast_update_successfully));
            closeSearchView();
            deactivateActionButtons();
            requestTransactions();
            mIsUpdating = false;
        }
    }

    @Override
    public void onDeleteFinished(int status) {
        if (status == 1) {
            mIsDeleting = false;
            StringBuilder toastMessage = new StringBuilder();
            toastMessage.append(mTransactionSelected.getTitle());
            toastMessage.append(" : ");
            toastMessage.append(getResources().getString(R.string.toast_deletion_successfully));
            mUserFeedbackService.showToast(toastMessage.toString());
            requestTransactions();
        } else {
            // should never be reached, since we use the Primary-Key of the database for updates
        }
    }

    @Override
    public void onDeletionConfirmed() {
        mIsDeleting = true;
        closeSearchView();
        deactivateActionButtons();
        deleteTransaction(mTransactionSelected);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Transaction transaction = data.getParcelableExtra(TRANSACTION_KEY);
            mIsUpdating = true;
            mDataManager.updateTransaction(transaction);
        }
    }

    private void initializeSearchAction(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.transactions_menuItem_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setQueryHint(getResources().getString(R.string.transactions_search_hint));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    private void getPrivacyStatus() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                Intent threadIntent =
                        new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
                threadIntent.putExtra(ThreadObserverService
                        .THREAD_NAME_EXTRA_KEY, "SharedPreferences Access");
                LocalBroadcastManager
                        .getInstance(TransactionsActivity.this).sendBroadcast(threadIntent);

                SharedPreferences defaultPref = PreferenceManager
                        .getDefaultSharedPreferences(TransactionsActivity.this);
                mDataManager.setIncognito(defaultPref.
                        getBoolean(SettingsFragment.INCOGNITO_KEY, false));
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                requestTransactions();
            }
        }.execute();
    }

    private void requestTransactions() {
        mDataManager.startQuery();
    }

    private void deleteTransaction(Transaction transaction) {
        mDataManager.deleteTransaction(transaction);
    }

    private void showCashFlow(Double spend, Double earned) {
        TextView totalSpendTextView = (TextView) findViewById(R.id.transactions_spend_total);
        TextView totalEarnedTextView = (TextView) findViewById(R.id.transactions_earned_total);
        totalSpendTextView.setText(Double.toString(spend));
        totalEarnedTextView.setText(Double.toString(earned));
    }

    private void initializeActionButtons() {
        findViewById(R.id.transactions_delete_button).setOnClickListener(this);
        findViewById(R.id.transactions_inspect_button).setOnClickListener(this);
        deactivateActionButtons();
    }

    private void activateActionButtons() {
        findViewById(R.id.transactions_delete_button).setClickable(true);
        findViewById(R.id.transactions_inspect_button).setClickable(true);
    }

    private void deactivateActionButtons() {
        findViewById(R.id.transactions_delete_button).setClickable(false);
        findViewById(R.id.transactions_inspect_button).setClickable(false);
    }

    private void closeSearchView() {
        mSearchView.setIconified(true);
        mSearchView.onActionViewCollapsed();
    }

    private void startUpdateActivity(Transaction transaction) {

        Intent startUpdateTransactionIntent = new Intent(this, TransactionsUpdateActivity.class);
        startUpdateTransactionIntent.putExtra(TRANSACTION_KEY, transaction);
        startActivityForResult(startUpdateTransactionIntent, REQUEST_CODE);
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
