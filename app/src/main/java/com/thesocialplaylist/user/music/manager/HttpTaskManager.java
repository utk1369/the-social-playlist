package com.thesocialplaylist.user.music.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thesocialplaylist.user.music.dto.AppHttpResponse;
import com.thesocialplaylist.user.music.dto.HttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by user on 16-07-2016.
 */
public class HttpTaskManager extends AsyncTask<Object, Void, Object> {

    private Context mContext;
    private ProgressDialog progressDialog;
    private HttpResponseHandler httpResponseHandler;

    public HttpTaskManager(Context appContext) {
        super();
        this.mContext = appContext;
    }

    public void setHttpResponseHandler(HttpResponseHandler httpResponseHandler) {
        this.httpResponseHandler = httpResponseHandler;
    }

    @Override
    protected Object doInBackground(Object... params) {
        String urlString = params[0].toString();
        String requestMethod = "GET";
        String payload = null;
        if(params.length > 1) {
            requestMethod = "POST";
            payload = params[1].toString();
        }
        URL url = null;
        try {
            url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(25*1000);
            if(requestMethod == "POST")
                sendPayload(conn, payload);

            Integer responseCode = conn.getResponseCode();
            InputStream responseStream = new BufferedInputStream(conn.getInputStream());
            Log.i("Http Response", responseCode + ": " + responseStream);
            conn.disconnect();

            AppHttpResponse appHttpResponse = new AppHttpResponse();
            appHttpResponse.setResponseCode(responseCode);
            appHttpResponse.setData(new JSONArray(getResponseText(responseStream)));

            return appHttpResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        httpResponseHandler.handleHttpResponse(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    private void sendPayload(HttpURLConnection conn, String payload) throws IOException {
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStreamWriter requestStream = new OutputStreamWriter(conn.getOutputStream());
        requestStream.write(payload);
        requestStream.flush();
        requestStream.close();
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}
