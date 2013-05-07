package com.spongesoft.bananarun;

import com.spongesoft.bananarun.R;
import com.spongesoft.bananarun.R.drawable;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener {
	LocationManager locationManager;
	Location previousLocation;
	boolean running = true;
	DBManagement manager;
	double race;
	int pickerType;
	int pickerValue;
	double[] infoArray;
	String unitsSystem;

	@Override
	public void onLocationChanged(Location loc) {
		if (running) {
			// Toast.makeText(this,"Lat: " + String.valueOf(loc.getLatitude()) +
			// " Long: " +
			// String.valueOf(loc.getLongitude()),Toast.LENGTH_SHORT).show();
			if (previousLocation == null) {
				manager.setLocation((long) race, loc.getLatitude(),
						loc.getLongitude(), loc.getAltitude(), 0,
						loc.getSpeed());
			} else {
				manager.setLocation((long) race, loc.getLatitude(),
						loc.getLongitude(), loc.getAltitude(),
						loc.distanceTo(previousLocation), loc.getSpeed());
			}
			previousLocation = loc;

			FinishOnLimit();
			
			final Intent intent = new Intent(
					"com.spongesoft.bananarun.LOCATION_UPDATED");
			intent.putExtra("data", loc);
			sendBroadcast(intent);

		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		final IntentFilter myFilter = new IntentFilter(
				"com.spongesoft.bananarun.TO_SERVICE");
		registerReceiver(mReceiver, myFilter);

		Bundle extras = intent.getExtras();
		race = extras.getInt("race_id");

		// Toast.makeText(this, "Service started, race id: "+race,
		// Toast.LENGTH_LONG).show();
		manager = new DBManagement(this);
		createNotification();
		manager.open();

		run();

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {

		unregisterReceiver(mReceiver);
		this.stopForeground(true);

		super.onDestroy();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void run() {

		final Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		Location location = locationManager
				.getLastKnownLocation(locationManager.GPS_PROVIDER);
		manager.setLocation((long) race, location.getLatitude(),
				location.getLongitude(), location.getAltitude(), 0,
				location.getSpeed());
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		pickerType = prefs.getInt("pickerType", 2);
		pickerValue = prefs.getInt("pickerValue", -1);
		unitsSystem = prefs.getString("prefUnitSystem", "1");

		// Define a listener that responds to location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 0, 0, this);
	}

	private void createNotification() {
		Intent notificationIntent = new Intent(this, SessionActivity.class);
		notificationIntent.putExtra("stopNotif", true);
		notificationIntent.putExtra("race_id", (int) race);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 12345,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationManager nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = this.getResources();
		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.preferences_button)
				// .setLargeIcon(BitmapFactory.decodeResource(res,
				// R.drawable.some_big_img))
				.setTicker("Now running!").setWhen(System.currentTimeMillis())
				.setAutoCancel(true).setContentTitle("Running!")
				.setContentText("Click to see statistics and more!");
		Notification n = builder.getNotification();
		nm.notify(143214321, n);

		startForeground(143214321, n);
	}

	private void stopReceiving() {
		locationManager.removeUpdates(this);
		this.stopSelf();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			Log.d("BroadcastService",
					"Service received: " + intent.getCharSequenceExtra("data"));
			String message = intent.getStringExtra("data");
			if (message.equals("stop")) {
				// Stop the service and mark the race as over
				manager.updateRace((long) race);
				manager.close();
				running = false;
				stopReceiving();
			}
		}
	};

	private void FinishOnLimit() {

		if (pickerType != 2) {
			
			manager.updateRace((long) race);
			infoArray =  manager.getParamsForSpecificRace((long)race);
			if (pickerType == 1) {
				
				
				int distance = (int) infoArray[4];
				if(unitsSystem.equals("1")){
					distance = distance/1000;
				} else if(unitsSystem.equals("2")){
					distance = (int) (distance*1.609);
				}
				
				if(distance >= pickerValue){
					manager.close();
					running = false;
					stopReceiving();
					
					Log.d("mierda", "mierda!");
					sendBroadcast(new Intent("xyz"));
				}
			} else if(pickerType == 0){
				int time = (int) (infoArray[3]/60);
				Log.d("mierda", time +":"+ pickerValue);
				if(time >= pickerValue) {
					manager.close();
					running = false;
					stopReceiving();
					
					Log.d("mierda", "mierda!");
					sendBroadcast(new Intent("xyz"));
				}
				
			}
		}
	}

}
