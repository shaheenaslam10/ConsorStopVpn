package com.shaheen.developer.consorstopvpn;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.atom.core.models.AccessToken;
import com.shaheen.developer.consorstopvpn.API.APIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GenericAsyncTask extends AsyncTask<Void, Void, String> {



    public interface DownloadCompleteHandler {
        public void handleDownloadComplete(String result);
    }

    private DownloadCompleteHandler handler;
    private String url;
    private Context context;

    public GenericAsyncTask(DownloadCompleteHandler handler, String url, Context context) {
        this.handler = handler;
        this.url = url;
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... voids) {

        if (url.equals(APIs.GET_ACCESS_TOKEN)){
            downloadAccessToken(url);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        handler.handleDownloadComplete(result);
    }


    private Void downloadAccessToken(String urlStr) {

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("grantType", "secret");
        params.put("secretKey", context.getResources().getString(R.string.atom_secret_key));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlStr, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject header = response.getJSONObject("header");
                    if (header.getString("message").equals("Access token created successfully!")) {

                        JSONObject body = response.getJSONObject("body");
                        String accessToken = body.getString("accessToken");
                        String refreshToken = body.getString("refreshToken");
                        int expiry = body.getInt("expiry");
                        String resellerId = body.getString("resellerId");
                        String resellerUid = body.getString("resellerUid");

                        AccessToken token = new AccessToken();

                        token.setAccessToken(accessToken);
                        token.setRefreshToken(refreshToken);
                        token.setExpiry(expiry);
                        token.setResellerId(resellerId);
                        token.setResellerUid(resellerUid);

                        AtomDemoApplicationController.getInstance().SetAccessToken(token);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        mRequestQueue.add(request);

        return null;
    }

}
