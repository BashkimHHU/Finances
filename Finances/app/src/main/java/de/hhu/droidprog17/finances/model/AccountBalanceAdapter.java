package de.hhu.droidprog17.finances.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hhu.droidprog17.finances.view.AccountBalanceActivity;
import de.hhu.droidprog17.finances.R;

/**
 * This Adapter populates the ListView responsible for the overview of all accounts and
 * account balances
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see AccountBalanceActivity
 */

public class AccountBalanceAdapter extends ArrayAdapter<Account> {
    private Context mContext;
    private List<Account> mAccounts;

    /**
     * @param context  calling context
     * @param accounts list of accounts to display
     */
    public AccountBalanceAdapter(Context context, List<Account> accounts) {
        super(context, R.layout.listview_rowlayout_accounts, accounts);
        mContext = context;
        mAccounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Account listEntry = mAccounts.get(position);

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_rowlayout_accounts, parent, false);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.account_name);
        TextView balanceTextView = (TextView) rowView.findViewById(R.id.account_balance);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.account_image);

        nameTextView.setText(listEntry.getAccountName());
        balanceTextView.setText(Double.toString(listEntry.getBalance()));

        modifyLayout(listEntry.getBalance(), balanceTextView, imageView);

        return rowView;
    }

    private void modifyLayout(Double amount, TextView balanceTextView, ImageView imageView) {
        if (amount.equals(0.0)) {
            imageView.setImageResource(R.drawable.ic_trending_flat_black_24dp);
        } else if (amount < 0.0) {
            balanceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            imageView.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
        } else if (amount > 0.0) {
            balanceTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            imageView.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
        }
    }
}
