package com.codytross.dietryinsr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.TextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferFragment extends android.app.Fragment {

    public TextView offer_label, endowment, endowment_label, game_id2;
    public int endowmentInt;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public dg dgActivity; //parent activity!

    // TODO: Rename and change types of parameters
    private String mParam1;

    public OfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferFragment newInstance(String param1) {
        OfferFragment fragment = new OfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
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
        endowmentInt = getResources().getInteger(R.integer.endowmentInt);

        // Get parent activity
        dgActivity = (dg) getActivity();

        // If there is already saved data, display and disable entry

        if(!mParam1.equals("")){
            Integer offerInt = Integer.parseInt(mParam1);
            endowment.setText(String.valueOf(endowmentInt - offerInt));
            game_id2.setEnabled(false);
            game_id2.setText(mParam1);
        }


        // Set initial endowment
        endowment.setText(Integer.toString(endowmentInt));

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

        // inflate view
        return view;
    }
}
