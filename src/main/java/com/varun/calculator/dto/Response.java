package com.varun.calculator.dto;

/**
 * The POJO class is used by the controller to return the result
 * 
 * @author Varun Srivastava
 *
 */
public class Response {

	public Response() {
	}

	public Response(String error, String result) {
		super();
		this.error = error;
		this.result = result;
	}

	private String error;

	private String result;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
