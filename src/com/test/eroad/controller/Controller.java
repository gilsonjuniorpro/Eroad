package com.test.eroad.controller;

import org.json.JSONException;

import com.test.eroad.dto.RetornoDTO;
import com.test.eroad.service.Service;

public class Controller {

	public static String getWS(String url) {

		RetornoDTO retornoDTO = Service.getWS(url);
		String timeZone = null;
		try {
			if(retornoDTO.getObjeto() != null){
				if(("OK").equals(retornoDTO.getObjeto().getString("status"))){

					timeZone = retornoDTO.getObjeto().getString("timeZoneName");
					/*
					"dstOffset" : 3600,
				    "rawOffset" : 43200,
				    "status" : "OK",
				    "timeZoneId" : "Pacific/Auckland",
				    "timeZoneName" : "New Zealand Daylight Time"
				    */
				}				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return timeZone;
	}
}
