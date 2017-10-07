package de.hhu.droidprog17.finances.model;

import android.os.Parcel;
import android.os.Parcelable;

import de.hhu.droidprog17.finances.view.TransactionsActivity;
import de.hhu.droidprog17.finances.view.TransactionsUpdateActivity;

/**
 * This Class represents the structure of a Transaction that can be created within this project
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class Transaction implements Parcelable {

    private Long mID;
    private Double mAmount;
    private String mTitle;
    private String mCategory;
    private String mType;
    private String mDate;
    private String mAccount;
    private String mInformation;
    private boolean mIncognito;

    public Transaction(Double amount, String type) {
        mAmount = amount;
        mType = type;
    }

    /**
     * @param id          ID internally given by the database
     * @param amount      transaction amount
     * @param title       transaction title
     * @param category    transaction category
     * @param type        transaction type (spend or earned)
     * @param date        transaction date
     * @param account     transaction account
     * @param information additional transaction information
     * @param incognito   is secret. 0 if not 1 otherwise
     */
    public Transaction(Long id, Double amount, String title, String category, String type,
                       String date, String account, String information, int incognito) {
        mID = id;
        mTitle = title;
        mAmount = amount;
        mCategory = category;
        mType = type;
        mDate = date;
        mAccount = account;
        mInformation = information;

        if (incognito == 0) {
            mIncognito = false;
        } else {
            mIncognito = true;
        }
    }

    /**
     * get ID given by the database
     *
     * @return transactionID
     */
    public Long getID() {
        return mID;
    }

    /**
     * Converts the ID to a String representation
     *
     * @return ID value as String
     */
    public String getIdAsString() {
        return Long.toString(mID);
    }

    /**
     * Return the amount specified by the Transaction
     *
     * @return transaction amount
     */
    public Double getAmount() {
        return mAmount;
    }

    /**
     * Converts the amount to a String representation
     *
     * @return amount as String
     */
    public String getAmountAsString() {
        return Double.toString(mAmount);
    }

    /**
     * Set transaction amount to a specified value
     *
     * @param amount new transaction amount
     */
    public void setAmount(Double amount) {
        mAmount = amount;
    }

    /**
     * Return the transaction title
     *
     * @return transaction title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set transaction title to a specified value
     *
     * @param title new transaction title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Return transaction category
     *
     * @return transaction category
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * Set transaction category to a specified value
     *
     * @param category new transaction category
     */
    public void setCategory(String category) {
        mCategory = category;
    }

    /**
     * Return transaction type
     *
     * @return new transaction type
     */
    public String getType() {
        return mType;
    }

    /**
     * Set transaction type to a specified value
     *
     * @param type new transaction type
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * Return transaction date
     *
     * @return transaction date
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Set transaction date to a specified value
     *
     * @param date new transaction date
     */
    public void setDate(String date) {
        mDate = date;
    }

    /**
     * Return transaction account
     *
     * @return transaction account
     */
    public String getAccount() {
        return mAccount;
    }

    /**
     * Set transaction account to a specified value
     *
     * @param account new transaction account
     */
    public void setAccount(String account) {
        mAccount = account;
    }

    /**
     * Return additional transaction information
     *
     * @return additional transaction information
     */
    public String getInformation() {
        return mInformation;
    }

    /**
     * Set transaction information to a specified value
     *
     * @param information new transaction information
     */
    public void setInformation(String information) {
        mInformation = information;
    }

    /**
     * Return whether this transaction is private
     *
     * @return true if incognito, false otherwise
     */
    public boolean getIncognito() {
        return mIncognito;
    }

    /**
     * Checks each field for a specific pattern
     *
     * @param pattern pattern that is matched against each transaction value
     * @return true if match found, false otherwise
     */
    public boolean containsPattern(String pattern) {
        if (getAmountAsString().toLowerCase().contains(pattern)) {
            return true;
        } else if (getTitle().toLowerCase().contains(pattern)) {
            return true;
        } else if (getType().toLowerCase().contains(pattern)) {
            return true;
        } else if (getCategory().toLowerCase().contains(pattern)) {
            return true;
        } else if (getDate().contains(pattern)) {
            return true;
        } else if (getAccount().toLowerCase().contains(pattern)) {
            return true;
        } else if (getInformation().toLowerCase().contains(pattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static final Parcelable.Creator<Transaction> CREATOR =
            new Parcelable.Creator<Transaction>() {
                public Transaction createFromParcel(Parcel in) {
                    return new Transaction(in);
                }

                public Transaction[] newArray(int size) {
                    return new Transaction[size];
                }
            };

    /**
     * Necessary to rebuild Transaction from Intent
     *
     * @param in transaction fields
     * @see TransactionsActivity
     * @see TransactionsUpdateActivity
     */
    public Transaction(Parcel in) {
        mID = in.readLong();
        mAmount = in.readDouble();
        mTitle = in.readString();
        mCategory = in.readString();
        mType = in.readString();
        mDate = in.readString();
        mAccount = in.readString();
        mInformation = in.readString();
        mIncognito = (Boolean) in.readValue(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
        dest.writeDouble(mAmount);
        dest.writeString(mTitle);
        dest.writeString(mCategory);
        dest.writeString(mType);
        dest.writeString(mDate);
        dest.writeString(mAccount);
        dest.writeString(mInformation);
        dest.writeValue(mIncognito);
    }
}
