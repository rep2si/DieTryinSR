package com.codytross.dietryinsr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayoutFragment extends android.app.Fragment {

    public TextView expected_label, received_label, tvExpected, tvReceived;
    public int receivedInt;
    public String expected;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXPECTED = "expected";
    private static final String RECEIVED_INT = "receivedInt";
    public payout payoutActivity; //parent activity!

    public PayoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param expected    Parameter 1.
     * @param receivedInt Parameter 2.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PayoutFragment newInstance(String expected, Integer receivedInt) {
        PayoutFragment fragment = new PayoutFragment();
        Bundle args = new Bundle();
        args.putString(EXPECTED, expected);
        args.putInt(RECEIVED_INT, receivedInt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expected = getArguments().getString(EXPECTED);
            receivedInt = getArguments().getInt(RECEIVED_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view that we will inflate lower
        View view = inflater.inflate(R.layout.fragment_payout, container, false);

        tvExpected = view.findViewById(R.id.expected);
        expected_label = view.findViewById(R.id.expected_label);
        tvReceived = view.findViewById(R.id.received);
        received_label = view.findViewById(R.id.received_label);

        // Get parent activity
        payoutActivity = (payout) getActivity();

        // If there is already saved data, display and disable entry
        if (!expected.equals("")) {
            tvExpected.setText(expected);
            tvExpected.setEnabled(false);
            tvReceived.setText(String.valueOf(receivedInt));
            tvReceived.setVisibility(View.VISIBLE);

        } else {
            // required to check if has been input when saving
            payoutActivity.expectedAmt = "";
            tvReceived.setText(String.valueOf(receivedInt));
            // Watch for offer changes and update variable
            tvExpected.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        int expected = Integer.parseInt(tvExpected.getText().toString());
                        // Should reimplement max amount, but that needs work in DieTryin
//                    if (expected > endowmentInt) {
//                        game_id2.setText(Integer.toString(endowmentInt));
//                        expected = endowmentInt;
//                    } else if (expected < 0) {
                        if (expected < 0) {
                            tvExpected.setText(Integer.toString(0));
                            expected = 0;
                        }
                        // Set gameOffer values in parent activity
                        payoutActivity.expectedAmt = Integer.toString(expected);

                    } catch (NumberFormatException nfe) {
                        tvExpected.setText(Integer.toString(0));
                    }
                }
            });
        }
        return view;
    } //end oncreateview
}
