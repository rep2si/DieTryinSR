package com.codytross.dietryinsr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptOutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptOutFragment extends android.app.Fragment {

    private Button btnOptOut,btnOptIn;
    private TextView optOutAmount, endowment, endowmentLabel, optOutLabel;
    private Integer endowmentInt, optOutKeepInt;
    public dg dgActivity; //parent activity!

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String OFFER = "Offer";
    private static final String ENDOWMENT_INT = "endowmentInt";
    private static final String OPTOUTKEEP_INT = "optOutKeepInt";

    // TODO: Rename and change types of parameters
    private String offer;

    public OptOutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param offer Parameter 1.
     * @param endowmentInt Parameter 2.
     * @param optOutKeepInt Parameter 3.
     * @return A new instance of fragment OptOutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptOutFragment newInstance(String offer, Integer endowmentInt, Integer optOutKeepInt) {
        OptOutFragment fragment = new OptOutFragment();
        Bundle args = new Bundle();
        args.putString(OFFER, offer);
        args.putInt(ENDOWMENT_INT, endowmentInt);
        args.putInt(OPTOUTKEEP_INT, optOutKeepInt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offer = getArguments().getString(OFFER);
            endowmentInt = getArguments().getInt(ENDOWMENT_INT);
            optOutKeepInt = getArguments().getInt(OPTOUTKEEP_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Prepare view to inflate
        View view = inflater.inflate(R.layout.fragment_opt_out, container, false);

        // Get parent activity
        dgActivity = (dg) getActivity();

        // get resources
        btnOptOut = view.findViewById(R.id.btnOptOut);
        btnOptIn = view.findViewById(R.id.btnOptIn);
        optOutAmount = view.findViewById(R.id.optout_amount);
        endowment = view.findViewById(R.id.endowment);
        endowmentLabel = view.findViewById(R.id.endowment_label);
        optOutLabel = view.findViewById(R.id.optout_label);
//        endowmentInt = getResources().getInteger(R.integer.endowmentInt);
//        optOutInt = getResources().getInteger(R.integer.optOutInt);

        // Display endowment and opt out values
        endowment.setText(Integer.toString(endowmentInt));
        optOutAmount.setText(Integer.toString(optOutKeepInt));

        // Assign functions to buttons
        btnOptIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optIn();
            }
        });

        btnOptOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optOut();
            }
        });

        // Check if this player already saved and display if so
        if(!offer.equals("")) {
            // Visual stuff
            btnOptOut.setBackgroundColor(Color.RED);
            btnOptIn.setBackgroundColor(Color.DKGRAY);
            endowment.setAlpha(0.3F);
            optOutAmount.setAlpha(1);
            optOutLabel.setAlpha(1);
            endowmentLabel.setAlpha(0.3F);
            dgActivity.hasOptedOut = true;
            dgActivity.hasOptedIn = false;

            // Disable buttons
            btnOptIn.setEnabled(false);
            btnOptOut.setEnabled(false);
        }

        // Inflate view
        return view;
    }

    private void optIn() {
        //Visual stuff
        btnOptIn.setBackgroundColor(Color.GREEN);
        btnOptOut.setBackgroundColor(Color.DKGRAY);
        endowment.setAlpha(1);
        endowmentLabel.setAlpha(1);
        optOutAmount.setAlpha(0.3F);
        optOutLabel.setAlpha(0.3F);
        dgActivity.hasOptedOut = false;
        dgActivity.hasOptedIn = true;
    }

    private void optOut() {
        //Visual stuff
        btnOptOut.setBackgroundColor(Color.RED);
        btnOptIn.setBackgroundColor(Color.DKGRAY);
        endowment.setAlpha(0.3F);
        optOutAmount.setAlpha(1);
        optOutLabel.setAlpha(1);
        endowmentLabel.setAlpha(0.3F);
        dgActivity.hasOptedOut = true;
        dgActivity.hasOptedIn = false;
    }
}