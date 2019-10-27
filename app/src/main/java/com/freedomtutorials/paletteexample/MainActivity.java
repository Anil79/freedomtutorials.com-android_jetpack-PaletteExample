package com.freedomtutorials.paletteexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static int RESULT_LOAD_IMAGE = 1;

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLoadImage = (Button) findViewById(R.id.imgLoadBut);

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        if(!checkPermissionForReadExtertalStorage())
        {
            try {
                requestPermissionForReadExtertalStorage();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public Palette generatePalette(Bitmap bitmap)
    {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Toast.makeText(getApplicationContext(), "requestCode=" + Integer.toString(requestCode) + ", resultCode=" + Integer.toString(resultCode), Toast.LENGTH_LONG).show();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            //Toast.makeText(getApplicationContext(), "selectedImage=" + selectedImage.toString(), Toast.LENGTH_LONG).show();

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();



            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);

            Bitmap bitMap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitMap);

            Palette p = generatePalette(bitMap);

            Palette.Swatch vibrantSwatch = p.getVibrantSwatch();



            if( vibrantSwatch != null)
            {
                int backgroundColor = vibrantSwatch.getRgb();
                int textColor = vibrantSwatch.getTitleTextColor();

                Toast.makeText(getApplicationContext(),
                        "backgroundColor=[" + Integer.toString(backgroundColor) + "],textColor=[" + Integer.toString(textColor) + "]",
                        Toast.LENGTH_LONG).show();

                View root = imageView.getRootView();
                root.setBackgroundColor( backgroundColor );
            }
            else
            {
                Toast.makeText(getApplicationContext(), "vibrantSwatch is null", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "unable to load...", Toast.LENGTH_LONG).show();
        }

    }
}
