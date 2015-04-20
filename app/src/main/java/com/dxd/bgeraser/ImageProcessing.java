package com.dxd.bgeraser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class ImageProcessing extends ActionBarActivity  {
    DrawView myDrawView;
    private SeekBar seekBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        myDrawView = (DrawView)findViewById(R.id.eraserImageView);
         seekBar = (SeekBar)findViewById(R.id.eraserSeekBar);
         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
             }

             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 // TODO Auto-generated method stub
                  myDrawView.setStrokeSize(progress);


             }
         });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_processing, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_save) {
            saveImage();
        }

        return super.onOptionsItemSelected(item);
    }


     private void  saveImage(){
        //save drawing
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save Image");
        saveDialog.setMessage("Save Image to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            private FileOutputStream fOut;

            public void onClick(DialogInterface dialog, int which){
                //save drawing

                Bitmap well = myDrawView.getBitmap();
                Bitmap save = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                Canvas now = new Canvas(save);
                now.drawRect(new Rect(0,0,320,480), paint);
                now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,320,480), null);

                //attempt to save
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), save,
                        UUID.randomUUID().toString()+".png", "drawing");
                //feedback
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                myDrawView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    public void onClickUndo (View v) {


    }

    public void onClickRedo (View v) {

    }





}