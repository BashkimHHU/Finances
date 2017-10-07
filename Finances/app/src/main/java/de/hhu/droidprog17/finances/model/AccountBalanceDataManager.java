package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import de.hhu.droidprog17.finances.view.AccountBalanceActivity;
import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;

/**
 * This Class manages each operation on the database concerning accounts.
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see AccountBalanceActivity
 */

public class AccountBalanceDataManager implements
        DataBaseDeletionManager.DbDeletionManagerResponse,
        DataBaseAccountQueryManager.DbAccountQueryManagerResponse,
        DataBaseTransactionQueryManager.DbQueryManagerResponse {

    private Context mContext;
    private AccountBalanceDataManagerInterface mCallback;
    private List<Account> mQueryResult;
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;

    /**
     * @param context  Calling Context
     * @param callback Calling Class implementing the interface
     */
    public AccountBalanceDataManager(Context context, AccountBalanceDataManagerInterface callback) {
        mContext = context;
        mCallback = callback;
        mFinancesDbHelper = new FinancesContract.FinancesDbHelper(mContext);
    }

    /**
     * Returns a reference to the class enabling the database access
     *
     * @return reference to the database helper
     */
    public FinancesContract.FinancesDbHelper getFinancesDbHelper() {
        return mFinancesDbHelper;
    }

    /**
     * Returns a List of Accounts that were found in the last database query
     *
     * @return list of Accounts stored in the database
     * @see Account
     */
    public List<Account> getQueryResult() {
        return mQueryResult;
    }

    /**
     * Start a query fetching all Accounts available in the database
     *
     * @see Account
     * @see DataBaseAccountQueryManager
     */
    public void startQuery() {
        new DataBaseAccountQueryManager(mFinancesDbHelper, mContext, this).execute();
    }

    /**
     * Delete a Account from the database
     *
     * @param account Account that should be removed from the database
     * @see Account
     */
    public void deleteAccount(final Account account) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                broadcastNewThread("AccountBalanceAdapter DataSet Account Deletion");
                deleteAccountFromList(account);
                return null;
            }
        }.execute();

        new DataBaseDeletionManager(mFinancesDbHelper, mContext, this, account.getID(), "accounts")
                .execute();
    }

    @Override
    public void returnDeletionStatus(int status) {
        mCallback.onDeleteFinished(status);
    }

    @Override
    public void returnResultSetAccount(List<Account> accounts) {
        mQueryResult = accounts;
        // -1 means all Transactions in the DataBase
        new DataBaseTransactionQueryManager(mFinancesDbHelper, mContext, this, -1, false).execute();
    }

    @Override
    public void returnResultSet(final List<Transaction> transactions) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                broadcastNewThread("Account-Balance-Calculation");
                calculateAccountBalance(transactions);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mCallback.onQueryFinished();
            }
        }.execute();
    }

    private void calculateAccountBalance(List<Transaction> transactions) {
        for (Account account : mQueryResult) {
            String currentAccount = account.getAccountName().toLowerCase().replaceAll(" ", "");
            for (Transaction transaction : transactions) {
                String transactionAccount = transaction.getAccount()
                        .toLowerCase().replaceAll(" ", "");

                if (transactionAccount.equals(currentAccount)) {
                    if (transaction.getType()
                            .equals(mContext.getResources().getString(R.string.type_spend))) {

                        account.withdraw(transaction.getAmount());

                    } else if (transaction.getType()
                            .equals(mContext.getResources().getString(R.string.type_earned))) {

                        account.deposit(transaction.getAmount());
                    }
                }
            }
            account.roundBalance();
        }
    }

    private void deleteAccountFromList(Account account) {
        for (int i = 0; i < mQueryResult.size(); i++) {
            if (mQueryResult.get(i).getID().equals(account.getID())) {
                mQueryResult.remove(i);
            }
        }
    }

    private void broadcastNewThread(String threadName) {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, threadName);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface AccountBalanceDataManagerInterface {

        /**
         * Reports the calling class, that the query was finished and data is available
         */
        void onQueryFinished();

        /**
         * Reports the calling class about the deletions status
         *
         * @param status number of entries deleted
         */
        void onDeleteFinished(int status);
    }
}
