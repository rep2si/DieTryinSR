package com.codytross.dietryinsr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepFragment extends android.app.Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REP_EVAL = "evaluation";
    private static final String Q_TXT = "question";

    // TODO: Rename and change types of parameters
    private String repEval, questionText;
    public reputation repActivity;
    private TextView tvQuestion;
    private RadioButton likert1, likert2, likert3, likert4, likert5;
    private RadioGroup likertGroup;

    public RepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param repEval
     * @return A new instance of fragment RepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepFragment newInstance(String repEval, String questionText) {
        RepFragment fragment = new RepFragment();
        Bundle args = new Bundle();
        args.putString(REP_EVAL, repEval);
        args.putString(Q_TXT, questionText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            repEval = getArguments().getString(REP_EVAL);
            questionText = getArguments().getString(Q_TXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Prepare view to inflate
        View view = inflater.inflate(R.layout.fragment_reputation, container, false);

        // Get parent activity
        repActivity = (reputation) getActivity();

        // resources
        tvQuestion = view.findViewById(R.id.tvQuestion);
        likert1 = view.findViewById(R.id.likert1);
        likert2 = view.findViewById(R.id.likert2);
        likert3 = view.findViewById(R.id.likert3);
        likert4 = view.findViewById(R.id.likert4);
        likert5 = view.findViewById(R.id.likert5);
        likertGroup = view.findViewById(R.id.likertGroup);

        // Set question text
        tvQuestion.setText(questionText);

        // We are going to do things to all buttons using for loops
        List<RadioButton> allButtons = new ArrayList<>(Arrays.asList(likert1, likert2, likert3, likert4, likert5));

        // Set the function of all buttons
        if (repEval.equals("")){
            for (int i = 0; i < 5; i++) {
                Integer val = i + 1;
                allButtons.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        repActivity.repEval = Integer.toString(val);
                    }
                });
            }
        }
        else{
            // Disable all buttons and tick the correct one
            likertGroup.clearCheck();
            for (int i = 0; i < 5; i++) {
                if (repEval.equals(Integer.toString(i + 1))) {
                    allButtons.get(i).setChecked(true); //tick
                }
                allButtons.get(i).setEnabled(false); // disable input
            }
        }
        return view;
    }
}