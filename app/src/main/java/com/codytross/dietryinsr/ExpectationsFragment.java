package com.codytross.dietryinsr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpectationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpectationsFragment extends android.app.Fragment {

    public TextView expected_label, tvExpected;
    public String expected;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXPECTED = "expected";
    public expectations expectationsActivity; //parent activity!


    public ExpectationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param expected    Parameter 1.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpectationsFragment newInstance(String expected) {
        ExpectationsFragment fragment = new ExpectationsFragment();
        Bundle args = new Bundle();
        args.putString(EXPECTED, expected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expected = getArguments().getString(EXPECTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view that we will inflate lower
        View view = inflater.inflate(R.layout.fragment_expectations, container, false);

        tvExpected = view.findViewById(R.id.expected);
        expected_label = view.findViewById(R.id.expected_label);

        // Get parent activity
        // Note: switch to requireActivity() once we use a more recent SDK
        expectationsActivity = (expectations) getActivity();
        MainActivity mainActivity = (MainActivity) getActivity();

        // apply translations
        expected_label.setText(mainActivity.i18nMap.get("expect"));
        tvExpected.setHint(mainActivity.i18nMap.get("expectHint"));

        // If there is already saved data, display and disable entry
        if (!expected.equals("")) {
            tvExpected.setText(expected);
            tvExpected.setEnabled(false);

        } else {
            // required to check if has been input when saving
            expectationsActivity.expectedAmt = "";
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

        return view;
    } //end oncreateview
}
