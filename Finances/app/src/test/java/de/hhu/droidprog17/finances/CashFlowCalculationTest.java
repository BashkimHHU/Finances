package de.hhu.droidprog17.finances;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.hhu.droidprog17.finances.model.Transaction;
import de.hhu.droidprog17.finances.view.TransactionsActivity;
import de.hhu.droidprog17.finances.model.TransactionsDataManager;

import static org.junit.Assert.*;

/**
 * This Test checks if the cash flow calculation is correct.
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 */

public class CashFlowCalculationTest {

    private static final String SPEND = "Spend";
    private static final String EARNED = "Earned";

    private Transaction mTransactionSpend10;
    private Transaction mTransactionSpend50;
    private Transaction mTransactionEarned20;
    private Transaction mTransactionEarned50;
    private double mEpsilon;
    private TransactionsDataManager mDataManager;

    @Before
    public void defineFields() {
        mTransactionSpend10 = new Transaction(10.0, SPEND);
        mTransactionSpend50 = new Transaction(50.5, SPEND);
        mTransactionEarned20 = new Transaction(20.0, EARNED);
        mTransactionEarned50 = new Transaction(50.5, EARNED);
        mEpsilon = 0;
        mDataManager = new TransactionsDataManager(null, null);
    }

    @Test
    public void additionNoEarnings() throws ComparisonFailure {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(mTransactionSpend10);
        transactions.add(mTransactionSpend50);

        List<Double> values = mDataManager.calculation(transactions, SPEND);

        assertEquals(values.get(0), 0, mEpsilon);
        assertEquals(values.get(1), -60.5, mEpsilon);
    }

    @Test
    public void additionNoSpends() throws ComparisonFailure {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(mTransactionEarned20);
        transactions.add(mTransactionEarned50);

        List<Double> values = mDataManager.calculation(transactions, SPEND);

        assertEquals(values.get(0), 70.5, mEpsilon);
        assertEquals(values.get(1), 0, mEpsilon);
    }

    @Test
    public void additionMixed() throws ComparisonFailure {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(mTransactionSpend10);
        transactions.add(mTransactionSpend50);
        transactions.add(mTransactionEarned20);
        transactions.add(mTransactionEarned50);

        List<Double> values = mDataManager.calculation(transactions, SPEND);

        assertEquals(values.get(0), 70.5, mEpsilon);
        assertEquals(values.get(1), -60.5, mEpsilon);
    }
}
