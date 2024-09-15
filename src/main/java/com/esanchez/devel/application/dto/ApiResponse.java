package com.esanchez.devel.application.dto;

public class ApiResponse {

	private String status;
	
	public ApiResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ApiResponse [status=" + status + "]";
	}
}
