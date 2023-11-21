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
 * Use the {@link ExpectationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpectationsFragment extends android.app.Fragment {

    public TextView expected_label, received_label, tvExpected, tvReceived;
    public int receivedInt;
    public String expected;
    private Boolean hideActualAllocation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXPECTED = "expected";
    private static final String RECEIVED_INT = "receivedInt";
    private static final String HIDE_ALLOC = "hideActualAllocation";
    public expectations expectationsActivity; //parent activity!

    public ExpectationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param expected    Parameter 1.
     * @param receivedInt Parameter 2.
     * @param hideActualAllocation Parameter 2.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpectationsFragment newInstance(String expected, Integer receivedInt, Boolean hideActualAllocation) {
        ExpectationsFragment fragment = new ExpectationsFragment();
        Bundle args = new Bundle();
        args.putString(EXPECTED, expected);
        args.putInt(RECEIVED_INT, receivedInt);
        args.putBoolean(HIDE_ALLOC, hideActualAllocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expected = getArguments().getString(EXPECTED);
            receivedInt = getArguments().getInt(RECEIVED_INT);
            hideActualAllocation = getArguments().getBoolean(HIDE_ALLOC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view that we will inflate lower
        View view = inflater.inflate(R.layout.fragment_expectations, container, false);

        tvExpected = view.findViewById(R.id.expected);
        expected_label = view.findViewById(R.id.expected_label);
        tvReceived = view.findViewById(R.id.received);
        received_label = view.findViewById(R.id.received_label);

        // Get parent activity
        expectationsActivity = (expectations) getActivity();
        MainActivity mainActivity = (MainActivity) getActivity();

        // This is almost certain horrible
        String expectText = mainActivity.getI18n("expect");
        String expectHint =  mainActivity.getI18n("expectHint");
        String receivedText = mainActivity.getI18n("received");

        expected_label.setText(expectText);
        tvExpected.setHint(expectHint);
        received_label.setText(receivedText);


        // If there is already saved data, display and disable entry
        if (!expected.equals("")) {
            tvExpected.setText(expected);
            tvExpected.setEnabled(false);
//            tvReceived.setText(String.valueOf(receivedInt));
//            tvReceived.setVisibility(View.VISIBLE);

        } else {
            // required to check if has been input when saving
            expectationsActivity.expectedAmt = "";
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
                        expectationsActivity.expectedAmt = Integer.toString(expected);

                    } catch (NumberFormatException nfe) {
                        tvExpected.setText(Integer.toString(0));
                    }
                }
            });
        }

        // hide/show elements depending on stage we are in
        if(hideActualAllocation) {
            tvExpected.setVisibility(View.VISIBLE);
            expected_label.setVisibility(View.VISIBLE);
            tvReceived.setVisibility(View.GONE);
            received_label.setVisibility(View.GONE);
        } else {
            tvReceived.setText(String.valueOf(receivedInt));
            tvExpected.setVisibility(View.GONE);
            expected_label.setVisibility(View.GONE);
            tvReceived.setVisibility(View.VISIBLE);
            received_label.setVisibility(View.VISIBLE);
        }

        return view;
    } //end oncreateview
}
