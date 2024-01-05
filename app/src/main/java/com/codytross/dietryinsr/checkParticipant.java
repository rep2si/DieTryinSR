package com.codytross.dietryinsr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.provider.DocumentFile;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;

import android.util.Log;

import java.io.InputStream;

public class checkParticipant extends MainActivity {

    private ImageView imgCheckParticipant;
    private Button btnYes, btnNo;
    private TextView tvCheckParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use correct layout
        setContentView(R.layout.activity_check_participant);

        // get elements
        imgCheckParticipant = findViewById(R.id.img_check_participant);
        tvCheckParticipant = findViewById(R.id.tvCheckParticipant);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        // apply translations
        tvCheckParticipant.setText(i18nMap.get("checkPart"));
        btnYes.setText(i18nMap.get("btn_yes"));
        btnNo.setText(i18nMap.get("btn_no"));

        // load image
        loadPartImg();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertWrongPart();
            }
        });

    }

    private void loadPartImg() {
        SharedPreferences sharedPref = appContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String partID = sharedPref.getString(getString(R.string.partIdString), "");
        String imageUriSt = treeDoc.getUri().toString() + "%2F" + "StandardizedPhotos" + "%2F" + partID + ".jpg"; // Hacky but fast
        Uri imageUri = Uri.parse(imageUriSt);

        // Ugly hack? Check if file can be opened.
        try {
            InputStream in = getContentResolver().openInputStream(imageUri);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            alertTypo();
        }

        // TODO Check that file exists

        // Create bitmap
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
            imgCheckParticipant.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void alertWrongPart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18nMap.get("message_title_wrongPart"));
        builder.setMessage(i18nMap.get("message_wrongPart"));
        builder.setCancelable(false);
        builder.setPositiveButton(i18nMap.get("ok"), (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
            finish();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertTypo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18nMap.get("message_title_nosettings"));
        builder.setMessage(i18nMap.get("message_nosettings"));
        builder.setCancelable(false);
        builder.setPositiveButton(i18nMap.get("ok"), (DialogInterface.OnClickListener) (dialog, which) -> {;
            dialog.cancel();
            finish();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
