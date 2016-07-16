package com.example.user.music.models;

import org.json.JSONArray;

/**
 * Created by user on 16-07-2016.
 */
public class AppHttpResponse {
    private int responseCode;
    private JSONArray data;
    private String statusText;
    private JSONArray errorResponse;
    private JSONArray validationErrors;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public JSONArray getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(JSONArray errorResponse) {
        this.errorResponse = errorResponse;
    }

    public JSONArray getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(JSONArray validationErrors) {
        this.validationErrors = validationErrors;
    }
}
