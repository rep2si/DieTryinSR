package com.codytross.dietryinsr;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.design.widget.BottomNavigationView;
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
        import java.io.FileWriter;
        import java.io.IOException;
        import java.nio.file.Files;
        import java.nio.file.Paths;

public class dg extends AppCompatActivity {

    // Initialise stuff
    public TextView txtDescription1,txtDescription2,game_id, game_id2, offer_label, endowment, endowment_label;
    private ImageView imgPreview1,imgPreview2;
    public Button btnLoad;
    public Button btnSave, buttonNext;
    public String personStamp, globalGameID, globalGameStamp, gameStamp, myJSONp, photoMode, photoNumber, entryMode, quietMode, gameOffer1, gameOffer2;
    int ticker, Ngames2, endowmentInt;
    public static final int BITMAP_SAMPLE_SIZE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout for dg
        setContentView(R.layout.activity_dg);

        // Defaults to 0
        ticker = 1;

        // get required elements from R[esources]
        txtDescription1 = findViewById(R.id.txt_desc1);
        txtDescription2 = findViewById(R.id.txt_desc2);
        imgPreview1 = findViewById(R.id.imgPreview1);
        imgPreview2 = findViewById(R.id.imgPreview2);
        btnLoad = findViewById(R.id.btnLoad);
        btnSave = findViewById(R.id.btnSave);
        game_id = findViewById(R.id.game_id);
        game_id2 = findViewById(R.id.game_id2);
        offer_label = findViewById(R.id.offer_label);
        endowment = findViewById(R.id.endowment);
        endowment_label = findViewById(R.id.endowment_label);
        buttonNext = findViewById(R.id.buttonNext);
        endowmentInt = getResources().getInteger(R.integer.endowmentInt);

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

        endowment.setText(Integer.toString(endowmentInt));

    }//end oncreate

    private void loadGame() {
        loadPlayer();
        buttonNext.setText(globalGameStamp);
    }

    private void saveOffer(){
        // calculate offer (gameOffer2) and remaining endowment (gameOffer1)
        // from details on screen
        File tryinDir = getExternalFilesDir(null);
        gameOffer2 = game_id2.getText().toString();
        int offer = Integer.parseInt(gameOffer2);
        int offer1 = endowmentInt - offer;
        gameOffer1 = Integer.toString(offer1);

        //Write these to JSON using Cody's code
        JsonParser parser = new JsonParser();

        //Creating JSONObject from String using parser
        JsonObject JSONObject1 = parser.parse(myJSONp).getAsJsonObject();

        JSONObject1.addProperty("Offer1", gameOffer1);
        JSONObject1.addProperty("Offer2", gameOffer2);

        gameStamp = globalGameStamp; //game_id.getText().toString();

        File filePath = new File(tryinDir.getPath() + File.separator
                +  "SubsetContributions" + File.separator + gameStamp + ".json");

        File plonk = new File(tryinDir.getPath() + File.separator
                + "plonk.json");

        try{
            // gson.toJson(JSONObject1, new FileWriter(filePath));
            String response = JSONObject1.toString();
            System.out.println(response);
            System.out.println(filePath.getAbsolutePath());
            // Note: this will fail miserably if write permissions are not set up correctly on the file
            BufferedWriter f = new BufferedWriter(new FileWriter(filePath));
            f.write(response);
            f.flush();
            f.close();

//            FileWriter fos = new FileWriter(filePath.getAbsolutePath());
//            fos.write(response);
//            fos.flush();
//            fos.close();
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

        try {
            // parsing JSON for player
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

            try {
                String myJSON = new String(Files.readAllBytes(Paths.get(filePath.getAbsolutePath())));
                System.out.println(myJSON);
                myJSONp = myJSON;
                //JSON Parser from Gson Library
                JsonParser parser = new JsonParser();
                JsonObject JSONObject1 = parser.parse(myJSON).getAsJsonObject();

                // We only need to update image 2 (the receiver in DG)
                imgPreview2.setVisibility(View.VISIBLE);
                //Creating JSONObject from String using parser
                String a2JsonString = JSONObject1.get("AID2").toString();
                String a2bJsonString = a2JsonString.substring(1, a2JsonString.length() - 1);
                File file2 = new File(getExternalFilesDir(null).getPath() + File.separator
                        + "StandardizedPhotos/" + a2bJsonString + "." + MainActivity.IMAGE_EXTENSION);
                Bitmap bitmap2 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file2.getAbsolutePath());
                imgPreview2.setImageBitmap(bitmap2);

                // get offer if already in json file
                String o2JsonString = JSONObject1.get("Offer2").toString();
                String o2bJsonString = o2JsonString.substring(1, o2JsonString.length() - 1);

                if (personStamp.equals(a2bJsonString)) {
                    game_id2.setEnabled(true);
                    if (photoMode.equals("onlyfocal")) {
                        game_id2.setHintTextColor(Color.parseColor("#006f94"));
                    }
                }
                if (!personStamp.equals(a2bJsonString)) {
                    if (photoMode.equals("onlyfocal")) {
                        game_id2.setEnabled(false);
                        game_id2.setHintTextColor(Color.parseColor("#a3abad"));
                    }
                }

                if (a2bJsonString.equals("BLANK")) {
                    game_id2.setEnabled(false);
                    game_id2.setHint(" ");
                }
                if (!o2bJsonString.equals("")) {
                    game_id2.setHint(o2bJsonString);
                    game_id2.setText(o2bJsonString);
                    if (entryMode.equals("permanent")) {
                        game_id2.setEnabled(false);
                    }
                }

                // Fixme: somehow signal invalid input to user (e.g. offer higher than endowment)

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
                        } catch (NumberFormatException nfe) {
                            endowment.setText(Integer.toString(endowmentInt));
                        }
                    }
                });

                // Button hide / show offer and what player keeps
                ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            game_id2.setVisibility(View.GONE);
                            offer_label.setVisibility(View.GONE);
                            endowment.setVisibility(View.GONE);
                            endowment_label.setVisibility(View.GONE);
                        } else {
                            game_id2.setVisibility(View.VISIBLE);
                            offer_label.setVisibility(View.VISIBLE);
                            endowment.setVisibility(View.VISIBLE);
                            endowment_label.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
// catch code from Cody
            catch (IOException e) {
                e.printStackTrace();

                txtDescription1.setVisibility(View.VISIBLE);
                imgPreview1.setVisibility(View.GONE);
                globalGameStamp = "NONE";
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();

            txtDescription1.setVisibility(View.VISIBLE);
            imgPreview1.setVisibility(View.GONE);
            globalGameStamp = "NONE";
        }
        catch (Exception e) {
            e.printStackTrace();
            txtDescription1.setVisibility(View.VISIBLE);
            imgPreview1.setVisibility(View.GONE);
            globalGameStamp = "NONE";
        }
    }
}

