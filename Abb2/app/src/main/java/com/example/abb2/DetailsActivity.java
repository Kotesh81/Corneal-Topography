package com.example.abb2;




import android.os.Bundle;

import android.telephony.PhoneNumberFormattingTextWatcher;
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

@SuppressLint("NewApi") public class DetailsActivity extends Activity {
    SQLiteDatabase db;
    Button ok,cancel;
    EditText pname,mrn,age,pno,add;
    CheckBox male,female;

    int cm = 0;
    int cf = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        pname=(EditText)findViewById(R.id.editText1);
        mrn=(EditText)findViewById(R.id.editText2);
        age=(EditText)findViewById(R.id.editText3);
        pno=(EditText)findViewById(R.id.editText5);
        add=(EditText)findViewById(R.id.editText4);
        male=(CheckBox)findViewById(R.id.checkBox1);
        female=(CheckBox)findViewById(R.id.checkBox2);
        ok=(Button)findViewById(R.id.button1);
        cancel=(Button)findViewById(R.id.button2);
        male.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                female.setChecked(false);
            }
        });
        female.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                male.setChecked(false);
            }
        });
        pno.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String val1=pname.getText().toString();
                String val2=mrn.getText().toString();

                String val3=male.getText().toString();
                String val31=female.getText().toString();
                String val4=age.getText().toString();
                String val5=pno.getText().toString();
                String val6=add.getText().toString();

                if(val1.compareTo("")==0||val2.compareTo("")==0||val4.compareTo("")==0||val5.compareTo("")==0||val6.compareTo("")==0||(!male.isChecked()&&!female.isChecked()))
                {
                    Toast.makeText(getApplicationContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();

                }
                else{
                    String sql;
                    db=openOrCreateDatabase("mydbase.db", MODE_PRIVATE, null);

                    sql="CREATE TABLE IF NOT EXISTS pat(name VARCHAR,mr_no VARCHAR,gender VARCHAR,age VARCHAR,phno VARCHAR,address VARCHAR)";
                    db.execSQL(sql);
                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                    if (male.isChecked())
                    {
                        sql ="INSERT or replace INTO pat(NAME, MR_NO, GENDER,AGE,PHNO,ADDRESS) VALUES('"+val1+"','"+val2+"','"+val3+"','"+val4+"','"+val5+"','"+val6+"')" ;
                        db.execSQL(sql);
                    }
                    else
                    {
                        sql ="INSERT or replace INTO pat(NAME, MR_NO, GENDER,AGE,PHNO,ADDRESS) VALUES('"+val1+"','"+val2+"','"+val31+"','"+val4+"','"+val5+"','"+val6+"')" ;
                        db.execSQL(sql);
                    }
//                    Toast.makeText(getApplicationContext(), "Inserted", Toast.LENGTH_SHORT).show();
                    Intent it1=new Intent(DetailsActivity.this,SuccessActivity.class);
                    it1.putExtra("mr_no", mrn.getText());
                    startActivity(it1);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pname.setText(" ");
                mrn.setText(" ");
                age.setText(" ");
                pno.setText(" ");
                add.setText(" ");
                male.setChecked(false);
                female.setChecked(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent it=new Intent(DetailsActivity.this,FirstActivity.class);
            startActivity(it);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // your code
        Intent it=new Intent(DetailsActivity.this,FirstActivity.class);
        startActivity(it);
        finish();
    }
}