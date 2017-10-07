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

import de.hhu.droidprog17.finances.R;
import de.hhu.droidprog17.finances.view.TransactionsActivity;

/**
 * This Adapter populates the ListView responsible for the overview of Transactions
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see TransactionsActivity
 */

public class TransactionsAdapter extends ArrayAdapter<Transaction> {
    private Context mContext;
    private List<Transaction> mValues;

    /**
     * @param context calling Context
     * @param values  List of Transactions that should be displayed
     */
    public TransactionsAdapter(Context context, List<Transaction> values) {
        super(context, R.layout.listview_rowlayout_2, values);
        mContext = context;
        mValues = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction listEntry = mValues.get(position);

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_rowlayout_2, parent, false);
        TextView idTextView = (TextView) rowView.findViewById(R.id.list_id);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.list_title);
        TextView amountTextView = (TextView) rowView.findViewById(R.id.list_amount);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.list_date);
        TextView categoryTextView = (TextView) rowView.findViewById(R.id.list_category);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);

        idTextView.setText(listEntry.getIdAsString());
        titleTextView.setText(listEntry.getTitle());
        amountTextView.setText(listEntry.getAmountAsString());
        dateTextView.setText(listEntry.getDate());
        categoryTextView.setText(listEntry.getCategory());

        modifyLayout(listEntry.getType(), amountTextView, imageView);

        return rowView;
    }

    private void modifyLayout(String type, TextView amountTextView, ImageView imageView) {
        if (type.equals(mContext.getResources().getString(R.string.type_spend))) {
            amountTextView.setText(amountTextView.getText());
            amountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            imageView.setImageResource(R.drawable.ic_trending_down_black_24dp);
        } else {
            amountTextView.setText(amountTextView.getText());
            amountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            imageView.setImageResource(R.drawable.ic_trending_up_black_24dp);
        }
    }
}
