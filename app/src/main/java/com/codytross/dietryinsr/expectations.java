package com.codytross.dietryinsr;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;


public class expectations extends MainActivity {

    public TextView txtDescription2, condition, tvGID;
    private ImageView imgPreview2;
    public Button btnSave, btnNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, expectedAmt;
    public int ticker;
    private String demoSetting = "";
    private int Ngames;
    private long loadTime;
    private String subDir;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use same layout as dg
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            demoSetting = extras.getString("demoSetting");
        }

        subDir = "SubsetExpectations";

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
                if(expectedAmt.equals("")){
                    //do nothing
                    return;
                }
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnNext.setEnabled(true);
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
    } //end oncreate

    // Warn on back button
    public void onBackPressed() {
        if (demoSetting.equals("true")) {
            finish(); // no alert if in demo view
        } else {
            warnBack();
        }
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
        loadPlayer();
    }

    private void saveOffer(){

        gameStamp = globalGameStamp;

        JsonObject gameJson = getGameJson(gameStamp);
        gameJson.addProperty("Expected", expectedAmt);
        gameJson.addProperty("loadTime", loadTime);
        gameJson.addProperty("saveTime", System.currentTimeMillis());
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String enumeratorId = sharedPref.getString(getString(R.string.enumIdString), "");
        gameJson.addProperty("RID", enumeratorId);

        // Offer fragment auto updates gameOffer values, so we can just proceed and write the file
        writeGameJson(gameStamp, gameJson);

        // disable input
        TextView tvExpected = findViewById(R.id.expected);
        tvExpected.setEnabled(false);

        // toggle buttons
        btnNext.setEnabled(true);
        btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
    }

    private JsonObject getGameJson(String gameStamp) {
        JsonObject jsonSettingsObj = null;
        String settingsUri = treeDoc.getUri().toString() + "%2F" + subDir + "%2F" + gameStamp + ".json"; // Hacky but fast
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + subDir + "%2F" + gameStamp + ".json"; // Hacky but fast
        String gameJsonString = gameJson.toString();
        try{
            writeTextToUri(Uri.parse(settingsUri), gameJsonString);
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

        String opponentStamp = "";
        String gameExpected = "";

        // Load settings for this player
        if (demoSetting.equals("true")) {
            // display current player
            opponentStamp = personStamp;
        } else {
            gameStamp = getPlayerSetting(personStamp, globalGameID);
            globalGameStamp = gameStamp; // why do we need both??
            Ngames = Integer.parseInt(getPlayerSetting(personStamp, "Ngames"));

            // Load settings for game
            opponentStamp = getGameSetting(gameStamp, "AID");
            gameExpected = getGameSetting(gameStamp, "Expected");
        }

        String enableClosedEyes = getGlobalSetting("enableClosedEyesExpectations");
        if (enableClosedEyes.equals("true")) {
            showImage(opponentStamp + "-closedEyes");
        } else {
            showImage(opponentStamp);
        }

        tvGID.setText(gameStamp);

        // Fragment here
        Fragment frag = ExpectationsFragment.newInstance(gameExpected);

        if (demoSetting.equals("true")) {
            // disable buttons entirely
            btnSave.setEnabled(false);
            btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
            btnNext.setEnabled(false);
            btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
        } else {

            if (!gameExpected.equals("")) {
                btnSave.setEnabled(false);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
                btnNext.setEnabled(true);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                btnSave.setEnabled(true);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnNext.setEnabled(false);
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorInactive));
                loadTime = System.currentTimeMillis();
            }
        }

        loadFragment(frag);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String getPlayerSetting(String personStamp, String setting) {
        String playerSetting = "";
        String settingsUri = treeDoc.getUri().toString() + "%2F" + subDir + "%2F" + "GIDsByPID" + "%2F" + personStamp + ".json"; // Hacky but fast
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + subDir + "%2F" + gameStamp + ".json"; // Hacky but fast
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


