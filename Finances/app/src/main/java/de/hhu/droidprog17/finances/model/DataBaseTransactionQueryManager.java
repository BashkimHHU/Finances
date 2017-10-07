package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import de.hhu.droidprog17.finances.controller.ThreadObserverService;
import de.hhu.droidprog17.finances.view.TransactionsActivity;

/**
 * This Class manages database queries concerning all defined user transactions
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 */

public class DataBaseTransactionQueryManager extends AsyncTask<Void, Void, List<Transaction>> {


    private static final String THREAD = "DB-Query-Transactions";
    private DataBaseTransactionQueryManager.DbQueryManagerResponse mCallback;
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private Context mContext;
    private int mTransactionAmount;
    private boolean mPrivacy;

    /**
     * @param financesDbHelper reference to the database
     * @param context          calling context
     * @param callback         calling class
     * @param amount           number of transactions that should be fetched
     * @param privacy          status whether the incognito mode is enabled
     */
    public DataBaseTransactionQueryManager(FinancesContract.FinancesDbHelper financesDbHelper,
                                           Context context,
                                           DbQueryManagerResponse callback,
                                           int amount,
                                           boolean privacy) {

        mFinancesDbHelper = financesDbHelper;
        mContext = context;
        mCallback = callback;
        mTransactionAmount = amount;
        mPrivacy = privacy;
    }

    @Override
    protected List<Transaction> doInBackground(Void... params) {
        broadcastNewThread();
        return mFinancesDbHelper.getTransactionsFromDb(mTransactionAmount, mPrivacy);
    }

    @Override
    protected void onPostExecute(List<Transaction> transactions) {
        mCallback.returnResultSet(transactions);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface DbQueryManagerResponse {
        /**
         * Returns a list of all transactions found matching the constructors criteria
         *
         * @param transactions
         * @see Transaction
         */
        void returnResultSet(List<Transaction> transactions);
    }

    private void broadcastNewThread() {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }
}
