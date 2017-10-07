package de.hhu.droidprog17.finances.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.controller.ThreadObserverService;
import de.hhu.droidprog17.finances.controller.UserFeedbackService;
import de.hhu.droidprog17.finances.model.DataBaseInsertionManager;
import de.hhu.droidprog17.finances.model.FinancesContract;

/**
 * This Activity represents the launching Activity of the application
 * It provides a NavigationBar and enables the user to add Transactions and Accounts
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.CategoryFragmentInterface,
        DatePickerDialogFragment.DatePickerDialogFragmentInterface,
        MainContentFragment.MainContentFragmentInterface,
        SharedPreferences.OnSharedPreferenceChangeListener,
        DataBaseInsertionManager.DbInsertionManagerResponse,
        CreateAccountFragment.CreateAccountInterface {

    private static final String TAG = "Main_Activity";
    private static final String THREAD = "Main-Thread";

    private SharedPreferences mSharedPreferences;
    private UserFeedbackService mUserFeedbackService;
    private boolean mServiceBound;
    private FinancesContract.FinancesDbHelper mFinancesDbHelper;
    private boolean mIncognito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startThreadObserverService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nav_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNav,
                R.string.closeNav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mFinancesDbHelper = new FinancesContract.FinancesDbHelper(this);
        displayFragment(new MainContentFragment(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UserFeedbackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
        stopThreadObserverService();
        stopSharedPreferenceChangeListener();
        mFinancesDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add_transaction:
                displayFragment(new MainContentFragment(), true);
                break;
            case R.id.nav_add_account:
                displayFragment(new CreateAccountFragment(), true);
                break;
            case R.id.nav_transaction_status:
                Intent startTransactionsIntent = new Intent(this, TransactionsActivity.class);
                startActivity(startTransactionsIntent);
                break;
            case R.id.nav_accountbalance:
                Intent startAccountBalanceIntent = new Intent(this, AccountBalanceActivity.class);
                startActivity(startAccountBalanceIntent);
                break;
            case R.id.nav_settings:
                displayFragment(new SettingsFragment(), true);
                break;
            default:
                break;
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nav_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        startSharedPreferenceChangeListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_settings) {
            displayFragment(new SettingsFragment(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void returnCategory(String category) {
        getFragmentManager().popBackStackImmediate();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof MainContentFragment) {
            ((MainContentFragment) fragment).updateCategory(category);
        }
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
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof MainContentFragment) {
            ((MainContentFragment) fragment).setDate(date);
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
        entry.add(Boolean.toString(mIncognito));
        new DataBaseInsertionManager(mFinancesDbHelper, this, entry, this, "transactions")
                .execute();
    }

    @Override
    public void onAbort() {
        // In case of abort, do nothing. Fragment is doing the job itself.
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsFragment.USERNAME_KEY)) {
            updateNavigationUsername(sharedPreferences
                    .getString(key, getResources().getString(R.string.nav_username_default)));
        } else if (key.equals(SettingsFragment.INCOGNITO_KEY)) {
            mIncognito = sharedPreferences.getBoolean(key, false);
            if (mIncognito) {
                mUserFeedbackService.showNotification(
                        getResources().getString(R.string.notification_incognito_title),
                        getResources().getString(R.string.notification_incognito_text)
                );
            } else {
                mUserFeedbackService.cancelNotification();
            }
        }
    }

    @Override
    public void returnInsertionStatus(Long result) {
        Log.d(TAG, Long.toString(result));
        if (result != -1) {
            mUserFeedbackService.showToast(getResources()
                    .getString(R.string.toast_insert_successfully));
        } else {
            mUserFeedbackService.showToast(getResources().getString(R.string.toast_insert_failed));
        }
    }

    @Override
    public void returnNewAccount(List<String> entry) {
        new DataBaseInsertionManager(mFinancesDbHelper, this, entry, this, "accounts").execute();
    }

    @Override
    public void returnMissingInput(String message) {
        mUserFeedbackService.showToast(message);
    }

    private void displayFragment(Fragment fragment, Boolean pushOnBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (pushOnBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void startSharedPreferenceChangeListener() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Since Service responsible is still starting, this exception is necessary.
                Log.i(TAG + "(T)", "THREAD: SharedPreferences Access");
                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                mSharedPreferences.registerOnSharedPreferenceChangeListener(MainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                determineUserSettings();
            }

        }.execute();
    }

    private void stopSharedPreferenceChangeListener() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(MainActivity.this);
    }

    private void determineUserSettings() {
        updateNavigationUsername(
                mSharedPreferences.getString(
                        SettingsFragment.USERNAME_KEY,
                        getResources().getString(R.string.nav_username_default))
        );

        mIncognito = mSharedPreferences.getBoolean(SettingsFragment.INCOGNITO_KEY, false);
        if (mIncognito) {
            mUserFeedbackService.showNotification(
                    getResources().getString(R.string.notification_incognito_title),
                    getResources().getString(R.string.notification_incognito_text)
            );
        }
    }

    private void updateNavigationUsername(String username) {
        TextView usernameTextView = (TextView) findViewById(R.id.nav_username);
        usernameTextView.setText(username);
    }

    private void startThreadObserverService() {
        Intent threadIntent = new Intent(this, ThreadObserverService.class);
        threadIntent.putExtra(ThreadObserverService.THREAD_NAME_EXTRA_KEY, THREAD);
        startService(threadIntent);
    }

    private void stopThreadObserverService() {
        stopService(new Intent(this, ThreadObserverService.class));
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

    //_________________________________ For Integration Testing __________________________________//

    /**
     * Deletes all Accounts and Transactions populating the data base.
     * This method was created to restore data base before testing.
     */
    public void clearDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mFinancesDbHelper.deleteDb();
                return null;
            }
        }.execute();

    }

    /**
     * Set the Incognito-State to a desired value.
     * This method was created to restore the Incognito-State after testing.
     *
     * @param value Incognito value
     */
    public void setIncognitoMode(final boolean value) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG + "(T)", "THREAD: SharedPreferences Edit");
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(SettingsFragment.INCOGNITO_KEY, value);
                editor.commit();
                return null;
            }
        }.execute();
    }

    //____________________________________________________________________________________________//
}
