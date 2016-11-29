package com.example.abb2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vedipen on 29/11/16.
 */

@SuppressLint("NewApi") public class SearchActivity extends Activity {

    Button search;
    TextView mrns;
    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toast.makeText(SearchActivity.this, R.string.enter_details_search, Toast.LENGTH_SHORT).show();
        search = (Button) findViewById(R.id.searchMRBtn);
        mrns = (EditText) findViewById(R.id.searchMRText);
        db=openOrCreateDatabase("mydbase.db", MODE_PRIVATE, null);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c=db.rawQuery("select count(*) from pat where mr_no="+mrns.getText(),null);
                c.moveToFirst();
                if(c.getInt(0)==0) {
                    Toast.makeText(SearchActivity.this, "Not in Database", Toast.LENGTH_SHORT).show();
                    Intent it1 = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(it1);
                    finish();
                }
                else {
                    Intent it2= new Intent(SearchActivity.this, SuccessActivity.class);
                    it2.putExtra("mr_no", mrns.getText());
                    startActivity(it2);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.suc, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent it = new Intent(SearchActivity.this, FirstActivity.class);
            startActivity(it);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // your code
        Intent it = new Intent(SearchActivity.this, FirstActivity.class);
        startActivity(it);
        finish();
    }
}