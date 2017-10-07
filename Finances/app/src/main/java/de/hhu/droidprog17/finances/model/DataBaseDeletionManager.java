package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import de.hhu.droidprog17.finances.controller.ThreadObserverService;

/**
 * This Async-Task performs any type of deletion on the database
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see AccountBalanceDataManager
 * @see TransactionsDataManager
 */

public class DataBaseDeletionManager extends AsyncTask<Void, Void, Integer> {

    private static final String THREAD = "DB-Deletion";
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private DataBaseDeletionManager.DbDeletionManagerResponse mCallback;
    private Context mContext;
    private Long mTransactionID;
    private String mTable;

    /**
     * @param financesDbHelper reference to the database
     * @param context          calling Context
     * @param callback         calling Class
     * @param id               entry id that should be removed
     * @param table            table to look for the entry
     */
    public DataBaseDeletionManager(FinancesContract.FinancesDbHelper financesDbHelper,
                                   Context context,
                                   DataBaseDeletionManager.DbDeletionManagerResponse callback,
                                   long id,
                                   String table) {

        mFinancesDbHelper = financesDbHelper;
        mContext = context;
        mCallback = callback;
        mTransactionID = id;
        mTable = table;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        broadcastNewThread();
        if (mTable.equals("transactions")) {
            return mFinancesDbHelper.deleteTransactionFromDb(mTransactionID);
        } else if (mTable.equals("accounts")) {
            return mFinancesDbHelper.deleteAccountFromDb(mTransactionID);
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        mCallback.returnDeletionStatus(result);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface DbDeletionManagerResponse {
        /**
         * Reports the calling class about the deletion status
         *
         * @param status number of entries deleted
         */
        void returnDeletionStatus(int status);
    }

    private void broadcastNewThread() {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }
}
