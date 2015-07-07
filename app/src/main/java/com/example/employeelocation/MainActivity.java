package com.example.employeelocation;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
		//	alarmIntent.putExtra("st", "true");
			// Pending Intent Object
			 pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, alarmIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			// Alarm Manager Object
			alarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);
			// Alarm Manager calls BroadCast for every Ten seconds (10 * 1000),
			// BroadCase further calls service to check if new records are inserted
			// in
			// Remote MySQL DB
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
					.getInstance().getTimeInMillis() + 5000, 60*5* 1000,
					pendingIntent);
		
			
			
		
	//	Intent i=new Intent(this,MyService.class);
		//i.putExtra("check", "main");
	//	this.startService(i);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
