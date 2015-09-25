package com.test.eroad.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.gms.vision.barcode.Barcode.GeoPoint;
import com.test.eroad.location.GPSTracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Util {
	
	static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
			
	public static String converteGzipEmJson(InputStream is) throws IOException{
		
		byte[] buffer = new byte[1024];
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((numRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, numRead);
        }

        is.close();
        
		return new String(baos.toByteArray());
	}
	
	
	
	
	
	static String get_xml_server_reponse(String server_url){

	    URL xml_server = null;
	    String xmltext = "";
	    InputStream input;

	    try {
	        xml_server = new URL(server_url);

	        try {
	            input = xml_server.openConnection().getInputStream();


	            final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	            final StringBuilder sBuf = new StringBuilder();

	            String line = null;
	            try {
	                while ((line = reader.readLine()) != null) 
	                {
	                    sBuf.append(line);
	                }
	               } 
	            catch (IOException e) 
	              {
	                    Log.e(e.getMessage(), "XML parser, stream2string 1");
	              } 
	            finally {
	                try {
	                    input.close();
	                    }
	                catch (IOException e) 
	                {
	                    Log.e(e.getMessage(), "XML parser, stream2string 2");
	                }
	            }

	            xmltext =  sBuf.toString();

	        } catch (IOException e1) {

	                e1.printStackTrace();
	        }


	        } catch (MalformedURLException e1) {

	          e1.printStackTrace();
	        }

	     return  xmltext;

	  }     


	 private static String get_UTC_Datetime_from_timestamp(long timeStamp){

	    try{

	        Calendar cal = Calendar.getInstance();
	        TimeZone tz = cal.getTimeZone();

	        int tzt = tz.getOffset(System.currentTimeMillis());

	        timeStamp -= tzt;

	        // DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	        DateFormat sdf = new SimpleDateFormat();
	        Date netDate = (new Date(timeStamp));
	        return sdf.format(netDate);
	    }
	    catch(Exception ex){
	        return "";
	     }
	    } 


	 public static String getTimeZone(GeoPoint gp){

	        String erg = "";
	        String raw_offset = "";
	        String dst_offset = "";

	        double Longitude = gp.lat;
	        double Latitude = gp.lng;

	        // String request = "http://ws.geonames.org/timezone?lat="+Latitude+"&lng="+ Longitude+ "&style=full";


	        long tsLong = 0; // System.currentTimeMillis()/1000;

	        NtpUtcTime client = new NtpUtcTime();

	        if (client.requestTime("pool.ntp.org", 2000)) {              
	          tsLong = client.getNtpTime();
	        }

	        if (tsLong != 0)
	        {

	        tsLong = tsLong  / 1000;

	        // https://maps.googleapis.com/maps/api/timezone/xml?location=39.6034810,-119.6822510&timestamp=1331161200&sensor=true

	        String request = "https://maps.googleapis.com/maps/api/timezone/xml?location="+Latitude+","+ Longitude+ "&timestamp="+tsLong +"&sensor=true";

	        String xmltext = get_xml_server_reponse(request);

	        if(xmltext.compareTo("")!= 0)
	        {

	         int startpos = xmltext.indexOf("<TimeZoneResponse");
	         xmltext = xmltext.substring(startpos);



	        XmlPullParser parser;
	        try {
	            parser = XmlPullParserFactory.newInstance().newPullParser();


	             parser.setInput(new StringReader (xmltext));

	             int eventType = parser.getEventType();  

	             String tagName = "";


	             while(eventType != XmlPullParser.END_DOCUMENT) {
	                 switch(eventType) {

	                     case XmlPullParser.START_TAG:

	                           tagName = parser.getName();

	                         break;


	                     case XmlPullParser.TEXT :


	                        if  (tagName.equalsIgnoreCase("raw_offset"))
	                          if(raw_offset.compareTo("")== 0)                               
	                            raw_offset = parser.getText();  

	                        if  (tagName.equalsIgnoreCase("dst_offset"))
	                          if(dst_offset.compareTo("")== 0)
	                            dst_offset = parser.getText();  


	                        break;   

	                 }

	                 try {
	                        eventType = parser.next();
	                    } catch (IOException e) {

	                        e.printStackTrace();
	                    }

	                }

	                } catch (XmlPullParserException e) {

	                    e.printStackTrace();
	                    erg += e.toString();
	                }

	        }      

	        int ro = 0;
	        if(raw_offset.compareTo("")!= 0)
	        { 
	            float rof = str_to_float(raw_offset);
	            ro = (int)rof;
	        }

	        int dof = 0;
	        if(dst_offset.compareTo("")!= 0)
	        { 
	            float doff = str_to_float(dst_offset);
	            dof = (int)doff;
	        }

	        tsLong = (tsLong + ro + dof) * 1000;



	        erg = get_UTC_Datetime_from_timestamp(tsLong);
	        }


	  return erg;

	}





	private static float str_to_float(String dst_offset) {
		return Float.parseFloat(dst_offset);
	}
	
	
	
	
	public static Date getUTCdatetimeAsDate(String data)
	{
	    return stringDateToDate(getUTCdatetimeAsString(data));
	}

	public static String getUTCdatetimeAsString(String data)
	{
	    final SimpleDateFormat sdf = new SimpleDateFormat(data);
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    final String utcTime = sdf.format(new Date());

	    return utcTime;
	}

	public static Date stringDateToDate(String data)
	{
	    Date dateToReturn = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat(data);

	    try
	    {
	        dateToReturn = (Date)dateFormat.parse(data);
	    }
	    catch (ParseException e)
	    {
	        e.printStackTrace();
	    }

	    return dateToReturn;
	}
	
	
	
    //verifica o status de conexao WIFI / 3G / 4G
	public static boolean conectado(Context context) {
		try{
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null){
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null){
					for (int i = 0; i < info.length; i++){
						if (info[i].getState() == NetworkInfo.State.CONNECTED){
							return true;
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
            Log.e("Util", e.getMessage());
            return false;
		}
    }
	
	
	
	
	public static void getGpsInformation(Context context, GPSTracker gps, Double longitude, Double latitude){
		if(gps.canGetLocation()) {
			longitude = gps.getLongitude();
			latitude = gps.getLatitude();
			//precisao = gps.getLocation().getAccuracy();
		}			
	}
	
	
    public static String retiraAcento(String campo){
    	String stringacentos="á,ã,à,â,é,è,ê,í,ì,î,ó,ò,õ,ô,ú,ù,û,Á,À,Ã,Â,É,È,Ê,Í,Ì,Î,Ó,Ò,Õ,Ô,Ú,Ù,Û,ç,Ç";
    	String stringsemacentos="a,a,a,a,e,e,e,i,i,i,o,o,o,o,u,u,u,A,A,A,A,E,E,E,I,I,I,O,O,O,O,U,U,U,c,C";
    	int stringTam = 35;
    	String stracentosexplode[] = stringacentos.split(",");
    	String strsemacentosexplode[] = stringsemacentos.split(",");
    	int campoTam = campo.length();
    	int cont = 0;
    	StringBuilder camposemacento= new StringBuilder();
    	int flag=0;
        int contacento=0;
        String caracter;
        String campo2[] = campo.split("");
    	while(cont <= campoTam )
    	{
    		caracter = campo2[cont];
    		while (contacento< stringTam && flag==0)
    		{
    			if(stracentosexplode[contacento].equals(caracter))
      			{
    				camposemacento.append(strsemacentosexplode[contacento]);
    				flag=1;
    			}
    			contacento++;
    		}
    		if(flag==0)
    		{
    			camposemacento.append(caracter);
    		}
    		else
    		{
    			flag=0;
    		}
    		cont++;
    		contacento=0;
    	}
    	return camposemacento.toString();
    }

	
	
}
