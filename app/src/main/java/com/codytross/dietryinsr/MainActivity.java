package com.codytross.dietryinsr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import android.graphics.Color;
import android.widget.LinearLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;

import java.io.File;
import java.util.List;


import java.io.FileWriter;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import com.codytross.dietryinsr.R;

public class MainActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "RICH";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    private static String imageStoragePath;

    public TextView txtDescription1, txtDescription2, txtDescription3,txtDescription4,txtDescription5,txtDescription6,txtDescription7,txtDescription8,txtDescription9, person_id, person_id_new, game_id;
    private ImageView imgPreview1, imgPreview2, imgPreview3, imgPreview4, imgPreview5, imgPreview6, imgPreview7, imgPreview8, imgPreview9;
    private VideoView videoPreview;
    public Button btnCapturePicture, btnCapturePictureB, buttonNext;
    public TextView game_id1,game_id2,game_id3,game_id4,game_id5,game_id6,game_id7,game_id8,game_id9; 
    public String gameOffer1, gameOffer2, gameOffer3, gameOffer4, gameOffer5, gameOffer6, gameOffer7, gameOffer8, gameOffer9;

   // public JsonObject JSONObject1;
    public String gameStamp, personStamp, globalGameStamp, globalGameID, photoMode, photoNumber, entryMode, quietMode;
    public String myJSONp;

    public LinearLayout strip2, strip2b, strip3, strip3b;

    int ticker, Ngames2;

    // Checks if a volume containing external storage is available
    // for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ticker = 1;

        txtDescription1 = findViewById(R.id.txt_desc1);
        txtDescription2 = findViewById(R.id.txt_desc2);
        txtDescription3 = findViewById(R.id.txt_desc3);
        txtDescription4 = findViewById(R.id.txt_desc4);
        txtDescription5 = findViewById(R.id.txt_desc5);
        txtDescription6 = findViewById(R.id.txt_desc6);
        txtDescription7 = findViewById(R.id.txt_desc7);
        txtDescription8 = findViewById(R.id.txt_desc8);
        txtDescription9 = findViewById(R.id.txt_desc9);

        imgPreview1 = findViewById(R.id.imgPreview1);
        imgPreview2 = findViewById(R.id.imgPreview2);
        imgPreview3 = findViewById(R.id.imgPreview3);
        imgPreview4 = findViewById(R.id.imgPreview4);
        imgPreview5 = findViewById(R.id.imgPreview5);
        imgPreview6 = findViewById(R.id.imgPreview6);
        imgPreview7 = findViewById(R.id.imgPreview7);
        imgPreview8 = findViewById(R.id.imgPreview8);
        imgPreview9 = findViewById(R.id.imgPreview9);


        videoPreview = findViewById(R.id.videoPreview);
        btnCapturePicture = findViewById(R.id.btnCapturePicture);
        btnCapturePictureB = findViewById(R.id.btnCapturePictureB);
        buttonNext = findViewById(R.id.buttonNext);

        person_id = findViewById(R.id.person_id);
        person_id_new = findViewById(R.id.person_id_new);
        game_id = findViewById(R.id.game_id);

        game_id1 = findViewById(R.id.game_id1);
        game_id2 = findViewById(R.id.game_id2);
        game_id3 = findViewById(R.id.game_id3);
        game_id4 = findViewById(R.id.game_id4);
        game_id5 = findViewById(R.id.game_id5);
        game_id6 = findViewById(R.id.game_id6);
        game_id7 = findViewById(R.id.game_id7);
        game_id8 = findViewById(R.id.game_id8);
        game_id9 = findViewById(R.id.game_id9);

        strip2 = findViewById(R.id.strip2);
        strip2b = findViewById(R.id.strip2b);

        strip3 = findViewById(R.id.strip3);
        strip3b = findViewById(R.id.strip3b);




        File tryinDir = getExternalFilesDir(null);

        File filePathIntro = new File(tryinDir.getPath() + File.separator
                + "SubsetContributions/GIDsByPID/" + "settings.json");

        File filePathBlank = new File(tryinDir.getPath() + File.separator
                + "SubsetContributions/GIDsByPID/" + "BLANK.jpg");
        File filePathBlank2 = new File(tryinDir.getPath() + File.separator
                + "StandardizedPhotos/"+ "BLANK.jpg");



        try {
            Files.copy(filePathBlank.toPath(), filePathBlank2.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            if (photoNumber.equals("oneimage")) {
                strip2.setVisibility(View.GONE);
                strip2b.setVisibility(View.GONE);

                strip3.setVisibility(View.GONE);
                strip3b.setVisibility(View.GONE);

                imgPreview3.setVisibility(View.GONE);
                game_id3.setVisibility(View.GONE);
                txtDescription3.setVisibility(View.GONE);
            }

            if (photoNumber.equals("twolines")) {
                strip3.setVisibility(View.GONE);
                strip3b.setVisibility(View.GONE);
            }


            if (quietMode.equals("fifty")) {
                Random rand = new Random();
                String QUOTES[] = {
                        "Hate it or love it, the underdog's on top. And I′m gon' shine homie until my heart stop―50 Cent",
                        "I sit and think of things to say that may make you smile. Or give you gifts from my heart to reflect my style―50 Cent",
                        "My flow, my show, brought me the dough. That bought me all my fancy things―50 Cent",
                        "Death gotta be easy, cause life is hard. It’ll leave you physically, mentally, and emotionally scarred―50 Cent",
                        "You shouldn't throw stones if you live in a glass house. And if you got a glass jaw, you should watch your mouth―50 Cent",
                        "Picture me coming up, picture me rich from rap, picture me blowing up, now picture me going back―50 Cent",
                        "I'm the diamond in the dirt that ain't been found. I'm the underground king and I ain't been crowned―50 Cent",
                        "I'm doin' what I'm supposed to. I'm a writer, I'm a fighter, entrepreneur―50 Cent",
                        "If I die today I'm happy how my life turned out―50 Cent",
                        "Hold up, hold up, get a good look at my rims. Goddamn, look at them twenty-fours inch diamonds spin―50 Cent",
                        "If I went back to a hooptie from a Benz. Would you poof and disappear like some of my friends?―50 Cent",
                };

                Toast.makeText(getApplicationContext(),
                        QUOTES[rand.nextInt(QUOTES.length)],
                        Toast.LENGTH_LONG).show();
            }

            /**
             * Capture image on button click
             */
            btnCapturePicture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        buttonNext.setBackgroundColor(Color.parseColor("#808080"));
                        captureImageA();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
                }
            });

            /**
             * Capture image on button click
             */
            btnCapturePictureB.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        buttonNext.setBackgroundColor(Color.parseColor("#5396ac"));
                        buttonNext.setEnabled(true);
                        captureImageB();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
                }
            });

            /**
             * Change game ID on button click
             */
            buttonNext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        if (Ngames2 > ticker) {
                            ticker = ticker + 1;
                            buttonNext.setBackgroundColor(Color.parseColor("#808080"));
                            buttonNext.setEnabled(false);
                        } else {
                            ticker = 1;

                            buttonNext.setBackgroundColor(Color.parseColor("#610c04"));
                        }
                        captureImageA();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
                }
            });

            // restoring storage image path from saved instance state
            // otherwise the path will be null on device rotation
            restoreFromBundle(savedInstanceState);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    } else if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + VIDEO_EXTENSION)) {
                        previewVideo();
                    }
                }
            }
        }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImageA();
                            } else {
                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */

    private void captureImageA() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String panelStamp = new String("A");

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MainActivity.GALLERY_DIRECTORY_NAME);


      //  String personStamp = person_id.getText().toString();
     //   gameStamp = game_id.getText().toString();

      //  File file = new File(mediaStorageDir.getPath() + File.separator
       //         + gameStamp + "_" + personStamp + "_" + panelStamp + "." + MainActivity.IMAGE_EXTENSION);
       // if (file != null) {
      //      imageStoragePath = file.getAbsolutePath();
      //  }

      //  Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

      //  intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        //startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

         game_id1.setHint("Offer");
         game_id1.setText("");
         game_id1.setEnabled(true);

         game_id2.setHint("Offer");
         game_id2.setText("");
         game_id2.setEnabled(true);

         game_id3.setHint("Offer");
         game_id3.setText("");
         game_id3.setEnabled(true);


         game_id4.setHint("Offer");
         game_id4.setText("");
         game_id4.setEnabled(true);

         game_id5.setHint("Offer");
         game_id5.setText("");
         game_id5.setEnabled(true);

         game_id6.setHint("Offer");
         game_id6.setText("");
         game_id6.setEnabled(true);


         game_id7.setHint("Offer");
         game_id7.setText("");
         game_id7.setEnabled(true);

         game_id8.setHint("Offer");
         game_id8.setText("");
         game_id8.setEnabled(true);

         game_id9.setHint("Offer");
         game_id9.setText("");
         game_id9.setEnabled(true);

         previewCapturedImage();

         buttonNext.setText(globalGameStamp);

       // File jfile = new File(mediaStorageDir.getPath() + File.separator
       //         + "SubsetContributions/" + gameStamp + ".json");



    }



    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImageB() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MainActivity.GALLERY_DIRECTORY_NAME);

        gameOffer1 = game_id1.getText().toString();
        gameOffer2 = game_id2.getText().toString();
        gameOffer3 = game_id3.getText().toString();
        gameOffer4 = game_id4.getText().toString();
        gameOffer5 = game_id5.getText().toString();
        gameOffer6 = game_id6.getText().toString();
        gameOffer7 = game_id7.getText().toString();
        gameOffer8 = game_id8.getText().toString();
        gameOffer9 = game_id9.getText().toString();

        //JSON Parser from Gson Library
        JsonParser parser = new JsonParser();

        //Creating JSONObject from String using parser
        JsonObject JSONObject1 = parser.parse(myJSONp).getAsJsonObject();

        JSONObject1.addProperty("Offer1", gameOffer1);
        JSONObject1.addProperty("Offer2", gameOffer2);
        JSONObject1.addProperty("Offer3", gameOffer3);

        JSONObject1.addProperty("Offer4", gameOffer4);
        JSONObject1.addProperty("Offer5", gameOffer5);
        JSONObject1.addProperty("Offer6", gameOffer6);

        JSONObject1.addProperty("Offer7", gameOffer7);
        JSONObject1.addProperty("Offer8", gameOffer8);
        JSONObject1.addProperty("Offer9", gameOffer9);

        gameStamp = globalGameStamp; //game_id.getText().toString();

        File filePath = new File(mediaStorageDir.getPath() + File.separator
                +  "SubsetContributions/" + gameStamp + ".json");

        Gson gson = new Gson();


        try{
       // gson.toJson(JSONObject1, new FileWriter(filePath));
            String response = JSONObject1.toString();
            System.out.println(response);
            System.out.println(filePath.getAbsolutePath());
            FileWriter fos = new FileWriter(filePath.getAbsolutePath());
            fos.write(response);
            fos.flush(); 
            fos.close(); 
        }catch(IOException e){
        System.out.println("error");
        }

        previewCapturedImage();
        
    }

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    /**
     * Launching camera app to record video
     */
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File file = CameraUtils.getOutputMediaFileA(MEDIA_TYPE_VIDEO);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
         try {
            // hide video preview... junk DNA of code

            videoPreview.setVisibility(View.GONE);
            
            txtDescription1.setVisibility(View.GONE);
            txtDescription2.setVisibility(View.GONE);
            txtDescription3.setVisibility(View.GONE);

            txtDescription4.setVisibility(View.GONE);
            txtDescription5.setVisibility(View.GONE);
            txtDescription6.setVisibility(View.GONE);

            txtDescription7.setVisibility(View.GONE);
            txtDescription8.setVisibility(View.GONE);
            txtDescription9.setVisibility(View.GONE);

            imgPreview1.setVisibility(View.VISIBLE);

          File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MainActivity.GALLERY_DIRECTORY_NAME);

        personStamp = game_id.getText().toString();


        File filePath2 = new File(mediaStorageDir.getPath() + File.separator
                +  "SubsetContributions/GIDsByPID/" + personStamp + ".json");

             System.out.println(filePath2);

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

        File filePath = new File(mediaStorageDir.getPath() + File.separator
                +  "SubsetContributions/" + gameStamp + ".json");

        try {
             String myJSON = new String(Files.readAllBytes(Paths.get(filePath.getAbsolutePath())));
                 System.out.println(myJSON);
            myJSONp = myJSON;
            //JSON Parser from Gson Library
            JsonParser parser = new JsonParser();
            JsonObject JSONObject1 = parser.parse(myJSON).getAsJsonObject();

        //Creating JSONObject from String using parser
        String a1JsonString = JSONObject1.get("AID1").toString();
        String a1bJsonString = a1JsonString.substring(1, a1JsonString.length() - 1);
        File file = new File(mediaStorageDir.getPath() + File.separator
                +  "StandardizedPhotos/" + a1bJsonString + "." + MainActivity.IMAGE_EXTENSION);

            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file.getAbsolutePath());

            imgPreview1.setImageBitmap(bitmap);

        String o1JsonString = JSONObject1.get("Offer1").toString();
        String o1bJsonString = o1JsonString.substring(1, o1JsonString.length() - 1);
        if(personStamp.equals(a1bJsonString)){
                game_id1.setEnabled(true);
               if(photoMode.equals("onlyfocal")) {
                game_id1.setHintTextColor(Color.parseColor("#006f94"));
               }
            }
        if(!personStamp.equals(a1bJsonString)){
            if(photoMode.equals("onlyfocal")){
                game_id1.setEnabled(false);
                game_id1.setHintTextColor(Color.parseColor("#a3abad"));
             }
            }

        if (a1bJsonString.equals("BLANK")){
            game_id1.setEnabled(false);
            game_id1.setHint(" ");
        }
        if (!o1bJsonString.equals("")){
         game_id1.setHint(o1bJsonString);
         game_id1.setText(o1bJsonString);
            if(entryMode.equals("permanent")){
              game_id1.setEnabled(false);
            }
        }







                imgPreview2.setVisibility(View.VISIBLE);
                //Creating JSONObject from String using parser
                String a2JsonString = JSONObject1.get("AID2").toString();
                String a2bJsonString = a2JsonString.substring(1, a2JsonString.length() - 1);
                File file2 = new File(mediaStorageDir.getPath() + File.separator
                        + "StandardizedPhotos/" + a2bJsonString + "." + MainActivity.IMAGE_EXTENSION);
                Bitmap bitmap2 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file2.getAbsolutePath());
                imgPreview2.setImageBitmap(bitmap2);

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


                // gameOffer2 = game_id2.getText().toString();


                System.out.println(gameOffer2);


            if (!photoNumber.equals("oneimage")) {
                imgPreview3.setVisibility(View.VISIBLE);
                //Creating JSONObject from String using parser
                String a3JsonString = JSONObject1.get("AID3").toString();
                String a3bJsonString = a3JsonString.substring(1, a3JsonString.length() - 1);
                File file3 = new File(mediaStorageDir.getPath() + File.separator
                        + "StandardizedPhotos/" + a3bJsonString + "." + MainActivity.IMAGE_EXTENSION);
                Bitmap bitmap3 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file3.getAbsolutePath());
                imgPreview3.setImageBitmap(bitmap3);

                String o3JsonString = JSONObject1.get("Offer3").toString();
                String o3bJsonString = o3JsonString.substring(1, o3JsonString.length() - 1);

                if (personStamp.equals(a3bJsonString)) {
                    game_id3.setEnabled(true);
                    if (photoMode.equals("onlyfocal")) {
                        game_id3.setHintTextColor(Color.parseColor("#006f94"));
                    }
                }
                if (!personStamp.equals(a3bJsonString)) {
                    if (photoMode.equals("onlyfocal")) {
                        game_id3.setEnabled(false);
                        game_id3.setHintTextColor(Color.parseColor("#a3abad"));
                    }
                }

                if (a3bJsonString.equals("BLANK")) {
                    game_id3.setEnabled(false);
                    game_id3.setHint(" ");
                }
                if (!o3bJsonString.equals("")) {
                    game_id3.setHint(o3bJsonString);
                    game_id3.setText(o3bJsonString);
                    if (entryMode.equals("permanent")) {
                        game_id3.setEnabled(false);
                    }
                }


                //  gameOffer3 = game_id3.getText().toString();
                System.out.println(personStamp);
                System.out.println(a3bJsonString);
            }

            imgPreview4.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a4JsonString = JSONObject1.get("AID4").toString();
             String a4bJsonString = a4JsonString.substring(1, a4JsonString.length() - 1);
             File file4 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a4bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap4 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file4.getAbsolutePath());
             imgPreview4.setImageBitmap(bitmap4);

        String o4JsonString = JSONObject1.get("Offer4").toString();
        String o4bJsonString = o4JsonString.substring(1, o4JsonString.length() - 1);

            if(personStamp.equals(a4bJsonString)){
                game_id4.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id4.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a4bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id4.setEnabled(false);
                    game_id4.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a4bJsonString.equals("BLANK")){
            game_id4.setEnabled(false);
            game_id4.setHint(" ");
        }
        if (!o4bJsonString.equals("")){
         game_id4.setHint(o4bJsonString);
         game_id4.setText(o4bJsonString);
            if(entryMode.equals("permanent")) {
                game_id4.setEnabled(false);
            }
        }
     //    gameOffer4 = game_id4.getText().toString();


            imgPreview5.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a5JsonString = JSONObject1.get("AID5").toString();
             String a5bJsonString = a5JsonString.substring(1, a5JsonString.length() - 1);
             File file5 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a5bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap5 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file5.getAbsolutePath());
             imgPreview5.setImageBitmap(bitmap5);

        String o5JsonString = JSONObject1.get("Offer5").toString();
        String o5bJsonString = o5JsonString.substring(1, o5JsonString.length() - 1);

            if(personStamp.equals(a5bJsonString)){
                game_id5.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id5.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a5bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id5.setEnabled(false);
                    game_id5.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a5bJsonString.equals("BLANK")){
            game_id5.setEnabled(false);
            game_id5.setHint(" ");
        }
        if (!o5bJsonString.equals("")){
         game_id5.setHint(o5bJsonString);
         game_id5.setText(o5bJsonString);
            if(entryMode.equals("permanent")) {
                game_id5.setEnabled(false);
            }
        }
      //   gameOffer5 = game_id5.getText().toString();


            imgPreview6.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a6JsonString = JSONObject1.get("AID6").toString();
             String a6bJsonString = a6JsonString.substring(1, a6JsonString.length() - 1);
             File file6 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a6bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap6 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file6.getAbsolutePath());
             imgPreview6.setImageBitmap(bitmap6);


        String o6JsonString = JSONObject1.get("Offer6").toString();
        String o6bJsonString = o6JsonString.substring(1, o6JsonString.length() - 1);

            if(personStamp.equals(a6bJsonString)){
                game_id6.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id6.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a6bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id6.setEnabled(false);
                    game_id6.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a6bJsonString.equals("BLANK")){
            game_id6.setEnabled(false);
            game_id6.setHint(" ");
        }
        if (!o6bJsonString.equals("")){
         game_id6.setHint(o6bJsonString);
         game_id6.setText(o6bJsonString);
            if(entryMode.equals("permanent")) {
                game_id6.setEnabled(false);
            }
        }
      //   gameOffer6 = game_id6.getText().toString();


            imgPreview7.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a7JsonString = JSONObject1.get("AID7").toString();
             String a7bJsonString = a7JsonString.substring(1, a7JsonString.length() - 1);
             File file7 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a7bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap7 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file7.getAbsolutePath());
             imgPreview7.setImageBitmap(bitmap7);


        String o7JsonString = JSONObject1.get("Offer7").toString();
        String o7bJsonString = o7JsonString.substring(1, o7JsonString.length() - 1);

            if(personStamp.equals(a7bJsonString)){
                game_id7.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id7.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a7bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id7.setEnabled(false);
                    game_id7.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a7bJsonString.equals("BLANK")){
            game_id7.setEnabled(false);
            game_id7.setHint(" ");
        }
        if (!o7bJsonString.equals("")){
         game_id7.setHint(o7bJsonString);
         game_id7.setText(o7bJsonString);
            if(entryMode.equals("permanent")) {
                game_id7.setEnabled(false);
            }
        }


       //  gameOffer7 = game_id7.getText().toString();


            imgPreview8.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a8JsonString = JSONObject1.get("AID8").toString();
             String a8bJsonString = a8JsonString.substring(1, a8JsonString.length() - 1);
             File file8 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a8bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap8 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file8.getAbsolutePath());
             imgPreview8.setImageBitmap(bitmap8);


        String o8JsonString = JSONObject1.get("Offer8").toString();
        String o8bJsonString = o8JsonString.substring(1, o8JsonString.length() - 1);

            if(personStamp.equals(a8bJsonString)){
                game_id8.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id8.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a8bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id8.setEnabled(false);
                    game_id8.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a8bJsonString.equals("BLANK")){
            game_id8.setEnabled(false);
            game_id8.setHint(" ");
        }
        if (!o8bJsonString.equals("")){
         game_id8.setHint(o8bJsonString);
         game_id8.setText(o8bJsonString);
            if(entryMode.equals("permanent")) {
                game_id8.setEnabled(false);
            }
        }


       //  gameOffer8 = game_id8.getText().toString();


            imgPreview9.setVisibility(View.VISIBLE);
             //Creating JSONObject from String using parser
             String a9JsonString = JSONObject1.get("AID9").toString();
             String a9bJsonString = a9JsonString.substring(1, a9JsonString.length() - 1);
             File file9 = new File(mediaStorageDir.getPath() + File.separator
                     +  "StandardizedPhotos/" + a9bJsonString + "." + MainActivity.IMAGE_EXTENSION);
             Bitmap bitmap9 = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, file9.getAbsolutePath());
             imgPreview9.setImageBitmap(bitmap9);


        String o9JsonString = JSONObject1.get("Offer9").toString();
        String o9bJsonString = o9JsonString.substring(1, o9JsonString.length() - 1);

            if(personStamp.equals(a9bJsonString)){
                game_id9.setEnabled(true);
                if(photoMode.equals("onlyfocal")) {
                    game_id9.setHintTextColor(Color.parseColor("#006f94"));
                }
            }
            if(!personStamp.equals(a9bJsonString)){
                if(photoMode.equals("onlyfocal")) {
                    game_id9.setEnabled(false);
                    game_id9.setHintTextColor(Color.parseColor("#a3abad"));
                }
            }

        if (a9bJsonString.equals("BLANK")){
            game_id9.setEnabled(false);
            game_id9.setHint(" ");
        }
        if (!o9bJsonString.equals("")){
         game_id9.setHint(o9bJsonString);
         game_id9.setText(o9bJsonString);
            if(entryMode.equals("permanent")) {
                game_id9.setEnabled(false);
            }
        }

      //   if( 9 > 6){
      //      imgPreview9.setVisibility(View.GONE);
      //      txtDescription9.setVisibility(View.GONE);
      //      game_id9.setVisibility(View.GONE);
      //      strip3.setVisibility(View.GONE);
      //    }
      //   gameOffer9 = game_id9.getText().toString();


     ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
     toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            game_id1.setVisibility(View.GONE);
            game_id2.setVisibility(View.GONE);
            game_id3.setVisibility(View.GONE);

            game_id4.setVisibility(View.GONE);
            game_id5.setVisibility(View.GONE);
            game_id6.setVisibility(View.GONE);

            game_id7.setVisibility(View.GONE);
            game_id8.setVisibility(View.GONE);
            game_id9.setVisibility(View.GONE);
        } else {
            game_id1.setVisibility(View.VISIBLE);
            game_id2.setVisibility(View.VISIBLE);
            game_id3.setVisibility(View.VISIBLE);

            game_id4.setVisibility(View.VISIBLE);
            game_id5.setVisibility(View.VISIBLE);
            game_id6.setVisibility(View.VISIBLE);

            game_id7.setVisibility(View.VISIBLE);
            game_id8.setVisibility(View.VISIBLE);
            game_id9.setVisibility(View.VISIBLE);
        }
        }
        });



        } catch (IOException e) {
            e.printStackTrace();

            txtDescription1.setVisibility(View.VISIBLE);
            txtDescription2.setVisibility(View.VISIBLE);
            txtDescription3.setVisibility(View.VISIBLE);

            txtDescription4.setVisibility(View.VISIBLE);
            txtDescription5.setVisibility(View.VISIBLE);
            txtDescription6.setVisibility(View.VISIBLE);

            txtDescription7.setVisibility(View.VISIBLE);
            txtDescription8.setVisibility(View.VISIBLE);
            txtDescription9.setVisibility(View.VISIBLE);

            imgPreview1.setVisibility(View.GONE);
            imgPreview2.setVisibility(View.GONE);
            imgPreview3.setVisibility(View.GONE);

            imgPreview4.setVisibility(View.GONE);
            imgPreview5.setVisibility(View.GONE);
            imgPreview6.setVisibility(View.GONE);

            imgPreview7.setVisibility(View.GONE);
            imgPreview8.setVisibility(View.GONE);
            imgPreview9.setVisibility(View.GONE);
            globalGameStamp = "NONE";
        }



        } catch (NullPointerException e) {
            e.printStackTrace();

            txtDescription1.setVisibility(View.VISIBLE);
            txtDescription2.setVisibility(View.VISIBLE);
            txtDescription3.setVisibility(View.VISIBLE);

            txtDescription4.setVisibility(View.VISIBLE);
            txtDescription5.setVisibility(View.VISIBLE);
            txtDescription6.setVisibility(View.VISIBLE);

            txtDescription7.setVisibility(View.VISIBLE);
            txtDescription8.setVisibility(View.VISIBLE);
            txtDescription9.setVisibility(View.VISIBLE);

            imgPreview1.setVisibility(View.GONE);
            imgPreview2.setVisibility(View.GONE);
            imgPreview3.setVisibility(View.GONE);

            imgPreview4.setVisibility(View.GONE);
            imgPreview5.setVisibility(View.GONE);
            imgPreview6.setVisibility(View.GONE);

            imgPreview7.setVisibility(View.GONE);
            imgPreview8.setVisibility(View.GONE);
            imgPreview9.setVisibility(View.GONE);

            globalGameStamp = "NONE";
        }

        } catch (Exception e) {
            e.printStackTrace();
            txtDescription1.setVisibility(View.VISIBLE);
            txtDescription2.setVisibility(View.VISIBLE);
            txtDescription3.setVisibility(View.VISIBLE);

            txtDescription4.setVisibility(View.VISIBLE);
            txtDescription5.setVisibility(View.VISIBLE);
            txtDescription6.setVisibility(View.VISIBLE);

            txtDescription7.setVisibility(View.VISIBLE);
            txtDescription8.setVisibility(View.VISIBLE);
            txtDescription9.setVisibility(View.VISIBLE);

            imgPreview1.setVisibility(View.GONE);
            imgPreview2.setVisibility(View.GONE);
            imgPreview3.setVisibility(View.GONE);

            imgPreview4.setVisibility(View.GONE);
            imgPreview5.setVisibility(View.GONE);
            imgPreview6.setVisibility(View.GONE);

            imgPreview7.setVisibility(View.GONE);
            imgPreview8.setVisibility(View.GONE);
            imgPreview9.setVisibility(View.GONE);

            globalGameStamp = "NONE";
        }
    }

    /**
     * Displaying video in VideoView
     */
    private void previewVideo() {
        try {
            // hide image preview
            txtDescription1.setVisibility(View.GONE);
            imgPreview1.setVisibility(View.GONE);

            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(imageStoragePath);
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(MainActivity.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
