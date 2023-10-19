package com.codytross.dietryinsr;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    public static final String IMAGE_EXTENSION = "jpg";
    public static final String GALLERY_DIRECTORY_NAME = "RICH";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String VIDEO_EXTENSION = "mp4";
    public Uri treeUri;
    public static DocumentFile treeDoc;
    private String treePath = "Location currently unset";
    private TextView tvTreePath, tvPermAlert;
    private Button btnPlay, btnPayout, btnRich;
    public Intent dgIntent, defIntent;
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();

        tvTreePath = findViewById(R.id.tvLoc);
        tvPermAlert = findViewById(R.id.tvPermAlert);
        btnPlay = findViewById(R.id.btnPlay);
        btnPayout = findViewById(R.id.btnPayout);
        btnRich = findViewById(R.id.btnRich);

        dgIntent = new Intent(this, dg.class);
        defIntent = new Intent(this, def.class);

        // get tree uri from shared prefs
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        String treeUriString = sharedPref.getString(getString(R.string.treeUriString), "");

        if (treeUriString == "") {
            Log.w("idx", "Tree Uri not stored in shared settings");
            flagPermissionNeeded();
        } else {
            if (checkAccess(treeUriString)) {
                // All good, make a DocumentFile
                Log.i("idx", "Tree Uri retrieved from shared settings, permissions ok");
                treeDoc = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(treeUriString));
                treeUri = Uri.parse(treeUriString);
                treePath = treeUri.getPath();
            } else {
                Log.w("idx", "Tree Uri retrieved from shared settings, but no permissions");
                flagPermissionNeeded();
            }
        }

        // Set text to current location
        tvTreePath.setText(treePath);

        // Play button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameMode = getGeneralSetting("gameMode");
                if (gameMode.equals("enhanced")) {
                    Log.i("idx", "launching enhanced mode");
                    startActivity(dgIntent);
                } else {
                    startActivity(defIntent);
                }
            }
        });

        // Rich button
        btnRich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
            }
        });

    }

    public String getGeneralSetting(String setting) {
        String gameSetting = "";
//        DocumentFile settingsFile = treeDoc.findFile("SubsetContributions").findFile("GIDsByPID").findFile("settings.json"); // SLOOOOW
        String settingsUri = treeDoc.getUri().toString() + "%2F" + "SubsetContributions" + "%2F" + "GIDsByPID" + "%2F" + "settings.json"; // Hacky but fast
        DocumentFile settingsFile = DocumentFile.fromSingleUri(appContext, Uri.parse(settingsUri));
        try {
            String jsonSettings = readTextFromUri(settingsFile.getUri());
            JsonObject jsonSettingsObj = JsonParser.parseString(jsonSettings).getAsJsonObject();
            gameSetting = jsonSettingsObj.get(setting).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameSetting;
    }

    private void flagPermissionNeeded() {
        tvPermAlert.setVisibility(View.VISIBLE);
    }

    private boolean checkAccess(String treeUriString) {
        for (UriPermission persistedUriPermission : getContentResolver().getPersistedUriPermissions()) {
            String persistedUriString = persistedUriPermission.getUri().toString();
            boolean canRead = persistedUriPermission.isReadPermission();
            boolean canWrite = persistedUriPermission.isWritePermission();
            if (persistedUriString.equals(treeUriString) && canRead && canWrite) {
                Log.i("idx", "Uri found in shared settings and permission set");
                return true;
            }
        }
        return false;
    }

    private void askPermission() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 45);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 45 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that the user selected.
            Uri treeUri = null;
            if (resultData != null) {
                treeUri = resultData.getData();
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String preferencesUri = treeUri.toString();

                // Store the uri in shared preferences
                SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.treeUriString), preferencesUri);
                editor.apply();

                // update treeDoc
                treeDoc = DocumentFile.fromTreeUri(this, treeUri);

                // Update path displayed
                tvTreePath.setText(treeUri.getPath());
                tvPermAlert.setVisibility(View.GONE);
            }
        }
    }

    public final String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

//    public final void writeTextToUri(Uri uri, String text) throws IOException {
//        try(OutputStream outputStream = getContentResolver().openOutputStream(uri);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(outputStream)))){
//            writer.write(text);
//            writer.flush();
//        }
//    }


//    public void alterDocument(Uri uri, String text) {
//        try {
//            ParcelFileDescriptor pfd = getActivity().getContentResolver().
//                    openFileDescriptor(uri, "w");
//            FileOutputStream fileOutputStream =
//                    new FileOutputStream(pfd.getFileDescriptor());
//            fileOutputStream.write((text).getBytes());
//            // Let the document provider know you're done by closing the stream.
//            fileOutputStream.close();
//            pfd.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public final void writeTextToUri(Uri uri, String text) throws IOException {
        try(OutputStream outputStream = getContentResolver().openOutputStream(uri,"wt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))){
            writer.write(text);
            writer.newLine();
            writer.flush();
            writer.close();
        }
    }

}

