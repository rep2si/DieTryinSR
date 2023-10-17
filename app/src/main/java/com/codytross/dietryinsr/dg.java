package com.codytross.dietryinsr;

        import static android.app.PendingIntent.getActivity;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.ParcelFileDescriptor;
        import android.provider.DocumentsContract;
        import android.support.design.widget.BottomNavigationView;
        import android.support.v4.provider.DocumentFile;
        import android.support.v7.app.AppCompatActivity;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.View;
        import android.widget.CompoundButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Button;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;
        import androidx.navigation.ui.AppBarConfiguration;
        import androidx.navigation.ui.NavigationUI;

        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.nio.file.Files;
        import java.nio.file.Paths;

        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.app.FragmentTransaction;
        import android.net.Uri;
        import android.content.ContentResolver;

public class dg extends AppCompatActivity {

    // Initialise stuff
    public TextView txtDescription1, txtDescription2, game_id;
    private ImageView imgPreview2;
    public Button btnLoad;
    public Button btnSave, buttonNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, myJSONp, photoMode, photoNumber, entryMode, quietMode, gameOffer1, gameOffer2;
    public int ticker, Ngames2, optOutInt;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    public Boolean hasOptedOut = false, hasOptedIn = false, inOptOutView = false;
    public Uri testDir;

    public Uri test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout for dg
        setContentView(R.layout.activity_dg);
//        loadFragment(new OfferFragment());

        openDirectory(test);



        // Defaults to 0
        ticker = 1;

        // get required elements from R[esources]
//        txtDescription1 = findViewById(R.id.txt_desc1);
        txtDescription2 = findViewById(R.id.txt_desc2);
//        imgPreview1 = findViewById(R.id.imgPreview1);
        imgPreview2 = findViewById(R.id.imgPreview2);
        btnLoad = findViewById(R.id.btnLoad);
        btnSave = findViewById(R.id.btnSave);
        game_id = findViewById(R.id.game_id); // NOT the offer text field!
//        optOutAmount = findViewById(R.id.optout_amount);
        buttonNext = findViewById(R.id.buttonNext);
//        endowmentInt = getResources().getInteger(R.integer.endowmentInt);
        optOutInt = getResources().getInteger(R.integer.optOutInt);


        // Get file paths
        File tryinDir = getExternalFilesDir(null);

        File filePathIntro = new File(tryinDir.getPath() + File.separator
                + "SubsetContributions/GIDsByPID/" + "settings.json");

//        File filePathBlank = new File(tryinDir.getPath() + File.separator
//                + "SubsetContributions/GIDsByPID/" + "BLANK.jpg");
//        File filePathBlank2 = new File(tryinDir.getPath() + File.separator
//                + "StandardizedPhotos/"+ "BLANK.jpg");


        // get some settings from settings.json
        try {
            String myJSONsett = new String(Files.readAllBytes(Paths.get(filePathIntro.getAbsolutePath())));
            System.out.println(myJSONsett);
            //JSON Parser from Gson Library
            JsonParser parserS = new JsonParser();
            JsonObject JSONObject1p = parserS.parse(myJSONsett).getAsJsonObject();

            String photoNumberb = JSONObject1p.get("photoNumber").toString();
            photoNumber = photoNumberb.substring(1, photoNumberb.length() - 1);

            String entryModeb = JSONObject1p.get("entryMode").toString();
            entryMode = entryModeb.substring(1, entryModeb.length() - 1);

            String photoModeb = JSONObject1p.get("photoMode").toString();
            photoMode = photoModeb.substring(1, photoModeb.length() - 1);

            String quietModeb = JSONObject1p.get("quietMode").toString();
            quietMode = quietModeb.substring(1, quietModeb.length() - 1);

            System.out.println(photoMode);

            buttonNext.setBackgroundColor(Color.parseColor("#808080"));
            buttonNext.setEnabled(false);

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

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // move to next player or cycle back to first if last
                    if (Ngames2 > ticker) {
                        ticker = ticker + 1;
                        buttonNext.setBackgroundColor(Color.parseColor("#808080"));
                        buttonNext.setEnabled(false);
                    } else {
                        ticker = 1;
                        buttonNext.setBackgroundColor(Color.parseColor("#610c04"));
                    }
                    loadGame();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        // update endowment with actual value
        // FIXME: for now getting this from xml, but should be in json

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

        File tryinDir = getExternalFilesDir(null);

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
                System.out.println("This option should never occur. Something is wrong with opt-in and out logic...");
                }
            }

        JsonParser parser = new JsonParser();

        //Creating JSONObject from String using parser
        JsonObject JSONObject1 = parser.parse(myJSONp).getAsJsonObject();

        JSONObject1.addProperty("Offer1", gameOffer1);
        JSONObject1.addProperty("Offer2", gameOffer2);
        if(hasOptedOut){
            JSONObject1.addProperty("OptedOut", "true");
        } else if(hasOptedIn){
            JSONObject1.addProperty("OptedOut", "false");
        }

        gameStamp = globalGameStamp; //game_id.getText().toString();

        File filePath = new File(tryinDir.getPath() + File.separator
                +  "SubsetContributions" + File.separator + gameStamp + ".json");

        try{
            // gson.toJson(JSONObject1, new FileWriter(filePath));
            String response = JSONObject1.toString();
            System.out.println(response);
            System.out.println(filePath.getAbsolutePath());
            // Note: this will fail miserably if write permissions are not set up correctly (chmod)
            BufferedWriter f = new BufferedWriter(new FileWriter(filePath));
            f.write(response);
            f.flush();
            f.close();

        }catch(IOException e){
            e.printStackTrace();
            System.out.println("error");
        }

//        // I don't get why Cody did this...
//        loadPlayer();

    }

    private void loadPlayer() {
        // hide text and show image
        txtDescription2.setVisibility(View.GONE);
        imgPreview2.setVisibility(View.VISIBLE);

        // get the person ID from text
        personStamp = game_id.getText().toString();

        // get path to
        File filePath2 = new File(getExternalFilesDir(null).getPath() + File.separator
                +  "SubsetContributions/GIDsByPID/" + personStamp + ".json");

        // TODO: refactor these into external functions, would be more elegant
        // parsing JSON for player
        try {
            String myJSONpid = new String(Files.readAllBytes(Paths.get(filePath2.getAbsolutePath())));
            System.out.println(myJSONpid);
            //JSON Parser from Gson Library
            JsonParser parserP = new JsonParser();
            JsonObject JSONObject1p = parserP.parse(myJSONpid).getAsJsonObject();

            globalGameID = "GIDx" + ticker;

            String GIDxString = JSONObject1p.get(globalGameID).toString();
            String GIDxString2 = GIDxString.substring(1, GIDxString.length() - 1);

            String Ngames = JSONObject1p.get("Ngames").toString();
            Ngames2 = Integer.valueOf(Ngames.substring(1, Ngames.length() - 1));

            System.out.println(GIDxString2);
            System.out.println(Ngames2);

            gameStamp = GIDxString2; //game_id.getText().toString();
            globalGameStamp = gameStamp;

            // get file for game stamp
            File filePath = new File(getExternalFilesDir(null).getPath() + File.separator
                    + "SubsetContributions/" + gameStamp + ".json");

            // parsing JSON for this game iteration and set photograph + existing offers
            try {
                String myJSON = new String(Files.readAllBytes(Paths.get(filePath.getAbsolutePath())));
                System.out.println(myJSON);
                myJSONp = myJSON;
                //JSON Parser from Gson Library
                JsonParser parser = new JsonParser();
                JsonObject JSONObject1 = parser.parse(myJSON).getAsJsonObject();

                // We only need to update image 2 (the receiver in DG)
                imgPreview2.setVisibility(View.VISIBLE);
                // Creating JSONObject from String using parser
                // Get receiver details
                String a2JsonString = JSONObject1.get("AID2").toString();
                String a2bJsonString = a2JsonString.substring(1, a2JsonString.length() - 1);
                File file2 = new File(getExternalFilesDir(null).getPath() + File.separator
                        + "StandardizedPhotos/" + a2bJsonString + "." + MainActivity.IMAGE_EXTENSION);
                Bitmap bitmap2 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file2.getAbsolutePath());
                imgPreview2.setImageBitmap(bitmap2);

                // get condition
                String gameCondition = JSONObject1.get("Condition").toString();
                String gameCondition2 = gameCondition.substring(1, gameCondition.length() - 1);

                // get offer if already in json file
                String o2JsonString = JSONObject1.get("Offer2").toString();
                String o2bJsonString = o2JsonString.substring(1, o2JsonString.length() - 1);

                // Needs to happen after fragment is loaded

//                if (personStamp.equals(a2bJsonString)) {
//                    game_id2.setEnabled(true);
//                    if (photoMode.equals("onlyfocal")) {
//                        game_id2.setHintTextColor(Color.parseColor("#006f94"));
//                    }
//                }
//                if (!personStamp.equals(a2bJsonString)) {
//                    if (photoMode.equals("onlyfocal")) {
//                        game_id2.setEnabled(false);
//                        game_id2.setHintTextColor(Color.parseColor("#a3abad"));
//                    }
//                }
//
//                if (a2bJsonString.equals("BLANK")) {
//                    game_id2.setEnabled(false);
//                    game_id2.setHint(" ");
//                }
//                if (!o2bJsonString.equals("")) {
//                    game_id2.setHint(o2bJsonString);
//                    game_id2.setText(o2bJsonString);
//                    if (entryMode.equals("permanent")) {
//                        game_id2.setEnabled(false);
//                    }
//                }

                // Load fragment if game condition is forced
                if (gameCondition2.equals("revealed") || gameCondition2.equals("anonymous")){
                    loadFragment(new OfferFragment());
                }

                // Run opt-in step if appropriate
                if (gameCondition2.equals("optin")) {
                    inOptOutView = true;
                    loadFragment(new OptOutFragment());
                }


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
            }
// catch code from Cody
            catch (IOException e) {
                e.printStackTrace();

                txtDescription1.setVisibility(View.VISIBLE);
                globalGameStamp = "NONE";
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();

            txtDescription1.setVisibility(View.VISIBLE);
            globalGameStamp = "NONE";
        }
        catch (Exception e) {
            e.printStackTrace();
            txtDescription1.setVisibility(View.VISIBLE);
            globalGameStamp = "NONE";
        }
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

    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, 42);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == 42
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                DocumentFile testDir2 = DocumentFile.fromTreeUri(this, uri);
                testDir2.createFile("txt/plain", "sausage.txt"); // application/json

            }
        }
    }

}



