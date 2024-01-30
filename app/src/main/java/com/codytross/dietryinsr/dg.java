package com.codytross.dietryinsr;

        import static android.app.PendingIntent.getActivity;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v4.provider.DocumentFile;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Button;

        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;

        import java.io.InputStream;

        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.app.FragmentTransaction;
        import android.net.Uri;

public class dg extends MainActivity {

    // Initialise stuff
    public TextView txtDescription2, condition, conditionLabel, tvGID;
    private ImageView imgPreview2;
    public Button btnSave, btnNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, previousCondition = "", gameOffer1, gameOffer2;
    public int ticker;
    public Boolean hasOptedOut = false, hasOptedIn = false, inOptOutView = false;
    private int Ngames;
    private long loadTime, saveTime;
    private View bgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout for dg
        setContentView(R.layout.activity_dg);

        // Defaults to 0
        ticker = 1;

        // get required elements from R[esources]
        txtDescription2 = findViewById(R.id.txt_desc2);
        imgPreview2 = findViewById(R.id.imgPreview2);
        btnSave = findViewById(R.id.btnSave);
        btnNext = findViewById(R.id.btnNext);
        condition = findViewById(R.id.condition);
        tvGID = findViewById(R.id.tvGID);
        bgView = findViewById(R.id.background);

        //apply translation
        btnNext.setText(i18nMap.get("btn_next"));
        btnSave.setText(i18nMap.get("btn_save"));

        // Load the correct player
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        personStamp = sharedPref.getString(getString(R.string.partIdString), "");
        loadGame();

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inOptOutView) {
                    if (gameOffer2.equals("")) {
                        return;
                    }
                    btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnNext.setEnabled(true);
                }
                saveOffer();
            }
        });

        // Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to next player or cycle back to first if last
                if (Ngames > ticker) {
                    ticker = ticker + 1;
//                    btnNext.setBackgroundColor(Color.parseColor("#808080"));
                    btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
                    btnNext.setEnabled(false);
                } else {
                    ticker = 1;
                    btnNext.setBackgroundColor(Color.parseColor("#610c04"));
                    alertComplete();
                }
                loadGame();
            }
        });

    }//end oncreate

    // Warn on back button
    public void onBackPressed() {
        warnBack();
    }


    private void warnBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i18nMap.get("message_mainMenu"));
        builder.setTitle(i18nMap.get("message_title_mainMenu"));
        builder.setCancelable(false);
        builder.setPositiveButton(i18nMap.get("btn_yes"), (DialogInterface.OnClickListener) (dialog, which) -> {;
            finish();
        });
        builder.setNegativeButton(i18nMap.get("btn_no"), (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadGame() {
        //reset opt in values
        hasOptedOut = false;
        hasOptedIn = false;
        loadPlayer();
    }
    private void saveOffer(){
        Boolean keepButtons = false;
        if(inOptOutView){
            if (!hasOptedOut & !hasOptedIn){
                // User has not chosen any option, do nothing.
                return;
            } else if (hasOptedIn) {
                inOptOutView = false;
                Integer endowmentInt = Integer.parseInt(getGameSetting(gameStamp, "Endowment"));
                Integer optOutKeepInt = Integer.parseInt(getGameSetting(gameStamp, "optOutKeep"));
                Fragment frag = OfferFragment.newInstance("", "hideOptOutBtn", "", endowmentInt, optOutKeepInt);
                loadFragment(frag);
                return;
            } else if(hasOptedOut) {
                // player gets opt out amount, opponent nothing
                Integer optOutKeepInt = Integer.parseInt(getGameSetting(gameStamp, "optOutKeep"));
                gameOffer1 = Integer.toString(optOutKeepInt); // unnecessarily doing string-int-string, could fix
                gameOffer2 = "0";
                // Freeze buttons
                Button btnOptOut = findViewById(R.id.btnOptOut);
                Button btnOptIn = findViewById(R.id.btnOptIn);
                btnOptIn.setEnabled(false);
                btnOptOut.setEnabled(false);
            }
            else {
                Log.e("idx","This option should never occur. Something is wrong with opt-in and out logic...");
                }
            }

        gameStamp = globalGameStamp; //game_id.getText().toString();

        JsonObject gameJson = getGameJson(gameStamp);
        gameJson.addProperty("amtKept", gameOffer1);
        gameJson.addProperty("amtGiven", gameOffer2);
        gameJson.addProperty("loadTime", loadTime);
        gameJson.addProperty("saveTime", System.currentTimeMillis());
        if(hasOptedOut){
            gameJson.addProperty("optedOut", "true");
        } else if(hasOptedIn){
            gameJson.addProperty("optedOut", "false");
        }
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String enumeratorId = sharedPref.getString(getString(R.string.enumIdString), "");
        gameJson.addProperty("RID", enumeratorId);
        // Offer fragment auto updates gameOffer values, so we can just proceed and write the file
        writeGameJson(gameStamp, gameJson);

        if(!inOptOutView) {
            TextView game_id2 = findViewById(R.id.game_id2);
            game_id2.setEnabled(false);
        }

        btnNext.setEnabled(true);
        btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));

        previousCondition = getGameSetting(gameStamp, "Condition");
    }

    private void recordGameResult(String gameStamp, String property, String value) {
//        DocumentFile settingsFile = treeDoc.findFile("SubsetContributions").findFile("GIDsByPID").findFile("settings.json"); // SLOOOOW
        JsonObject jsonSettingsObj = null;
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + gameStamp + ".json"; // Hacky but fast
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonSettingsObj.addProperty(property, value);
        try {
            writeTextToUri(Uri.parse(settingsUri), jsonSettingsObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonObject getGameJson(String gameStamp) {
        JsonObject jsonSettingsObj = null;
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + gameStamp + ".json"; // Hacky but fast
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonSettingsObj;
    }

    private void writeGameJson(String gameStamp, JsonObject gameJson) {
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + gameStamp + ".json"; // Hacky but fast
        String gameJsonString = gameJson.toString();
        try{
            writeTextToUri(Uri.parse(settingsUri), gameJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recordSingleGameResult(String gameStamp, String property, String value) {
//        DocumentFile settingsFile = treeDoc.findFile("SubsetContributions").findFile("GIDsByPID").findFile("settings.json"); // SLOOOOW
        JsonObject jsonSettingsObj = null;
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + gameStamp + ".json"; // Hacky but fast
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonSettingsObj.addProperty(property, value);
        try {
            writeTextToUri(Uri.parse(settingsUri),  jsonSettingsObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlayer() {
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
        // hide text and show image
        txtDescription2.setVisibility(View.GONE);
        imgPreview2.setVisibility(View.VISIBLE);

        // get the person ID from text
        globalGameID = "GIDx" + ticker;

        // Load settings for this player
        gameStamp = getPlayerSetting(personStamp, globalGameID);
        globalGameStamp = gameStamp; // why do we need both??
        Ngames = Integer.parseInt(getPlayerSetting(personStamp, "Ngames"));

        // Load settings for game
        String opponentStamp = getGameSetting(gameStamp, "AID");
        String gameCondition = getGameSetting(gameStamp, "Condition");
        String gameOffer = getGameSetting(gameStamp, "amtGiven");
        String askOptOut = getGameSetting(gameStamp, "askOptOut");

       // Record loading time if needed
        if(gameOffer.equals("")) {
            loadTime = System.currentTimeMillis();
        }

        // Alert condition change if appropriate
        if (gameOffer.equals("") && !gameCondition.equals(previousCondition)) {
            alertCondition(gameCondition);
        }

        // Load game elements
        String enableClosedEyes = getGlobalSetting("enableClosedEyes");
        String gameConditionLetter;
        String bgColor;
        switch (gameCondition) {
            case "anonymous":
                if (enableClosedEyes.equals("true")) {
                    showImage(opponentStamp + "-closedEyes");
                } else {
                    showImage(opponentStamp);
                }
                gameConditionLetter = i18nMap.get("alloc_condAnon_singleletter");
                bgColor = getGlobalSetting("bgAnonymous");
                break;
            case "revealed":
                showImage(opponentStamp);
                gameConditionLetter = i18nMap.get("alloc_condRevealed_singleletter");
                bgColor = getGlobalSetting("bgRevealed");
                break;
            default:
                showImage(opponentStamp);
                gameConditionLetter = "Game condition unknown";
                bgColor = "#ffffff";
                break;
        }
        condition.setText(gameConditionLetter);
        tvGID.setText(gameStamp);
        bgView.setBackgroundColor(Color.parseColor(bgColor));

        // second part of condition to switch to offer frag if recorded data and has opted in
        if (askOptOut.equals("true") && !getGameSetting(gameStamp, "optedOut").equals("false")) {
            inOptOutView = true;
            Integer endowmentInt = Integer.parseInt(getGameSetting(gameStamp, "Endowment"));
            Integer optOutKeepInt = Integer.parseInt(getGameSetting(gameStamp, "optOutKeep"));
            Fragment frag = OptOutFragment.newInstance(gameOffer, endowmentInt, optOutKeepInt);
            loadFragment(frag);
            // Deal with buttons
            if(!gameOffer.equals("")){
                btnSave.setEnabled(false);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
                btnNext.setEnabled(true);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                btnSave.setEnabled(true);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnNext.setEnabled(false);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
            }
        } else { //askOptout is "false"
            inOptOutView = false;
            Integer endowmentInt = Integer.parseInt(getGameSetting(gameStamp, "Endowment"));
            Integer optOutKeepInt = Integer.parseInt(getGameSetting(gameStamp, "optOutKeep"));
            String recordedOptOut = getGameSetting(gameStamp, "optedOut");
            String recordedAskOptOut = getGameSetting(gameStamp, "askOptOut");
            Fragment frag = OfferFragment.newInstance(gameOffer, recordedOptOut, recordedAskOptOut, endowmentInt, optOutKeepInt);
            if(!gameOffer.equals("")){
                btnSave.setEnabled(false);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
                btnNext.setEnabled(true);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                btnSave.setEnabled(true);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnNext.setEnabled(false);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
            }
            loadFragment(frag);
        }
    }

    private void showImage(String opponentStamp) {
        txtDescription2.setVisibility(View.GONE);
        imgPreview2.setVisibility(View.VISIBLE);
        String imageUriSt = treeDoc.getUri().toString() + "%2F" + "StandardizedPhotos" + "%2F" + opponentStamp + ".jpg"; // Hacky but fast
        Uri imageUri = Uri.parse(imageUriSt);
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Do not load in memory, just get dimensions of image
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();
            // Calculate inSampleSize
            int reqWidth = 1000; //super ugly to hard code this in, but can't figure out imgview getHeight method
            int reqHeight = 2000; //super ugly to hard code this in
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            InputStream newimageStream = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(newimageStream, null, options);
            newimageStream.close();
            imgPreview2.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getPlayerSetting(String personStamp, String setting) {
        String playerSetting = "";
//        DocumentFile settingsFile = treeDoc.findFile("SubsetContributions").findFile("GIDsByPID").findFile("settings.json"); // SLOOOOW
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + "GIDsByPID" + "%2F" + personStamp + ".json"; // Hacky but fast
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
//        DocumentFile settingsFile = treeDoc.findFile("SubsetContributions").findFile("GIDsByPID").findFile("settings.json"); // SLOOOOW
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + gameStamp + ".json"; // Hacky but fast
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

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.flDecision, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    private void alertCondition(String condition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18nMap.get("message_condChange"));
        String conditionString = null;
        if (condition.equals("revealed")) {
            conditionString = i18nMap.get("alloc_condRevealed");
        } else if (condition.equals("anonymous")) {
            conditionString = i18nMap.get("alloc_condAnon");
        }
        builder.setMessage(i18nMap.get("message_condChange") + ": " + conditionString);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertComplete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18nMap.get("message_title_complete"));
        builder.setMessage(i18nMap.get("message_complete"));
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
