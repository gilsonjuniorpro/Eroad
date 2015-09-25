package com.test.eroad;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.eroad.location.GPSTracker;
import com.test.eroad.location.RotaTask;
import com.test.eroad.util.Dominios;
import com.test.eroad.util.Util;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    Context context;
    GPSTracker gps;
    Boolean isConected = false;
    //Double longitude = null;
	//Double latitude = null;
	//Nes Zealand address for test route 
	Double longitude = 174.712727;
	Double latitude = -36.722491;
	
	List<Address> addressesFrom;
    List<Address> addressesTo;
    String initialAddress = null;
	String finalAddress = null;
	Double latitudeEroad = null;
	Double longitudeEroad = null;
	Long time;
	GoogleMap map;
	
	String utcTime = null;
	TimeZone timeZone = null;
	Boolean isRouteDrawed = false;
	
	public static final String EROAD_ADDRESS = "260 Oteha Valley Road, Albany, Auckland 0632, New Zealand";
	public static final int MAX_RESULTS = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		
		Geocoder gc = new Geocoder(getApplicationContext(), new Locale("en", "NZ"));
		try {
			List<Address> list = gc.getFromLocationName(EROAD_ADDRESS, MAX_RESULTS);

			for(Address address:list){
				latitudeEroad = address.getLatitude();
				longitudeEroad = address.getLongitude();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(Dominios.STATUS_GPS_ATIVO){
			gps = new GPSTracker(context);
			isConected = Util.conectado(context);
			if(isConected) {	
				if(gps.canGetLocation()) {
					//longitude = gps.getLongitude();
					//latitude = gps.getLatitude();
				}
			}
			/*
			time = gps.getLocation().getTime();
			Date date = new Date(time);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			utcTime = sdf.format(date);
			Toast.makeText(context, "UTC: " + utcTime, Toast.LENGTH_LONG).show();
			timeZone = sdf.getTimeZone();
			Toast.makeText(context, "Timezone: " + timeZone, Toast.LENGTH_LONG).show();
			*/
		}
		
		
		LatLng myPointLocation = new LatLng(latitude,longitude);
		LatLng eroadPointLocation = new LatLng(latitudeEroad,longitudeEroad);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
		
		addMarker(map, myPointLocation, eroadPointLocation);
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPointLocation , 12));
		map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
		
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.getUiSettings().setCompassEnabled(true); 
		map.setMyLocationEnabled(true);
	}
	
	
	
	public void getRoute(GoogleMap map, Double latitudeTo, Double longitudeTo){
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		
		try {
			addressesFrom = geocoder.getFromLocation(latitude, longitude, 1);
			
			initialAddress = Util.retiraAcento(addressesFrom.get(0).getAddressLine(0)); 

		} catch (IOException e) {
			e.printStackTrace();
		}

        try {
			addressesTo = geocoder.getFromLocation(latitudeTo, longitudeTo, 1);
			
			finalAddress = Util.retiraAcento(addressesTo.get(0).getAddressLine(0)); 

		} catch (IOException e) {
			e.printStackTrace();
		}

        new RotaTask(this, map, initialAddress, finalAddress).execute();
	}

	
	public void addMarker(final GoogleMap map, LatLng userLatLng, LatLng eroadLatLng){
		
		Marker markerUser = map.addMarker(new MarkerOptions()
				.position(userLatLng)
				.title("My Location")
				.snippet("User location"));
		
		Marker markerEroad = map.addMarker(new MarkerOptions()
				.position(eroadLatLng)
				.title("Eroad Location")
				.snippet("Eroad location")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.eroad)));
		
		map.setInfoWindowAdapter(new InfoWindowAdapter(){

			@Override
			public View getInfoContents(Marker marker) {
				
				if(marker.getTitle().equals("Eroad Location")){
					if(isRouteDrawed == false){
						getRoute(map, latitudeEroad, longitudeEroad);
						isRouteDrawed = true;
					}
            	}
				
				LinearLayout linear = new LinearLayout(getBaseContext());
				linear.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				linear.setOrientation(LinearLayout.VERTICAL);
				
				TextView text = new TextView(getBaseContext());
				text.setText("Eroad Test");
				text.setTextColor(Color.BLACK);
				text.setGravity(Gravity.CENTER);
				linear.addView(text);
				
				TextView title = new TextView(getBaseContext());
				title.setText(marker.getTitle());
				title.setTextColor(Color.RED);
				title.setGravity(Gravity.CENTER);
				linear.addView(title);
				
				TextView snippet = new TextView(getBaseContext());
				snippet.setText(marker.getSnippet());
				snippet.setTextColor(Color.BLUE);
				snippet.setGravity(Gravity.CENTER);
				linear.addView(snippet);

				
				
				return linear;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				//View content = c
				
				return null;
			}
			
		});
		
	}
	
	
	
	
}
