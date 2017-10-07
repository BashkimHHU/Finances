package de.hhu.droidprog17.finances.model;

/**
 * This Class represents the structure of a Account that can be created within this project
 *
 * @author Bashkim Berzati
 * @version 1.0
 */

public class Account {

    private Long mID;
    private String mAccountName;
    private Double mBalance;

    /**
     * @param id      account ID assigned by the data base
     * @param name    account name
     * @param balance account balance
     */
    public Account(Long id, String name, Double balance) {
        mID = id;
        mAccountName = name;
        mBalance = balance;
    }

    /**
     * Return the ID that was assigned by the data base
     *
     * @return Data base ID
     */
    public Long getID() {
        return mID;
    }

    /**
     * Return the account name determined by the user
     *
     * @return account name
     */
    public String getAccountName() {
        return mAccountName;
    }

    /**
     * Return the current balance of the corresponding account
     *
     * @return account balance
     */
    public Double getBalance() {
        return mBalance;
    }

    /**
     * Round current Account-Balance to two decimal places.
     *
     * @return account balance
     */
    public void roundBalance(){
        mBalance = Math.round(mBalance * 100.0) / 100.0;
    }

    /**
     * Subtracts specified amount from the current account balance
     *
     * @param amount amount that should be subtracted from the account balance
     */
    public void withdraw(Double amount) {
        mBalance = mBalance - amount;
    }

    /**
     * Adds specified amount to the current account balance
     *
     * @param amount amount that should be add to the account balance
     */
    public void deposit(Double amount) {
        mBalance = mBalance + amount;
    }
}
