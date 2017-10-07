package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import de.hhu.droidprog17.finances.controller.ThreadObserverService;

/**
 * This Async-Task performs any type of update operation on the database
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsDataManager
 */

public class DataBaseUpdateManager extends AsyncTask<Void, Void, Integer> {

    private static final String THREAD = "DB-Update";
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private DataBaseUpdateManager.DbUpdateManagerResponse mCallback;
    private Context mContext;
    private Transaction mTransaction;

    /**
     * @param financesDbHelper reference to the database
     * @param context          calling context
     * @param callback         calling class
     * @param transaction      updated transaction
     */
    public DataBaseUpdateManager(FinancesContract.FinancesDbHelper financesDbHelper,
                                 Context context,
                                 DataBaseUpdateManager.DbUpdateManagerResponse callback,
                                 Transaction transaction) {

        mFinancesDbHelper = financesDbHelper;
        mContext = context;
        mCallback = callback;
        mTransaction = transaction;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        broadcastNewThread();
        return mFinancesDbHelper.updateTransaction(mTransaction);
    }

    @Override
    protected void onPostExecute(Integer result) {
        mCallback.returnUpdateStatus(result);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface DbUpdateManagerResponse {
        /**
         * Returns the number of entries that were updated
         *
         * @param status number of updated database entries
         */
        void returnUpdateStatus(int status);
    }

    private void broadcastNewThread() {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }
}
