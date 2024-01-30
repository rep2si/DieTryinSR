package com.codytross.dietryinsr;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class payout extends MainActivity {

    // Initialise stuff
    private TextView nReceived, nKept, nExpecTested, nExpecAccurate, totReceived, totKept, totExpecAccurate, totGrand, guessExplanation;
    private TextView nReceivedLabel, nKeptLabel, nExpecTestedLabel, nExpecAccurateLablel, totReceivedLabel, totKeptLabel, totExpecAccurateLabel, totGrandLabel;
    private String personStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // layout
        setContentView(R.layout.activity_payout);

        // get required elements from R[esources]
        nReceived        = findViewById(R.id.n_received);
        nKept            = findViewById(R.id.n_kept);
        nExpecTested     = findViewById(R.id.n_expectations_tested);
        nExpecAccurate   = findViewById(R.id.n_expectations_successful);
        totReceived      = findViewById(R.id.tot_received);
        totKept          = findViewById(R.id.total_kept);
        totGrand         = findViewById(R.id.grand_total);
        totExpecAccurate = findViewById(R.id.total_accurate_guesses);
        guessExplanation = findViewById(R.id.gess_explanation);


        nReceivedLabel        = findViewById(R.id.n_received_label);
        nKeptLabel            = findViewById(R.id.n_kept_label);
        nExpecTestedLabel     = findViewById(R.id.n_expectations_tested_label);
        nExpecAccurateLablel  = findViewById(R.id.n_expectations_successful_label);
        totReceivedLabel      = findViewById(R.id.tot_received_label);
        totKeptLabel          = findViewById(R.id.total_kept_label);
        totExpecAccurateLabel = findViewById(R.id.total_accurate_guesses_label);
        totGrandLabel         = findViewById(R.id.grand_total_label);


        nReceivedLabel.setText(i18nMap.get("payout_inEdgesPayout"));
        nKeptLabel.setText(i18nMap.get("payout_outEdgesPayout"));
        nExpecTestedLabel.setText(i18nMap.get("payout_accuracyTested"));
        nExpecAccurateLablel.setText(i18nMap.get("payout_accurateExpectations"));
        totReceivedLabel.setText(i18nMap.get("payout_totalReceived"));
        totKeptLabel.setText(i18nMap.get("payout_totalKept"));
        totExpecAccurateLabel.setText(i18nMap.get("payout_totalAccurateExpectations"));
        totGrandLabel.setText(i18nMap.get("payout_grandTotal"));


        // Load the correct player
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        personStamp = sharedPref.getString(getString(R.string.partIdString), "");
        loadGame();

    }

    private void loadGame() {
        Integer m = Integer.parseInt(getPlayerSetting(personStamp, "guessMargin"));
        // Check for accurate Expectations only if player has corresponding GIDsByPID
        Integer accurateExpectations;
        if (checkConfigFile("SubsetExpectations%2FGIDsByPID", personStamp)) {
            accurateExpectations = getAccurateExpectations(personStamp, m);
            nExpecAccurate.setText(Integer.toString(accurateExpectations));
            nExpecTested.setText(getPlayerSetting(personStamp, "Ngames", true));
        } else {
            accurateExpectations = 0;
            nExpecAccurate.setText("NA");
            nExpecTested.setText("0");
        }
        nReceived.setText(getPlayerSetting(personStamp, "nReceived"));
        nKept.setText(getPlayerSetting(personStamp, "nKept"));
        Integer received = Integer.parseInt(getPlayerSetting(personStamp, "amtReceived"));
        Integer kept = Integer.parseInt(getPlayerSetting(personStamp, "amtKept"));
        Integer expectationsBonusPerGuess = Integer.parseInt(getPlayerSetting(personStamp, "guessPayout"));
        Integer expectationsBonusTotal = accurateExpectations * expectationsBonusPerGuess;
        totExpecAccurate.setText(Integer.toString(expectationsBonusTotal));
        Integer grandTot = received + kept + expectationsBonusTotal ;
        totReceived.setText(Integer.toString(received));
        totKept.setText(Integer.toString(kept));
        totGrand.setText(Integer.toString(grandTot));
        String accuracyMarginReminder = i18nMap.get("payout_accuracyMarginReminder") + ": ";
        String accuracyRewardReminder = i18nMap.get("payout_accuracyRewardReminder") + ": ";
        guessExplanation.setText(
                accuracyMarginReminder + Integer.toString(m) + "\n\n" + accuracyRewardReminder + Integer.toString(expectationsBonusPerGuess)
        );
    }

    private Integer getAccurateExpectations(String personStamp, Integer margin) {
        Integer accurateCount = 0;
        Integer ticker = 1;
        Integer nGames = Integer.parseInt(getPlayerSetting(personStamp, "Ngames", true));
        while (ticker <= nGames) {
            String gameStamp = getPlayerSetting(personStamp, "GIDx" + Integer.toString(ticker), true);
            String expected = getGameSetting(gameStamp, "Expected");
            String received = getGameSetting(gameStamp, "Given");
            if (!expected.equals("") && !received.equals("")) {
                Integer expectedAmt = Integer.parseInt(expected);
                Integer receivedAmt = Integer.parseInt(received);
                if (Math.abs(expectedAmt - receivedAmt) <= margin) {
                    accurateCount++;
                }
            }
            ticker++;
        }
        return(accurateCount);
    }

    private String getPlayerSetting(String personStamp, String setting) {
       return(this.getPlayerSetting(personStamp, setting, false));
    }

    private String getPlayerSetting(String personStamp, String setting, Boolean expec) {
        String playerSetting = "";
        String settingsUri;
        if (expec) {
            settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetExpectations" + "%2F" + "GIDsByPID" + "%2F" + personStamp + ".json"; // Hacky but fast
        } else {
            settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + personStamp + ".json"; // Hacky but fast
        };
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            JsonObject jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
            playerSetting = jsonSettingsObj.get(setting).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  playerSetting;
    }

    public String getGameSetting(String gameStamp, String setting) {
        String gameSetting = "";
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetExpectations" + "%2F" + gameStamp + ".json"; // Hacky but fast
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            JsonObject jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
            gameSetting = jsonSettingsObj.get(setting).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  gameSetting;
    }

}
