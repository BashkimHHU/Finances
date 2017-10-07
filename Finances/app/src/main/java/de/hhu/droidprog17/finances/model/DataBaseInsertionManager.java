package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import de.hhu.droidprog17.finances.view.MainActivity;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;

/**
 * This Async-Task performs any type insertion into the database
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see MainActivity
 */

public class DataBaseInsertionManager extends AsyncTask<Void, Void, Long> {


    private static final String THREAD = "DB-Insertion-Thread";

    private DbInsertionManagerResponse mCallback;
    private List<String> mEntry;
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private Context mContext;
    private String mTable;

    /**
     * @param financesDbHelper Reference to the database
     * @param context          calling context
     * @param entry            information that should be inserted
     * @param callback         calling class
     * @param table            table to insert data
     */
    public DataBaseInsertionManager(FinancesContract.FinancesDbHelper financesDbHelper,
                                    Context context,
                                    List<String> entry,
                                    DbInsertionManagerResponse callback,
                                    String table) {

        mFinancesDbHelper = financesDbHelper;
        mContext = context;
        mEntry = entry;
        mCallback = callback;
        mTable = table;
    }

    @Override
    protected Long doInBackground(Void... params) {
        broadcastNewThread();
        long status;
        if (mTable.equals("accounts")) {
            status = mFinancesDbHelper.addAccountToDb(mEntry);
        } else {
            status = mFinancesDbHelper.addTransactionToDb(mEntry);
        }
        return status;
    }

    @Override
    protected void onPostExecute(Long result) {
        mCallback.returnInsertionStatus(result);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface DbInsertionManagerResponse {
        /**
         * Returns the ID of the newly created database entry
         *
         * @param result ID if insert was successful, -1 otherwise
         */
        void returnInsertionStatus(Long result);
    }

    private void broadcastNewThread() {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }
}
