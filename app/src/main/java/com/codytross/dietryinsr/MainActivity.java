package com.codytross.dietryinsr;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
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
    private TextView tvTreePath, tvPermAlert, tvEnum, tvEnumAlert, tvPartID;
    private Button btnMakeAllocations, btnExpectations, btnRich, btnRep1, btnRep2, btnReportAllocations,btnPayout, btnEnumerator, btnPartID, btnCheck;
    public static Context appContext;
    private String partID, enumeratorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();

        tvTreePath = findViewById(R.id.tvLoc);
        tvPermAlert = findViewById(R.id.tvPermAlert);
        tvEnumAlert = findViewById(R.id.tvEnumAlert);
        tvEnum = findViewById(R.id.tvEnum);
        btnMakeAllocations = findViewById(R.id.btnPlay);
        btnRep1 = findViewById(R.id.btn_Rep1);
        btnExpectations = findViewById(R.id.btnExpectations);
        btnReportAllocations = findViewById(R.id.btn_report);
        btnRep2 = findViewById(R.id.btn_Rep2);
        btnPayout = findViewById(R.id.btn_payout);
        btnEnumerator = findViewById(R.id.btnEnumerator);
        btnRich = findViewById(R.id.btnRich);
        tvPartID = findViewById(R.id.part_id);
        btnPartID = findViewById(R.id.btn_part_id);
        btnCheck = findViewById(R.id.btn_check);

        // get tree uri and enumerator from shared prefs

        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String treeUriString = sharedPref.getString(getString(R.string.treeUriString), "");
        enumeratorId = sharedPref.getString(getString(R.string.enumIdString), "");
        partID = sharedPref.getString(getString(R.string.partIdString), "");

        if (treeUriString.equals("")) {
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

        if(enumeratorId.equals("")){
            flagEnumeratorNeeded();
        } else {
           tvEnum.setText(enumeratorId);
        }

        tvPartID.setText(partID);

        // Set text to current location
        tvTreePath.setText(treePath);

        // Participant ID button
        btnPartID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPartID();
            }
        });

        // Check participant button
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkIntent = new Intent(getApplicationContext(), checkParticipant.class);
                startActivity(checkIntent);
            }
        });

        // Allocations button
        btnMakeAllocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetContributions%2FGIDsByPID", partID)) {
                    Intent dgIntent = new Intent(getApplicationContext(), dg.class);
                    startActivity(dgIntent);
                } else {
                    alertNoSettingsFile();
                }

            }
        });

        // Reputational evals 1 button
        btnRep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetRep1%2FGIDsByPID", partID)) {
                    Intent repIntent = new Intent(getApplicationContext(), reputation.class);
                    repIntent.putExtra("repEvalRound", 1);
                    startActivity(repIntent);
                } else {
                    alertNoSettingsFile();
                }
            }
        });

        // Expectations button
        btnExpectations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetExpectations%2FGIDsByPID", partID)) {
                    Intent expectationsIntent = new Intent(getApplicationContext(), expectations.class);
                    expectationsIntent.putExtra("hideActualAllocation", true);
                    startActivity(expectationsIntent);
                } else {
                    alertNoSettingsFile();
                }
            }
        });


        // Report allocations button
        btnReportAllocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetRevelations%2FGIDsByPID", partID)) {
                    Intent expectationsIntent = new Intent(getApplicationContext(), expectations.class);
                    expectationsIntent.putExtra("hideActualAllocation", false);
                    startActivity(expectationsIntent);
                } else {
                    alertNoSettingsFile();
                }
            }
        });


        // Reputational evals 2 button
        btnRep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetRep2%2FGIDsByPID", partID)) {
                    Intent repIntent = new Intent(getApplicationContext(), reputation.class);
                    repIntent.putExtra("repEvalRound", 2);
                    startActivity(repIntent);
                } else {
                    alertNoSettingsFile();
                }
            }
        });

        // Payout button
        btnPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConfigFile("SubsetPayouts", partID)) {
                    Intent payoutIntent = new Intent(getApplicationContext(), payout.class);
                    startActivity(payoutIntent);
                } else {
                    alertNoSettingsFile();
                }
            }
        });

        // Enumerator button
        btnEnumerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnumerator();
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

    private void setPartID() {
        // Dialog box to enter ID
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Participant ID");
        builder.setMessage("Set Participant ID");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {;
            String enteredPartID = input.getText().toString();
            SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.partIdString), enteredPartID);
            editor.apply();
            tvPartID.setText(enteredPartID);
            partID = enteredPartID;
        });
        builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setEnumerator() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enumerator ID");
        builder.setMessage("Set Enumerator ID");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {;
            String value = input.getText().toString();
            SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.enumIdString), value);
            editor.apply();
            tvEnum.setText(value);
            if (value.equals("")) {
                tvEnumAlert.setText("Set enumerator ID!");
                tvEnumAlert.setVisibility(View.VISIBLE);
            } else {
                tvEnumAlert.setVisibility(View.GONE);
                enumeratorId = value;
                checkGangsta();
            }

        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkGangsta() {
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String enumeratorId = sharedPref.getString(getString(R.string.enumIdString), "");
        List names = Arrays.asList("poorvi","Poorvi","poorvi iyer", "Poorvi Iyer", "poorviiyer", "PoorviIyer", "piyer", "Piyer", "PIyer");
        if(names.contains(enumeratorId)) {
           MediaPlayer mediaPlayer = MediaPlayer.create(appContext, R.raw.pimp);
           mediaPlayer.start();
        }
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
        tvPermAlert.setText("Set Location of RICH folder!");
    }

    private void flagEnumeratorNeeded() {
        tvEnumAlert.setVisibility(View.VISIBLE);
        tvEnumAlert.append("Set enumerator ID!");
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
                SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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


    public final void writeTextToUri(Uri uri, String text) throws IOException {
        try(OutputStream outputStream = getContentResolver().openOutputStream(uri,"wt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))){
            writer.write(text);
            writer.newLine();
            writer.flush();
            writer.close();
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

    private Boolean checkConfigFile(String subdir, String playerId) {
        String settingsUriSt = treeDoc.getUri().toString() + "%2F" + subdir + "%2F" + playerId + ".json"; // Hacky but fast
        Uri settingsUri = Uri.parse(settingsUriSt);
        try {
            InputStream in = getContentResolver().openInputStream(settingsUri);
            in.close();
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(false);
    }
    private void alertNoSettingsFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ID not found");
        builder.setMessage("No participant with the ID you entered or no Settings file for this activity. Please check for typos and use the CHECK button. Pay attention to capital letters, if any." +
                "\n\n If the problem persists, contact people in charge of the research project.");
//        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

