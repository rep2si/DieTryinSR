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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RevelationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RevelationsFragment extends android.app.Fragment {

    public TextView received_label, tvReceived, likert_label;
    public int receivedInt;

    private LinearLayout allocBlock, likertBlock;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECEIVED_INT = "receivedInt";
    private static final String ALLOC_EVAL = "allocEval";
    private static final String ANONYMOUS = "anonymousCondition";
    public revelations revelationsActivity; //parent activity!

    private RadioButton likert1, likert2, likert3, likert4, likert5, likert99;
    private RadioGroup likertGroup;
    private String allocEval, anonymousCondition;

    public RevelationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param receivedInt Parameter 1.
     * @param allocEval Parameter 2.
     * @param anonymousCondition Parameter 3.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RevelationsFragment newInstance(Integer receivedInt, String allocEval, String anonymousCondition) {
        RevelationsFragment fragment = new RevelationsFragment();
        Bundle args = new Bundle();
        args.putInt(RECEIVED_INT, receivedInt);
        args.putString(ALLOC_EVAL, allocEval);
        args.putString(ANONYMOUS, anonymousCondition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receivedInt = getArguments().getInt(RECEIVED_INT);
            allocEval = getArguments().getString(ALLOC_EVAL);
            anonymousCondition = getArguments().getString(ANONYMOUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view that we will inflate lower
        View view = inflater.inflate(R.layout.fragment_revelations, container, false);

        tvReceived = view.findViewById(R.id.received);
        received_label = view.findViewById(R.id.received_label);
        allocBlock = view.findViewById(R.id.allocBlock);
        likertBlock = view.findViewById(R.id.likertBlock);

        likert1 = view.findViewById(R.id.likert1);
        likert2 = view.findViewById(R.id.likert2);
        likert3 = view.findViewById(R.id.likert3);
        likert4 = view.findViewById(R.id.likert4);
        likert5 = view.findViewById(R.id.likert5);
        likert99 = view.findViewById(R.id.likert99);
        likertGroup = view.findViewById(R.id.likertGroup);
        likert_label = view.findViewById(R.id.likert_label);

        // Get parent activity
        // Note: switch to requireActivity() once we use a more recent SDK
        revelationsActivity = (revelations) getActivity();
        MainActivity mainActivity = (MainActivity) getActivity();

        // apply translations
        received_label.setText(mainActivity.i18nMap.get("payout_received"));
        likert_label.setText(mainActivity.i18nMap.get("payout_likert_label"));

        tvReceived.setText(String.valueOf(receivedInt));

        // hide/show elements depending on settings and condition
        String showLikert = mainActivity.getGlobalSetting("likertInRevelations");
        if (showLikert.equals("true")) {
            likertBlock.setVisibility(View.VISIBLE);
        } else if (showLikert.equals("revealedOnly") & !anonymousCondition.equals("true") ) {
            likertBlock.setVisibility(View.VISIBLE);
        } else {
            likertBlock.setVisibility(View.GONE);
        }

        Integer NLikertLevels = Integer.parseInt(revelationsActivity.getGameSetting(revelationsActivity.gameStamp, "NlikertLevels"));

        // List of buttons
        List<RadioButton> allButtons = new ArrayList<>(Arrays.asList(likert1, likert2, likert3, likert4, likert5));

        // Set the function of all buttons
        Integer n_btns = allButtons.toArray().length;
        if (allocEval.equals("")){
            for (int i = 0; i < n_btns; i++) {
                Integer val = i + 1;
                allButtons.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        revelationsActivity.allocEval = Integer.toString(val);
                        likert99.setAlpha(0.5F);
                        for (int i = 0; i < n_btns; i++) {
                            allButtons.get(i).setAlpha(1);
                        }
                    }
                });
                // Set function of "don't know" button
                likert99.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        revelationsActivity.allocEval = "99";
                        likert99.setAlpha(1);
                        for (int i = 0; i < n_btns; i++) {
                            allButtons.get(i).setAlpha(0.5F);
                        }
                    }
                });
            }
        }
        else{
            // Disable all buttons and tick the correct one
            likertGroup.clearCheck();
            for (int i = 0; i < n_btns; i++) {
                if (allocEval.equals(Integer.toString(i + 1))) {
                    allButtons.get(i).setChecked(true); //tick
                }
                allButtons.get(i).setEnabled(false); // disable input
            }
            likert99.setEnabled(false);
            // Check don't know if adequate
            if (allocEval.equals("99")) {
                likert99.setChecked(true);
                likert99.setEnabled(false);
                likert99.setAlpha(1);
            }
        }

        // Hide buttons for which we do not have a likert level
        for (int i = NLikertLevels; i < n_btns; i++) {
            allButtons.get(i).setVisibility(View.GONE);
        }

        // Set button text
        for (int i = 0; i < NLikertLevels; i++) {
            String text = revelationsActivity.getGameSetting(revelationsActivity.gameStamp, "likertLevel" + Integer.toString(i + 1));
            allButtons.get(i).setText(text);
        }

        // Set don't know text
        likert99.setText(revelationsActivity.getGameSetting(revelationsActivity.gameStamp, "dontKnowText"));

        return view;
    } //end oncreateview
}
