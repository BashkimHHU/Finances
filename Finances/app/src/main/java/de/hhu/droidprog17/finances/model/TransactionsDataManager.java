package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;
import de.hhu.droidprog17.finances.view.TransactionsActivity;

/**
 * This Class manages each operation on the database concerning transactions.
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 */

public class TransactionsDataManager implements
        DataBaseTransactionQueryManager.DbQueryManagerResponse,
        DataBaseDeletionManager.DbDeletionManagerResponse,
        DataBaseUpdateManager.DbUpdateManagerResponse {

    private Context mContext;
    private TransactionsDataManagerInterface mCallback;
    private List<Transaction> mQueryResults;
    private List<Transaction> mQueryResultsFiltered;
    private List<Transaction> mCurrentResultSet;
    private int mQueryResultSize;
    private boolean mPrivacyStatus;
    private Double mTotalSpend;
    private Double mTotalEarned;
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;

    /**
     * @param context  Calling context
     * @param callback Calling class
     */
    public TransactionsDataManager(Context context, TransactionsDataManagerInterface callback) {
        mContext = context;
        mCallback = callback;
        mFinancesDbHelper = new FinancesContract.FinancesDbHelper(mContext);
    }

    /**
     * Define result size of future database queries
     *
     * @param size limit of entries returned
     */
    public void setQueryResultSize(int size) {
        mQueryResultSize = size;
    }

    /**
     * Define whether the Incognito-Mode is enabled or not
     *
     * @param privacy true if enabled, false otherwise
     */
    public void setIncognito(boolean privacy) {
        mPrivacyStatus = privacy;
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
     * Returns the total amount of money spend by all Transactions displayed
     *
     * @return total amount spend
     */
    public Double getAmountSpend() {
        return mTotalSpend;
    }

    /**
     * Returns the total amount of money earned by all Transactions displayed
     *
     * @return total amount earned
     */
    public Double getAmountEarned() {
        return mTotalEarned;
    }

    /**
     * Returns a list of all Transactions
     *
     * @return List of Transactions
     */
    public List<Transaction> getQueryResults() {
        return mCurrentResultSet;
    }

    /**
     * Start database query to fetch Transactions saved
     *
     * @see Transaction
     * @see DataBaseTransactionQueryManager
     */
    public void startQuery() {
        new DataBaseTransactionQueryManager(
                mFinancesDbHelper,
                mContext,
                this,
                mQueryResultSize,
                mPrivacyStatus).execute();
    }

    /**
     * Delete Transaction from database
     *
     * @param transaction Transaction that should be removed
     * @see Transaction
     */
    public void deleteTransaction(Transaction transaction) {
        new DataBaseDeletionManager(mFinancesDbHelper,
                mContext,
                this,
                transaction.getID(),
                "transactions")
                .execute();
    }

    /**
     * Update a Transaction saved in the database
     *
     * @param transaction Updated Transaction
     * @see Transaction
     */
    public void updateTransaction(Transaction transaction) {
        new DataBaseUpdateManager(mFinancesDbHelper, mContext, this, transaction)
                .execute();
    }

    @Override
    public void returnResultSet(List<Transaction> transactions) {
        mQueryResults = transactions;
        calculateTotals(mQueryResults);
    }

    @Override
    public void returnDeletionStatus(int status) {
        mCallback.onDeleteFinished(status);
    }

    @Override
    public void returnUpdateStatus(int status) {
        mCallback.onUpdateFinished(status);
    }

    /**
     * Filter Transactions received by the last query for a specific pattern
     *
     * @param pattern pattern that is searched in each Transaction
     * @see Transaction
     */
    public void filter(final String pattern) {
        new AsyncTask<Void, Void, List<Transaction>>() {
            @Override
            protected List<Transaction> doInBackground(Void... params) {
                broadcastNewThread("Filter Result-Set");
                List<Transaction> result = new ArrayList<>();
                for (Transaction transaction : mQueryResults) {
                    if (transaction.containsPattern(pattern)) {
                        result.add(transaction);
                    }
                }
                return result;
            }

            protected void onPostExecute(List<Transaction> result) {
                mQueryResultsFiltered = result;
                calculateTotals(mQueryResultsFiltered);
            }
        }.execute();
    }

    /**
     * Restore all current filters by selecting the origin result List
     */
    public void restoreFilter() {
        calculateTotals(mQueryResults);
    }

    private void calculateTotals(final List<Transaction> transactions) {
        mCurrentResultSet = transactions;
        mTotalEarned = 0.0;
        mTotalSpend = 0.0;
        new AsyncTask<Void, Void, List<Double>>() {
            @Override
            protected List<Double> doInBackground(Void... params) {
                broadcastNewThread("CashFlow Calculation");
                return calculation(transactions,
                        mContext.getResources().getString(R.string.type_spend));
            }

            @Override
            protected void onPostExecute(List<Double> result) {
                mTotalEarned = roundValueOnTwoDecimals(result.get(0));
                mTotalSpend = roundValueOnTwoDecimals(result.get(1));
                respondToRequest();
            }
        }.execute();
    }

    /**
     * Returns the total amount spend and earned by the transactions displayed
     *
     * @param transactions transactions displayed
     * @param typeSpend    String representation of spend
     * @return List where the 1 entry is the amount total spend and the 2 the amount total earned
     */
    public List<Double> calculation(List<Transaction> transactions, String typeSpend) {
        java.lang.Double spend = 0.0;
        java.lang.Double earned = 0.0;
        for (Transaction transaction : transactions) {
            String type = transaction.getType();
            if (type.equals(typeSpend)) {
                spend = spend - transaction.getAmount();
            } else {
                earned = earned + transaction.getAmount();
            }
        }
        List<java.lang.Double> result = new ArrayList<>();
        result.add(earned);
        result.add(spend);
        return result;
    }

    private Double roundValueOnTwoDecimals(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void respondToRequest() {
        mCallback.onQueryFinished();
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
    public interface TransactionsDataManagerInterface {

        /**
         * Reports the calling class, that the query was finished and data is available
         */
        void onQueryFinished();

        /**
         * Reports the calling class, that the update was finished
         *
         * @param status number of entries updated
         */
        void onUpdateFinished(int status);

        /**
         * Reports the calling class about the deletions status
         *
         * @param status number of entries deleted
         */
        void onDeleteFinished(int status);
    }
}
