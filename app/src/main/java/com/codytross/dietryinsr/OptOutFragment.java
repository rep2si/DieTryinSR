package com.codytross.dietryinsr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptOutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptOutFragment extends android.app.Fragment {

    private Button btnOptOut,btnOptIn;
    private TextView optOutAmount, endowment, endowmentLabel, optOutLabel;
    private int endowmentInt,optOutInt;
    public dg dgActivity; //parent activity!

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OptOutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptOutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptOutFragment newInstance(String param1, String param2) {
        OptOutFragment fragment = new OptOutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Prepare view to inflate
        View view =inflater.inflate(R.layout.fragment_opt_out, container, false);

        // Get parent activity
        dgActivity = (dg) getActivity();

        // get resources
        btnOptOut = view.findViewById(R.id.btnOptOut);
        btnOptIn = view.findViewById(R.id.btnOptIn);
        optOutAmount = view.findViewById(R.id.optout_amount);
        endowment = view.findViewById(R.id.endowment);
        endowmentLabel = view.findViewById(R.id.endowment_label);
        optOutLabel = view.findViewById(R.id.optout_label);
        endowmentInt = getResources().getInteger(R.integer.endowmentInt);
        optOutInt = getResources().getInteger(R.integer.optOutInt);

        // Display endowment and opt out values
        endowment.setText(Integer.toString(endowmentInt));
        optOutAmount.setText(Integer.toString(optOutInt));


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