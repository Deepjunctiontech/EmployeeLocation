package com.example.employeelocation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class SampleBC extends BroadcastReceiver implements LocationListener {

	LocationManager lm;
	double lati, longi;
	private String location;
	private String mydate;
	private Gson gson;
	Context c;
	private String IMEI;

	private boolean check=true;
	
	
	public static abstract class LocationResult{
	    public abstract void gotLocation(Location location);
	}



	@Override
	public void onReceive(Context arg0, Intent arg1) {

		c = arg0;
		ConnectionDetector cd = new ConnectionDetector(arg0);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		check = cd.isConnectingToInternet();
		

		if (check == false)
			myClick(true);

		TelephonyManager telephonyManager = (TelephonyManager) arg0
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();

		

		gson = new GsonBuilder().create();

	

		//lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
		
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		    	
		    	try {
					Thread.sleep(5000);
					 onLocationChanged(location);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(arg0, locationResult);
		

	}
	public class MyLocation {
	    Timer timer1;
	    LocationManager lm;
	    LocationResult locationResult;
	    boolean gps_enabled=false;
	    boolean network_enabled=false;

	    public boolean getLocation(Context context, LocationResult result)
	    {
	        //I use LocationResult callback class to pass location value from MyLocation to user code.
	        locationResult=result;
	        if(lm==null)
	            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

	        //exceptions will be thrown if provider is not permitted.
	        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
	        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

	        //don't start listeners if no provider is enabled
	        if(!gps_enabled && !network_enabled)
	            return false;

	        if(gps_enabled)
	            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
	        if(network_enabled)
	            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
	        timer1=new Timer();
	        timer1.schedule(new GetLastLocation(), 20000);
	        return true;
	    }

	    LocationListener locationListenerGps = new LocationListener() {
	        public void onLocationChanged(Location location) {
	            timer1.cancel();
	            locationResult.gotLocation(location);
	            lm.removeUpdates(this);
	            lm.removeUpdates(locationListenerNetwork);
	        }
	        public void onProviderDisabled(String provider) {}
	        public void onProviderEnabled(String provider) {}
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	    };

	    LocationListener locationListenerNetwork = new LocationListener() {
	        public void onLocationChanged(Location location) {
	            timer1.cancel();
	            locationResult.gotLocation(location);
	            lm.removeUpdates(this);
	            lm.removeUpdates(locationListenerGps);
	        }
	        public void onProviderDisabled(String provider) {}
	        public void onProviderEnabled(String provider) {}
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	    };

	    class GetLastLocation extends TimerTask {
	        @Override
	        public void run() {
	             lm.removeUpdates(locationListenerGps);
	             lm.removeUpdates(locationListenerNetwork);

	             Location net_loc=null, gps_loc=null;
	             if(gps_enabled)
	                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	             if(network_enabled)
	                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	             //if there are both values use the latest one
	             if(gps_loc!=null && net_loc!=null){
	                 if(gps_loc.getTime()>net_loc.getTime())
	                     locationResult.gotLocation(gps_loc);
	                 else
	                     locationResult.gotLocation(net_loc);
	                 return;
	             }

	             if(gps_loc!=null){
	                 locationResult.gotLocation(gps_loc);
	                 return;
	             }
	             if(net_loc!=null){
	                 locationResult.gotLocation(net_loc);
	                 return;
	             }
	             locationResult.gotLocation(null);
	        }
	    }

	    
	}

	public void myClick(boolean t) {

		ConnectivityManager connectivity = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd = null;
		try {
			dataMtd = ConnectivityManager.class.getDeclaredMethod(
					"setMobileDataEnabled", boolean.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataMtd.setAccessible(true);
		try {
			dataMtd.invoke(connectivity, t);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateMySQLSyncSts(String abc) {
	
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("detail", abc);

	

		client.post("http://jsoftware.in/vishal/updateServer.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// prgDialog.hide();
						Toast.makeText(c, "Successfully Inserted",
								Toast.LENGTH_LONG).show();
						
						if(check==false)
						myClick(false);

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {

						if (statusCode == 404) {
							Toast.makeText(c, "Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(c,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									c,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_LONG).show();
						}
						if(check==false)
						myClick(false);
					}
				});
	}

	@Override
	public void onLocationChanged(Location location) {
		lati = location.getLatitude();
		longi = location.getLongitude();
		
		

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(2000);
					new V().execute(new Geocoder(c, Locale.ENGLISH));
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		t.start();

	}

	class V extends AsyncTask<Geocoder, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(Geocoder... geocoders) {
			List<Address> l1 = null;
			try {
				// Toast.makeText(c, "error", Toast.LENGTH_LONG).show();
				l1 = geocoders[0].getFromLocation(lati, longi, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(c, "error", Toast.LENGTH_LONG).show();

			}
			return l1;

		}

		@Override
		protected void onPostExecute(List<Address> result) {

			if (result != null)

			{

				Address a = result.get(0);
				
				for (int i = 0; i < a.getMaxAddressLineIndex(); i++)

				{

					location =a.getSubLocality() + "\n" + a.getAddressLine(i)

					+ "\n" + a.getCountryName() + " " + a.getCountryCode();

				}
				
				mydate = java.text.DateFormat.getDateTimeInstance().format(
						Calendar.getInstance().getTime());
				
				String[] abc = { IMEI, mydate, location };
				Toast.makeText(c, location, Toast.LENGTH_LONG).show();
				updateMySQLSyncSts(gson.toJson(abc));
			}

		}

		@Override
		protected void onPreExecute() {

		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
