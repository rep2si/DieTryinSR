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
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;


public class expectations extends MainActivity {

    public TextView txtDescription2, game_id, condition, conditionLabel;
    private ImageView imgPreview2;
    public Button btnLoad;
    public Button btnSave, btnNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, previousCondition = "", expectedAmt;
    public int ticker;
    public Boolean hasOptedOut = false, hasOptedIn = false, inOptOutView = false;
    private Boolean hideActualAllocation = false;
    private int Ngames;
    private long loadTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use same layout as dg
        setContentView(R.layout.activity_dg);

        // Defaults to 0
        ticker = 1;

        // get required elements from R[esources]
        txtDescription2 = findViewById(R.id.txt_desc2);
        imgPreview2 = findViewById(R.id.imgPreview2);
        btnLoad = findViewById(R.id.btnLoad);
        btnSave = findViewById(R.id.btnSave);
        game_id = findViewById(R.id.game_id); // NOT the offer text field!
        btnNext = findViewById(R.id.btnNext);
        condition = findViewById(R.id.condition);
        conditionLabel = findViewById(R.id.condition_label);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hideActualAllocation = extras.getBoolean("hideActualAllocation");
        }
        // Load button
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGame();
            }
        });

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
                }
                loadGame();
            }
        });
    } //end oncreate

    // Warn on back button
    public void onBackPressed() {
        warnBack();
    }

    private void warnBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Return to the main menu?");
        builder.setTitle("Main menu");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            ;
            finish();
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            ;
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadGame() {
        loadPlayer();
        btnNext.setText(globalGameStamp);
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

        // Reveal actual amount received
        TextView tvExpected = findViewById(R.id.expected);
        TextView tvReceived = findViewById(R.id.received);

        // disable input and reveal
        tvExpected.setEnabled(false);
        if(!hideActualAllocation) {
            tvReceived.setVisibility(View.VISIBLE); // comment out if want reveal at end only
        }

        // toggle buttons
        btnNext.setEnabled(true);
        btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorInactive));
    }

    private void recordGameResult(String gameStamp, String property, String value) {
        JsonObject jsonSettingsObj = null;
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + gameStamp + ".json"; // Hacky but fast
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + gameStamp + ".json"; // Hacky but fast
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + gameStamp + ".json"; // Hacky but fast
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
        personStamp = game_id.getText().toString();
        globalGameID = "GIDx" + ticker;

        // Load settings for this player
        // HERE
        gameStamp = getPlayerSetting(personStamp, globalGameID);
        globalGameStamp = gameStamp; // why do we need both??
        Ngames = Integer.parseInt(getPlayerSetting(personStamp, "Ngames"));

        // Load settings for game
        String opponentStamp = getGameSetting(gameStamp, "AID");
        String gameExpected = getGameSetting(gameStamp, "Expected");

        // Load game elements
        showImage(opponentStamp);
        condition.setVisibility(View.VISIBLE);

        Integer receivedInt = Integer.parseInt(getGameSetting(gameStamp, "Given"));

        // Fragment here

        Fragment frag = ExpectationsFragment.newInstance(gameExpected, receivedInt, hideActualAllocation);

        if(!gameExpected.equals("")){
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + "GIDsByPID" + "%2F" + personStamp + ".json"; // Hacky but fast
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
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetPayouts" + "%2F" + gameStamp + ".json"; // Hacky but fast
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
        builder.setMessage("Next condition: " + condition);
        builder.setTitle("Condition change!");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}


