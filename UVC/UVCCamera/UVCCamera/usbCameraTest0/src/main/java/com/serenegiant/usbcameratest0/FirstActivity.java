package com.serenegiant.usbcameratest0;

import android.os.Bundle;
import android.widget.Button;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vedipen on 29/11/16.
 */

@SuppressLint("NewApi") public class FirstActivity extends Activity {
    Button first, second;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        first=(Button) findViewById(R.id.FirstActivityFirstButton);
        second=(Button) findViewById(R.id.FirstActivitySecondButton);
        first.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FirstActivity.this, R.string.WelcomeToast, Toast.LENGTH_SHORT).show();
                Intent itf1=new Intent(FirstActivity.this,DetailsActivity.class);
                startActivity(itf1);
                finish();
            }
        });
        second.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itf2=new Intent(FirstActivity.this,SearchActivity.class);
                startActivity(itf2);
                finish();

            }
        });
    }
}
