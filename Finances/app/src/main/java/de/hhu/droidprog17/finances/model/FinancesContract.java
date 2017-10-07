package de.hhu.droidprog17.finances.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class describes the database structure and operations that can be performed on it
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see DataBaseTransactionQueryManager
 * @see DataBaseAccountQueryManager
 * @see DataBaseInsertionManager
 * @see DataBaseUpdateManager
 * @see DataBaseDeletionManager
 */

public class FinancesContract {

    private FinancesContract() {
    }

    private static final String TEXT = " TEXT";
    private static final String REAL = " REAL";
    private static final String INT = " INTEGER";

    public static class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_INFORMATION = "information";
        public static final String COLUMN_NAME_INCOGNITO = "incognito";
    }

    public static class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_BALANCE = "balance";
    }

    public static final String SQL_CREATE_ENTRIES_TRANSACTION =
            "CREATE TABLE " + TransactionEntry.TABLE_NAME + " (" +
                    TransactionEntry._ID + INT + " PRIMARY KEY," +
                    TransactionEntry.COLUMN_NAME_AMOUNT + REAL + "," +
                    TransactionEntry.COLUMN_NAME_TITLE + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_CATEGORY + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_TYPE + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_DATE + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_ACCOUNT + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_INFORMATION + TEXT + "," +
                    TransactionEntry.COLUMN_NAME_INCOGNITO + INT + ")";

    public static final String SQL_CREATE_ENTRIES_ACCOUNT =
            "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
                    AccountEntry._ID + " INTEGER PRIMARY KEY," +
                    AccountEntry.COLUMN_NAME_ACCOUNT + TEXT + "," +
                    AccountEntry.COLUMN_NAME_BALANCE + REAL + ")";

    public static final String SQL_DELETE_ENTRIES_TRANSACTION =
            "DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES_ACCOUNT =
            "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;


    public static class FinancesDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Finances.db";

        public FinancesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES_TRANSACTION);
            db.execSQL(SQL_CREATE_ENTRIES_ACCOUNT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Database is final for now. On extending this will be implemented
        }

        /**
         * Remove all entries saved in the database
         */
        public void deleteDb() {
            SQLiteDatabase database = getWritableDatabase();
            database.execSQL(SQL_DELETE_ENTRIES_TRANSACTION);
            database.execSQL(SQL_CREATE_ENTRIES_TRANSACTION);
            database.execSQL(SQL_DELETE_ENTRIES_ACCOUNT);
            database.execSQL(SQL_CREATE_ENTRIES_ACCOUNT);
        }

        /**
         * Add transaction details to the database
         *
         * @param transactionDetails list of transaction details
         * @return ID internally given to the new entry by the database
         */
        public long addTransactionToDb(List<String> transactionDetails) {
            SQLiteDatabase database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TransactionEntry.COLUMN_NAME_AMOUNT, transactionDetails.get(0));
            values.put(TransactionEntry.COLUMN_NAME_TITLE, transactionDetails.get(1));
            values.put(TransactionEntry.COLUMN_NAME_CATEGORY, transactionDetails.get(2));
            values.put(TransactionEntry.COLUMN_NAME_TYPE, transactionDetails.get(3));
            values.put(TransactionEntry.COLUMN_NAME_DATE, transactionDetails.get(4));
            values.put(TransactionEntry.COLUMN_NAME_ACCOUNT, transactionDetails.get(5));
            values.put(TransactionEntry.COLUMN_NAME_INFORMATION, transactionDetails.get(6));

            if (transactionDetails.get(7).equals("true")) {
                values.put(TransactionEntry.COLUMN_NAME_INCOGNITO, 1);
            } else {
                values.put(TransactionEntry.COLUMN_NAME_INCOGNITO, 0);
            }

            return database.insert(TransactionEntry.TABLE_NAME, null, values);
        }

        /**
         * Add account details to the database
         *
         * @param accountDetails list of account details
         * @return ID internally given to the new entry by the database
         */
        public long addAccountToDb(List<String> accountDetails) {
            SQLiteDatabase database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(AccountEntry.COLUMN_NAME_ACCOUNT, accountDetails.get(0));
            values.put(AccountEntry.COLUMN_NAME_BALANCE, accountDetails.get(1));

            return database.insert(AccountEntry.TABLE_NAME, null, values);
        }

        /**
         * Return a list of Transactions saved in the database
         *
         * @param transactionAmount number of Transactions to fetch
         * @param privacy           Incognito-Status
         * @return List of Transactions
         * @see Transaction
         */
        public List<Transaction> getTransactionsFromDb(int transactionAmount, boolean privacy) {
            Cursor resultCursor = getTransactionsFromDbAsCursor(transactionAmount, privacy);
            List<Transaction> transactions = convertCursorToTransactions(resultCursor);
            resultCursor.close();
            return transactions;
        }

        /**
         * Return a list of Accounts saved in the database
         *
         * @return List of Accounts
         */
        public List<Account> getAccountsFromDb() {
            Cursor resultCursor = getAccountsFromDbAsCursor();
            List<Account> accounts = convertCursorToAccounts(resultCursor);
            resultCursor.close();
            return accounts;
        }

        /**
         * Remove a Transaction from the database
         *
         * @param transactionId ID given to this transaction, when inserted to the database
         * @return number of entries deleted
         */
        public int deleteTransactionFromDb(long transactionId) {
            SQLiteDatabase database = getWritableDatabase();
            return database.delete(
                    TransactionEntry.TABLE_NAME,
                    TransactionEntry._ID + " =?",
                    new String[]{Long.toString(transactionId)}
            );
        }

        /**
         * Remove a Account from the database
         *
         * @param accountId ID given to this Account, when inserted to the database
         * @return number of entries deleted
         */
        public int deleteAccountFromDb(long accountId) {
            SQLiteDatabase database = getWritableDatabase();
            return database.delete(
                    AccountEntry.TABLE_NAME,
                    AccountEntry._ID + " =?",
                    new String[]{Long.toString(accountId)}
            );
        }

        /**
         * Update a transaction stored in the database
         *
         * @param transaction Updated Transaction
         * @return number of entries updated
         */
        public int updateTransaction(Transaction transaction) {
            SQLiteDatabase database = getWritableDatabase();

            ContentValues newValues = new ContentValues();
            newValues.put(TransactionEntry.COLUMN_NAME_AMOUNT, transaction.getAmount());
            newValues.put(TransactionEntry.COLUMN_NAME_TITLE, transaction.getTitle());
            newValues.put(TransactionEntry.COLUMN_NAME_CATEGORY, transaction.getCategory());
            newValues.put(TransactionEntry.COLUMN_NAME_TYPE, transaction.getType());
            newValues.put(TransactionEntry.COLUMN_NAME_DATE, transaction.getDate());
            newValues.put(TransactionEntry.COLUMN_NAME_ACCOUNT, transaction.getAccount());
            newValues.put(TransactionEntry.COLUMN_NAME_INFORMATION, transaction.getInformation());

            String whereClause = "_ID = ?";
            String[] whereArgs = new String[]{Long.toString(transaction.getID())};

            return database.update(
                    TransactionEntry.TABLE_NAME,
                    newValues,
                    whereClause,
                    whereArgs);
        }

        private Cursor getTransactionsFromDbAsCursor(int transactionAmount, boolean privacy) {
            SQLiteDatabase database = getReadableDatabase();
            String limit = Integer.toString(transactionAmount);

            String[] projection = {
                    TransactionEntry._ID,
                    TransactionEntry.COLUMN_NAME_AMOUNT,
                    TransactionEntry.COLUMN_NAME_TITLE,
                    TransactionEntry.COLUMN_NAME_CATEGORY,
                    TransactionEntry.COLUMN_NAME_TYPE,
                    TransactionEntry.COLUMN_NAME_DATE,
                    TransactionEntry.COLUMN_NAME_ACCOUNT,
                    TransactionEntry.COLUMN_NAME_INFORMATION,
                    TransactionEntry.COLUMN_NAME_INCOGNITO
            };

            StringBuilder selection = new StringBuilder();
            List<String> selectionArgs = new ArrayList<>();

            if (!privacy) {
                selection.append(TransactionEntry.COLUMN_NAME_INCOGNITO + " = ?");
                selectionArgs.add("0");
            }

            String sortOrder =
                    TransactionEntry.COLUMN_NAME_DATE + " DESC";

            String[] args = new String[selectionArgs.size()];
            selectionArgs.toArray(args);

            if (transactionAmount == -(1)) {
                return database.query(
                        TransactionEntry.TABLE_NAME,    // Table Name
                        projection,                     // Columns to return
                        selection.toString(),           // Columns for WHERE
                        args,                           // Arguments for WHERE
                        null,                           // How to Group
                        null,                           // How to filter Groups
                        sortOrder);                     // Sort order
            } else {
                return database.query(
                        TransactionEntry.TABLE_NAME,    // Table Name
                        projection,                     // Columns to return
                        selection.toString(),           // Columns for WHERE
                        args,                           // Arguments for WHERE
                        null,                           // How to Group
                        null,                           // How to filter Groups
                        sortOrder,                      // Sort order
                        limit);                         // Number of Rows returned
            }
        }

        private Cursor getAccountsFromDbAsCursor() {
            SQLiteDatabase database = getReadableDatabase();

            String[] projection = {
                    AccountEntry._ID,
                    AccountEntry.COLUMN_NAME_ACCOUNT,
                    AccountEntry.COLUMN_NAME_BALANCE
            };

            String sortOrder =
                    AccountEntry.COLUMN_NAME_BALANCE + " DESC";

            return database.query(
                    AccountEntry.TABLE_NAME,        // Table Name
                    projection,                     // Columns to return
                    null,                           // Columns for WHERE
                    null,                           // Arguments for WHERE
                    null,                           // How to Group
                    null,                           // How to filter Groups
                    sortOrder);                     // Sort order
        }

        private List<Transaction> convertCursorToTransactions(Cursor cursor) {
            List<Transaction> resultSet = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                resultSet.add(entryToTransaction(cursor));
                cursor.moveToNext();
            }
            return resultSet;
        }

        private List<Account> convertCursorToAccounts(Cursor cursor) {
            List<Account> resultSet = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                resultSet.add(entryToAccount(cursor));
                cursor.moveToNext();
            }
            return resultSet;
        }

        private Transaction entryToTransaction(Cursor cursor) {

            Long id = cursor
                    .getLong(cursor.getColumnIndex(TransactionEntry._ID));
            Double amount = cursor
                    .getDouble(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_AMOUNT));
            String title = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_TITLE));
            String category = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_CATEGORY));
            String type = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_TYPE));
            String date = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_DATE));
            String account = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_ACCOUNT));
            String information = cursor
                    .getString(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_INFORMATION));
            int incognito = cursor
                    .getInt(cursor.getColumnIndex(TransactionEntry.COLUMN_NAME_INCOGNITO));

            return new Transaction(id, amount, title,
                    category, type, date, account, information, incognito);
        }

        private Account entryToAccount(Cursor cursor) {
            Long id = cursor
                    .getLong(cursor.getColumnIndex(AccountEntry._ID));
            String accName = cursor
                    .getString(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_ACCOUNT));
            Double balance = cursor
                    .getDouble(cursor.getColumnIndex(AccountEntry.COLUMN_NAME_BALANCE));

            return new Account(id, accName, balance);
        }
    }
}
