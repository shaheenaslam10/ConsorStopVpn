package com.shaheen.developer.consorstopvpn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.atom.core.models.AccessToken;
import com.atom.sdk.android.AtomManager;
import com.shaheen.developer.consorstopvpn.API.APIs;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    String navigation = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //java.lang.OutOfMemoryError: Failed to allocate a 64163084 byte allocation with 10593528 free bytes and 10MB until OOM

        try {
            SaveCurrentDate(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA",MODE_PRIVATE);
        if (sharedPreferences!=null){
            if (sharedPreferences.getBoolean("login",false)){

                if (sharedPreferences.getString("login_time",null)!=null  &&  !sharedPreferences.getString("login_time",null).equals("")){

                    String  previousDate_ST = sharedPreferences.getString("login_time",null);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    Date myDate = null;
                    try {
                        myDate = format.parse(previousDate_ST);
                        myDate.setDate(myDate.getDate()+1);

                        Date currentTime1 = Calendar.getInstance().getTime();

                        if (myDate.before(currentTime1)){
                            navigation = "login";
                            Log.d("shani","in after ...... "+myDate+"......=======....."+currentTime1);
                            AddHandler();
                        }else {

                            AtomDemoApplicationController.getInstance().getAtomManager().setUUID(sharedPreferences.getString("uu_id",null));
                            navigation = "main";
                            Log.d("shani","1loginfalse.....");
                            GetAccessToken();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d("shani","2loginfalse....."+e.getMessage());
                        navigation = "login";
                        AddHandler();
                    }

                }else {
                    navigation = "login";
                    Log.d("shani","3loginfalse.....");
                    AddHandler();
                }


            }else {
                Log.d("shani","4loginfalse.....");
                navigation = "login";
                AddHandler();
            }
        }else {
            navigation = "login";
            Log.d("shani","5loginfalse.....");
            AddHandler();
        }




    }
    public boolean IsVpnConnected() {
        if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(SplashActivity.this).equals(AtomManager.VPNStatus.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("shani","data..splash..........."+data);
        Log.d("shani","data..splash..........."+data.getData());
    }

    public void SaveCurrentDate(boolean empty) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);

        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (empty){
            editor.putString("start_time", myDate);
        }else {
            Log.d("shani","innnnnnnnnnnnnnnnnnnnnnnnnnnn..............");
            editor.putString("start_time", "null");
        }
        editor.apply();
    }

    public void AddHandler(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (navigation.equals("main")){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }

    private void GetAccessToken() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(SplashActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("grantType", "secret");
        params.put("secretKey", getResources().getString(R.string.atom_secret_key));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APIs.GET_ACCESS_TOKEN, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("shani","response ..... "+response);
                // Some code
                /*{"header":{"code":1,"message":"Access token created successfully!","response_code":1},"body":{"accessToken":"94e683059c3855ffe30d1b9bd03e71c2d070be2986bbf365505c4cce266d2a66","refreshToken":"9b297246947e4301bcaa7267dc95f5d62b64d559d2ee8c8d30d2701d60162bb0","expiry":3600,"resellerId":"443","resellerUid":"res_5d4d5945a6207"}}*/

                AddHandler();
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
            public void onErrorResponse(VolleyError error) {
                //handle errors
                AddHandler();
            }
        });
        mRequestQueue.add(request);




    }


    @Override
    protected void onResume() {
        super.onResume();

        String oldLanguage = LocaleManager.getLanguage(this);
        String language = LocaleManager.getLocale(this.getResources()).getLanguage();


        Log.d("shani","oldLanguage....."+oldLanguage);
        Log.d("shani","language....."+language);

        if (!oldLanguage.equals(language)){
            LocaleManager.setNewLocale(this,LocaleManager.getLanguage(this));
            finish();
            startActivity(getIntent());
        }
    }
}
