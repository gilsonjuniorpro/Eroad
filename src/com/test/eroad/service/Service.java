package com.test.eroad.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.eroad.dto.RetornoDTO;
import com.test.eroad.util.Util;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class Service {

public static RetornoDTO getWS(String url) {
		
		JSONObject jsonObject = null;
		InputStream is = null;
		RetornoDTO retornoDTO = new RetornoDTO();
		
		try {
			JSONObject jsonObj = new JSONObject();			
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("json", jsonObj.toString()));

			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Accept-Encoding", "compress, gzip");
			httpPost.setHeader("Accept", "application/json");
	
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
		    entity.setContentEncoding(HTTP.UTF_8);
		    entity.setContentType("application/json");
		    httpPost.setEntity(entity);
	
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			is = AndroidHttpClient.getUngzippedContent(httpResponse.getEntity());

	        String json = Util.converteGzipEmJson(is);
				
			if(!json.isEmpty()){
				jsonObject = new JSONObject(json);
				retornoDTO.setObjeto(jsonObject);
			}
			retornoDTO.setHttpStatus(httpResponse.getStatusLine().getStatusCode());
			
		} catch (UnsupportedEncodingException e) {
			Log.e("e", e.toString());
		} catch (ClientProtocolException e) {
			Log.e("e", e.toString());
		} catch (JSONException e) {
			Log.e("e", e.toString());
		} catch (IOException e) {
			Log.e("e", e.toString());
		}

		return retornoDTO;
	}
	
}
