package com.example.abb2;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

public class SuccessActivity extends Activity {
	SQLiteDatabase db;
	TextView e2,e4,e6,e8,e12,e11;String newString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		Bundle extras;


		if (savedInstanceState == null)

		{

			//fetching extra data passed with intents in a Bundle type variable

			extras = getIntent().getExtras();

			if(extras == null)

			{        newString= null;

			}

			else

			{            /* fetching the string passed with intent using extras*/

				newString= extras.get("mr_no").toString();

			}
			Toast.makeText(getBaseContext(), newString, Toast.LENGTH_SHORT).show();
		}

//		Button ret=(Button)findViewById(R.id.button1);

		Button home=(Button)findViewById(R.id.button2);
		e2=(TextView)findViewById(R.id.textView2);
		e4=(TextView)findViewById(R.id.textView4);
		e6=(TextView)findViewById(R.id.textView6);
		e8=(TextView)findViewById(R.id.textView8);
		e12=(TextView)findViewById(R.id.textView12);
		e11=(TextView)findViewById(R.id.textView11);
//		ret.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
		// TODO Auto-generated method stub

		db=openOrCreateDatabase("mydbase.db",MODE_PRIVATE, null);
		Cursor c=db.rawQuery("select * from pat where mr_no='"+newString+"'", null);
		c.moveToFirst();
		if(c!=null)
		{
//					Toast.makeText(getApplicationContext(), "retreiving", Toast.LENGTH_SHORT).show();
			do
			{
				int c1=c.getColumnIndex("name");
				String val1=c.getString(c1);
				e2.setText(val1);
				int c2=c.getColumnIndex("mr_no");
				String val2=c.getString(c2);
				e4.setText(val2);
				int c3=c.getColumnIndex("gender");
				String val3=c.getString(c3);
				e6.setText(val3);
//						Toast.makeText(this, "Here"+val3+val3.length(), Toast.LENGTH_SHORT).show();
				int c4=c.getColumnIndex("age");
				String val4=c.getString(c4);
				e8.setText(val4);
				int c5=c.getColumnIndex("phno");
				String val5=c.getString(c5);
				e12.setText(val5);
				int c6=c.getColumnIndex("address");
				String val6=c.getString(c6);
				e11.setText(val6);

			}while(c.moveToNext());

		}

//				Toast.makeText(getApplicationContext(), "retreived", Toast.LENGTH_LONG).show();
//			}
//		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it=new Intent(SuccessActivity.this,MainActivity.class);
				it.putExtra("mr_no", newString);
				startActivity(it);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.success, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_home) {
			Intent it=new Intent(SuccessActivity.this,FirstActivity.class);
			startActivity(it);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
		// your code
		Intent it=new Intent(SuccessActivity.this,FirstActivity.class);
		startActivity(it);
		finish();
	}
}
