package com.test.eroad.dto;

import org.json.JSONObject;

public class RetornoDTO {
	private JSONObject objeto;
	
	private Integer httpStatus;

	public JSONObject getObjeto() {
		return objeto;
	}

	public void setObjeto(JSONObject objeto) {
		this.objeto = objeto;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}
}
