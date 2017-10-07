package de.hhu.droidprog17.finances.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hhu.droidprog17.finances.R;

/**
 * This Fragment enables the user to select a category for the transactions made
 *
 * @author Bashkim Berzati
 * @version 1.0
 * @see MainContentFragment
 * @see MainActivity
 * @see TransactionsUpdateActivity
 */

public class CategoryFragment extends Fragment implements View.OnTouchListener {
    private static final String TAG = "Category_Frag";
    private CategoryFragmentInterface mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        implementListeners(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT > 22) {
            try {
                mCallback = (CategoryFragmentInterface) context;
            } catch (ClassCastException e) {
                Log.e(TAG, context.toString() + " does not implement interface");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (CategoryFragmentInterface) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, activity.toString() + " does not implement interface");
            }
        }
    }

    private void implementListeners(View view) {
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.category_parent_layout);
        String pattern = "category_";
        String[] textViewNames = new String[parentLayout.getChildCount()];
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            textViewNames[i] = pattern + Integer.toString(i);
        }

        for (int i = 0; i < textViewNames.length; i++) {
            int textViewId = getResources().getIdentifier(textViewNames[i],
                    "id",
                    getActivity().getPackageName());
            TextView textView = (TextView) view.findViewById(textViewId);
            textView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            TextView categoryTextView = (TextView) v;
            String category = categoryTextView.getText().toString();
            mCallback.returnCategory(category);
        }
        return true;
    }

    /**
     * This Interface gives report to attached Activity that the user selected a category
     */
    public interface CategoryFragmentInterface {

        /**
         * Return chosen category
         *
         * @param category category selected
         */
        void returnCategory(String category);
    }
}
