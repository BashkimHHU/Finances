package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import de.hhu.droidprog17.finances.view.AccountBalanceActivity;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;

/**
 * This Class manages database queries concerning all defined user accounts
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see AccountBalanceActivity
 */

public class DataBaseAccountQueryManager extends AsyncTask<Void, Void, List<Account>> {

    private DataBaseAccountQueryManager.DbAccountQueryManagerResponse mCallback;

    private static final String THREAD = "DB-Query-Account";
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private Context mContext;

    /**
     * @param financesDbHelper reference to the database
     * @param context          calling context
     * @param callback         calling class
     */
    public DataBaseAccountQueryManager(FinancesContract.FinancesDbHelper financesDbHelper,
                                       Context context, DbAccountQueryManagerResponse callback) {

        mFinancesDbHelper = financesDbHelper;
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected List<Account> doInBackground(Void... params) {
        broadcastNewThread();
        return mFinancesDbHelper.getAccountsFromDb();
    }

    @Override
    protected void onPostExecute(List<Account> accounts) {
        mCallback.returnResultSetAccount(accounts);
    }

    /**
     * Since database operations are fully async, this Interface must be implemented by the
     * creating Class to provide the possibility to be informed if desired operations are finished
     */
    public interface DbAccountQueryManagerResponse {
        /**
         * Returns a list of all accounts found in the database
         *
         * @param accounts accounts stored in database
         * @see Account
         */
        void returnResultSetAccount(List<Account> accounts);
    }

    private void broadcastNewThread() {
        Intent threadIntent = new Intent(ThreadObserverService.SERVICE_BROADCAST_RECEIVER_ACTION);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(threadIntent);
    }
}
