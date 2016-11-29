package com.example.abb2;
/* perfect running code with camera and flash and storage*/

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    SQLiteDatabase db;
    TextView testView;
    public static final int MEDIA_TYPE_IMAGE = 1;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    private final String tag = "Server";
    private File myGeneralFolder;
    private byte[] mCameraData;
    Button start, done,capture,save,retake;

    String newString;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras;
        if (savedInstanceState == null) {
            //fetching extra data passed with intents in a Bundle type variable
            extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            }
            else {
              /* fetching the string passed with intent using extras*/
                newString= extras.get("mr_no").toString();
            }
            Toast.makeText(getBaseContext(), newString, Toast.LENGTH_SHORT).show();
        }
        start = (Button)findViewById(R.id.button1);

        save = (Button)findViewById(R.id.button4);
        save.setEnabled(false);

        done = (Button)findViewById(R.id.button2);
        done.setEnabled(false);

        capture = (Button) findViewById(R.id.button3);
        capture.setEnabled(false);

        start.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {
                start_camera();
                capture.setEnabled(true);
            }
        });

        capture.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                captureImage();
                save.setEnabled(true);
            }
        });

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                store(mCameraData);
                stop_camera();
                done.setEnabled(true);
                capture.setEnabled(false);
            }
        });

        done.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {
                Intent it=new Intent(MainActivity.this,SuccessActivity.class);
                it.putExtra("mr_no", newString);
                startActivity(it);
                finish();
            }
        });

        surfaceView = (SurfaceView)findViewById(R.id.surface_view1);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rawCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Log", "onPictureTaken - raw");
            }
        };

        /** Handles data for jpeg picture */
        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                Log.i("Log", "onShutter'd");
            }
        };

        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                mCameraData = data;
                //store(data);
            }
        };
    }
    private void store(byte[] mCameraData) {
        myGeneralFolder = new  File(Environment.getExternalStorageDirectory()+File.separator+"myBullImages");
        myGeneralFolder.mkdirs();
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(String.format(myGeneralFolder+"/%d.jpg",(System.currentTimeMillis())));

            outStream.write(mCameraData);
            outStream.close();

            Log.d("Log", "onPictureTaken - wrote bytes: " + mCameraData.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getBaseContext(), "saved", Toast.LENGTH_SHORT).show();
        Log.d("Log", "onPictureTaken - jpeg");
        db=openOrCreateDatabase("mydbase.db", MODE_PRIVATE, null);
        String sql="CREATE TABLE IF NOT EXISTS patimgs(mr_no VARCHAR,url image)";
        db.execSQL(sql);
        String sql1="INSERT INTO  patimgs(mr_no,url) values('"+newString+"','"+mCameraData+"')";
        db.execSQL(sql1);
        Toast.makeText(getBaseContext(), "Saved in Database", Toast.LENGTH_SHORT).show();
    }
    private void captureImage() {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void start_camera() {
        try{
            camera = Camera.open();
            turnOnFlashLight();
        } catch(RuntimeException e){
            Log.e(tag, "init_camera: " + e);
            return;
        }

        Camera.Parameters param;
        param = camera.getParameters();
        //modify parameter
        param.setPreviewFrameRate(20);
        param.setPreviewSize(1280,720); /*176, 144*/

        param.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        param.setJpegQuality(100);
        //param.setFocusMode(param.FOCUS_MODE_FIXED);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            camera.setDisplayOrientation(90);

        } catch (Exception e) {
            Log.e(tag, "init_camera: " + e);
            return;
        }

    }

    private void stop_camera()
    {
        // camera.stopPreview();
        camera.release();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    // Turning On flash
    public void turnOnFlashLight() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Parameters p = camera.getParameters();
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception throws in turning on flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent it=new Intent(MainActivity.this,FirstActivity.class);
            startActivity(it);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // your code
        Intent it=new Intent(MainActivity.this,FirstActivity.class);
        startActivity(it);
        finish();
    }

}