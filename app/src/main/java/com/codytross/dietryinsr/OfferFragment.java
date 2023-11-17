package com.codytross.dietryinsr;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextWatcher;
import android.widget.ToggleButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferFragment extends android.app.Fragment {

    public TextView offer_label, endowment, endowment_label, game_id2, tvOptOut;
    public int endowmentInt, optOutKeepInt;
    private ToggleButton btnOptOut;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String OFFER = "Offer";
    private static final String ENDOWMENT_INT = "endowmentInt";
    private static final String OPTOUTKEEP_INT = "optOutKeepInt";
    private static final String RECORDED_OPTOUT = "recordedOptOut";
    private static final String RECORDED_ASK_OPTOUT = "recordedAskOptOut";

    public dg dgActivity; //parent activity!

    // TODO: Rename and change types of parameters
    private String offer, recordedOptOut, recordedAskOptOut;

    public OfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param offer Parameter 1.
     * @param endowmentInt Parameter 2.
     * @param optOutKeepInt Parameter 3.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferFragment newInstance(String offer, String recordedOptOut, String recordedAskOptOut, Integer endowmentInt, Integer optOutKeepInt) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putString(OFFER, offer);
        args.putString(RECORDED_OPTOUT, recordedOptOut);
        args.putString(RECORDED_ASK_OPTOUT, recordedAskOptOut);
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
            recordedOptOut = getArguments().getString(RECORDED_OPTOUT);
            recordedAskOptOut = getArguments().getString(RECORDED_ASK_OPTOUT);
            endowmentInt = getArguments().getInt(ENDOWMENT_INT);
            optOutKeepInt = getArguments().getInt(OPTOUTKEEP_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create view that we will inflate lower
        View view = inflater.inflate(R.layout.fragment_offer, container, false);

        game_id2 = view.findViewById(R.id.game_id2);
        offer_label = view.findViewById(R.id.offer_label);
        endowment = view.findViewById(R.id.endowment);
        endowment_label = view.findViewById(R.id.endowment_label);
        btnOptOut = view.findViewById(R.id.btnOptOut);
        tvOptOut = view.findViewById(R.id.optout_amount);


        // Get parent activity
        dgActivity = (dg) getActivity();

        // If there is already saved data, display and disable entry
        if(!offer.equals("") && !recordedOptOut.equals("true")){
            Integer offerInt = Integer.parseInt(offer);
            endowment.setText(String.valueOf(endowmentInt - offerInt));
            tvOptOut.setText(Integer.toString(optOutKeepInt));
            game_id2.setEnabled(false);
            game_id2.setText(offer);
            if(recordedAskOptOut.equals("true")) {
                tvOptOut.setVisibility(View.GONE);
                btnOptOut.setVisibility(View.GONE);
            }
        } else if (!offer.equals("")) {
            Integer offerInt = Integer.parseInt(offer);
            endowment.setEnabled(false);
            endowment.setAlpha(0.3F);
            game_id2.setEnabled(false);
            game_id2.setAlpha(0.3F);;
            tvOptOut.setText(Integer.toString(optOutKeepInt));
            tvOptOut.setAlpha(1);
            //make button red
            btnOptOut.setBackgroundColor(Color.RED);
            btnOptOut.setAlpha(1);
            game_id2.setText("");
            // Hide opt out button if chose to opt in at earlier stage
        } else {
        // Set initial endowment
            endowment.setText(Integer.toString(endowmentInt));
            tvOptOut.setText(Integer.toString(optOutKeepInt));
            dgActivity.gameOffer2 = ""; //required to check if any input when saving
            // Hide opt out button if has already opted in.
            if(recordedOptOut.equals("hideOptOutBtn")) {
                tvOptOut.setVisibility(View.GONE);
                btnOptOut.setVisibility(View.GONE);
            }
        }

        // Watch for offer changes and update endowment on the fly
        game_id2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int offer = Integer.parseInt(game_id2.getText().toString());
                    if (offer > endowmentInt) {
                        game_id2.setText(Integer.toString(endowmentInt));
                        offer = endowmentInt;
                    } else if (offer < 0) {
                        game_id2.setText(Integer.toString(0));
                        offer = 0;
                    }
                    endowment.setText(Integer.toString(endowmentInt - offer));
                    // Set gameOffer values in parent activity
                    dgActivity.gameOffer2 = Integer.toString(offer);
                    dgActivity.gameOffer1 = Integer.toString(endowmentInt - offer);

                } catch (NumberFormatException nfe) {
                    endowment.setText(Integer.toString(endowmentInt));
                }
            }
        });

        btnOptOut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                optOut();
            } else {
                cancelOptOut();
            }
        });

        // inflate view
        return view;
    }

    private void optOut() {
        btnOptOut.setBackgroundColor(Color.RED);
        btnOptOut.setAlpha(1);
        endowment.setAlpha(0.3F);
        game_id2.setEnabled(false);
        game_id2.setAlpha(0.3F);
        tvOptOut.setAlpha(1);
        game_id2.setText("");
        endowment.setText(Integer.toString(endowmentInt));
        // Record amt taken home
        dgActivity.hasOptedOut = true;
        dgActivity.gameOffer1 = Integer.toString(optOutKeepInt);
        dgActivity.gameOffer2 = "0";
    }

    private void cancelOptOut() {
        btnOptOut.setBackgroundColor(getContext().getColor(R.color.colorInactive));
        btnOptOut.setAlpha(0.3F);
        endowment.setAlpha(1);
        game_id2.setEnabled(true);
        game_id2.setAlpha(1);
        tvOptOut.setAlpha(0.3F);
        // Force player to re-enter amount given
        dgActivity.hasOptedOut = false;
        game_id2.setText("");
        endowment.setText(Integer.toString(endowmentInt));
        dgActivity.gameOffer2 = ""; //required to check if any input when saving
    }
}
