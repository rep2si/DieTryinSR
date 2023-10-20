package com.codytross.dietryinsr;

        import static android.app.PendingIntent.getActivity;

        import android.app.Activity;
        import android.content.ContentResolver;
        import android.content.Intent;
        import android.content.UriPermission;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.support.v4.provider.DocumentFile;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Button;

        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.util.List;

        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.app.FragmentTransaction;
        import android.net.Uri;
        import android.content.SharedPreferences;

        import org.w3c.dom.Document;

public class dg extends MainActivity {

    // Initialise stuff
    public TextView txtDescription1, txtDescription2, game_id;
    private ImageView imgPreview2;
    public Button btnLoad;
    public Button btnSave, buttonNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, myJSONp, photoMode, photoNumber, entryMode, quietMode, gameOffer1, gameOffer2;
    public int ticker, Ngames2, optOutInt;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    public Boolean hasOptedOut = false, hasOptedIn = false, inOptOutView = false;

    public Uri test;
    private String Ngames;

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
        btnLoad = findViewById(R.id.btnLoad);
        btnSave = findViewById(R.id.btnSave);
        game_id = findViewById(R.id.game_id); // NOT the offer text field!
        buttonNext = findViewById(R.id.buttonNext);
        optOutInt = getResources().getInteger(R.integer.optOutInt);

        // Load button
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGame();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNext.setBackgroundColor(Color.parseColor("#5396ac"));
                buttonNext.setEnabled(true);
                saveOffer();
            }
        });

//        buttonNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // move to next player or cycle back to first if last
//                if (Ngames > ticker) {
//                    ticker = ticker + 1;
//                    buttonNext.setBackgroundColor(Color.parseColor("#808080"));
//                    buttonNext.setEnabled(false);
//                } else {
//                    ticker = 1;
//                    buttonNext.setBackgroundColor(Color.parseColor("#610c04"));
//                }
//                loadGame();
//            }
//        });

        // Missing button hide

//        // Get file paths
//        File tryinDir = getExternalFilesDir(null);
//
//        File filePathIntro = new File(tryinDir.getPath() + File.separator
//                + "SubsetContributions/GIDsByPID/" + "settings.json");

//        File filePathBlank = new File(tryinDir.getPath() + File.separator
//                + "SubsetContributions/GIDsByPID/" + "BLANK.jpg");
//        File filePathBlank2 = new File(tryinDir.getPath() + File.separator
//                + "StandardizedPhotos/"+ "BLANK.jpg");


        // get some settings from settings.json
//        try {
//            String myJSONsett = new String(Files.readAllBytes(Paths.get(filePathIntro.getAbsolutePath())));
//            System.out.println(myJSONsett);
//            //JSON Parser from Gson Library
//            JsonParser parserS = new JsonParser();
//            JsonObject JSONObject1p = parserS.parse(myJSONsett).getAsJsonObject();
//
//            String photoNumberb = JSONObject1p.get("photoNumber").toString();
//            photoNumber = photoNumberb.substring(1, photoNumberb.length() - 1);
//
//            String entryModeb = JSONObject1p.get("entryMode").toString();
//            entryMode = entryModeb.substring(1, entryModeb.length() - 1);
//
//            String photoModeb = JSONObject1p.get("photoMode").toString();
//            photoMode = photoModeb.substring(1, photoModeb.length() - 1);
//
//            String quietModeb = JSONObject1p.get("quietMode").toString();
//            quietMode = quietModeb.substring(1, quietModeb.length() - 1);
//
//            System.out.println(photoMode);
//
//            buttonNext.setBackgroundColor(Color.parseColor("#808080"));
//            buttonNext.setEnabled(false);
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //CRASHES HERE--- need to move this stuff to the fragment, probably
//        endowment.setText(Integer.toString(endowmentInt));

    }//end oncreate


    private void loadGame() {
        //reset opt in values
        hasOptedOut = false;
        hasOptedIn = false;
        loadPlayer();
        buttonNext.setText(globalGameStamp);
    }

    private void saveOffer(){
        if(inOptOutView){
            if (!hasOptedOut & !hasOptedIn){
                // User has not chosen any option, do nothing.
                return;
            } else if (hasOptedIn) {
                inOptOutView = false;
                loadFragment(new OfferFragment());
            } else if(hasOptedOut) {
                // player gets opt out amoun, opponent nothing
                gameOffer1 = Integer.toString(optOutInt);
                gameOffer2 = "0";
            }
            else {
                Log.e("idx","This option should never occur. Something is wrong with opt-in and out logic...");
                }
            }

        gameStamp = globalGameStamp; //game_id.getText().toString();

        JsonObject gameJson = getGameJson(gameStamp);
        gameJson.addProperty("Offer1", gameOffer1);
        gameJson.addProperty("Offer2", gameOffer2);
        if(hasOptedOut){
            gameJson.addProperty("OptedOut", "true");
        } else if(hasOptedIn){
            gameJson.addProperty("OptedOut", "false");
        }
        // Offer fragment auto updates gameOffer values, so we can just proceed and write the file
        writeGameJson(gameStamp, gameJson);


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
        // hide text and show image
        txtDescription2.setVisibility(View.GONE);
        imgPreview2.setVisibility(View.VISIBLE);

        // get the person ID from text
        personStamp = game_id.getText().toString();
        globalGameID = "GIDx" + ticker;

        // Load settings for this player
        gameStamp = getPlayerSetting(personStamp, globalGameID);
        globalGameStamp = gameStamp; // why do we need both??
        Ngames = getPlayerSetting(personStamp, "Ngames");

        // Load settings for game
        String opponentStamp = getGameSetting(gameStamp, "AID2");
        String gameCondition = getGameSetting(gameStamp, "Condition");
        String gameOffer = getGameSetting(gameStamp, "Offer2");

        // Load game elements
        showImage(opponentStamp);
        if (gameCondition.equals("optin")) {
            inOptOutView = true;
            loadFragment(new OptOutFragment());
        } else {
            loadFragment(new OfferFragment());
        }
    }


//
//                // Needs to happen after fragment is loaded
//
////                if (personStamp.equals(a2bJsonString)) {
////                    game_id2.setEnabled(true);
////                    if (photoMode.equals("onlyfocal")) {
////                        game_id2.setHintTextColor(Color.parseColor("#006f94"));
////                    }
////                }
////                if (!personStamp.equals(a2bJsonString)) {
////                    if (photoMode.equals("onlyfocal")) {
////                        game_id2.setEnabled(false);
////                        game_id2.setHintTextColor(Color.parseColor("#a3abad"));
////                    }
////                }
////
////                if (a2bJsonString.equals("BLANK")) {
////                    game_id2.setEnabled(false);
////                    game_id2.setHint(" ");
////                }
////                if (!o2bJsonString.equals("")) {
////                    game_id2.setHint(o2bJsonString);
////                    game_id2.setText(o2bJsonString);
////                    if (entryMode.equals("permanent")) {
////                        game_id2.setEnabled(false);
////                    }
////                }


//                // Button hide / show offer and what player keeps
//                ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
//                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            game_id2.setVisibility(View.GONE);
//                            offer_label.setVisibility(View.GONE);
//                            endowment.setVisibility(View.GONE);
//                            endowment_label.setVisibility(View.GONE);
//                        } else {
//                            game_id2.setVisibility(View.VISIBLE);
//                            offer_label.setVisibility(View.VISIBLE);
//                            endowment.setVisibility(View.VISIBLE);
//                            endowment_label.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//            }
// catch code from Cody
//            catch (IOException e) {
//                e.printStackTrace();
//
//                txtDescription1.setVisibility(View.VISIBLE);
//                globalGameStamp = "NONE";
//            }
//        }
//        catch (NullPointerException e) {
//            e.printStackTrace();
//
//            txtDescription1.setVisibility(View.VISIBLE);
//            globalGameStamp = "NONE";
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            txtDescription1.setVisibility(View.VISIBLE);
//            globalGameStamp = "NONE";
//        }

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

    private String getGameSetting(String gameStamp, String setting) {
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


}
