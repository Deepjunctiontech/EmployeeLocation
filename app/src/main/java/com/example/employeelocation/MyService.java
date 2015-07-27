/**
 * 
 */
package com.example.employeelocation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;
import java.util.Calendar;

/**
 * @author Junction software Pvt Ltd.
 *
 */
public class MyService extends Service implements LocationListener{

	LocationManager lm;
	double lati, longi;
	private String mydate;
	private Gson gson;
	private String IMEI;
	private DBHANDLER db;


	@Override
	public void onCreate() {
		
		super.onCreate();
		db=new DBHANDLER(this,"K&J",null,1);
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();
		gson = new GsonBuilder().create();
		lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
	}

	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
       boolean gps_enable=false,network_enable=false;

        try
        {
            gps_enable=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception e)
        {
            Toast.makeText(this, "Exception GPS_Enable",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            network_enable=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception e)
        {
            Toast.makeText(this, "Exception Network_Enable",
                    Toast.LENGTH_LONG).show();
        }

        if(!gps_enable&&!network_enable)
        {
            Toast.makeText(this, "Both not enable",
                    Toast.LENGTH_LONG).show();
            Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }
        else if(gps_enable) {
            Toast.makeText(this, " GPS_Enable",
                    Toast.LENGTH_LONG).show();
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        }
        else if(network_enable) {
            Toast.makeText(this, "Network_Enable",
                    Toast.LENGTH_LONG).show();
            lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        }

        Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
		
		
		 PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar
				.getInstance().getTimeInMillis() + 30*1000, pendingIntent);
		
		return START_REDELIVER_INTENT;
	}
	public void updateMySQLSyncSts(final String abc) {
		
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("detail", abc);

	

		client.post("http://www.junctionerp.com/login/updateServer", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// prgDialog.hide();
				Toast.makeText(MyService.this, "Successfully Inserted",
								Toast.LENGTH_LONG).show();
				db.putStatus(abc,"true");
						
			//			if(check==false)
				//		myClick(false);

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {

						
			/*			if (statusCode == 404) {
							Toast.makeText(MyService.this, "Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(MyService.this,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									MyService.this,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_LONG).show();
						}
						*/
					//	db.addData(abc, "false");
			//			if(check==false)
				//		myClick(false);
					}
				});
	}

	@Override
	public void onLocationChanged(Location location) {
		lati = location.getLatitude();
		longi = location.getLongitude();
		
		mydate = java.text.DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());
		
		String[] abc = { IMEI, mydate, lati+"", longi+"" };
	//	Toast.makeText(MyService.this, lati+"\n"+longi, Toast.LENGTH_LONG).show();
		
		db.addData(gson.toJson(abc), "false");
		
		String cba[]=db.search();
		Arrays.sort(cba);
		
		for(int i=0;i<cba.length;i++)
		updateMySQLSyncSts(cba[i]);
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onStatusChanged",
				Toast.LENGTH_LONG).show();

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onProviderEnabled",
				Toast.LENGTH_LONG).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onProviderDisabled",
				Toast.LENGTH_LONG).show();

	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
