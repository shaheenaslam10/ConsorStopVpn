package com.shaheen.developer.consorstopvpn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.atom.core.models.AccessToken;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.shaheen.developer.consorstopvpn.API.APIs;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;
import com.shaheen.developer.consorstopvpn.Models.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    ImageView cross;
    TextView forgot_pass;
    Button login;
    EditText username;
    ShowHidePasswordEditText password;
    private static String uniqueID = "";
    ProgressBar progress_circular;
    AppCompatCheckBox remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        remember_me = (AppCompatCheckBox) findViewById(R.id.remember_me);
        progress_circular = (ProgressBar) findViewById(R.id.progress_circular);
        username = (EditText) findViewById(R.id.username);
        password = (ShowHidePasswordEditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        forgot_pass = (TextView) findViewById(R.id.forgot_pass);
        cross = (ImageView)findViewById(R.id.cross);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText()==null  ||  username.getText().toString().length()<1){
                    username.setError(getResources().getString(R.string.username_error));
                    Toast.makeText(LoginActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
                }else if (password.getText()==null  ||  password.getText().toString().length()<1){
                    password.setError(getResources().getString(R.string.password_error));
                    Toast.makeText(LoginActivity.this, "password is empty", Toast.LENGTH_SHORT).show();
                }else {
                    SaveRemember(remember_me.isChecked());
                    Login();
                }
            }
        });



        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APIs.FORGOTPASSLINK));
                startActivity(browserIntent);
            }
        });


        remember_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    if (Build.VERSION.SDK_INT < 21) {
                        CompoundButtonCompat.setButtonTintList(remember_me, ColorStateList.valueOf(getResources().getColor(R.color.orange)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                    } else {
                        remember_me.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));//setButtonTintList is accessible directly on API>19
                    }
                }else {
                    if (Build.VERSION.SDK_INT < 21) {
                        CompoundButtonCompat.setButtonTintList(remember_me, ColorStateList.valueOf(getResources().getColor(R.color.gray_light)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                    } else {
                        remember_me.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_light)));//setButtonTintList is accessible directly on API>19
                    }
                }
                //SaveRemember(b);
            }
        });
        if (CheckRememberMe()){
            remember_me.setChecked(true);
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(remember_me, ColorStateList.valueOf(getResources().getColor(R.color.orange)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                remember_me.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));//setButtonTintList is accessible directly on API>19
            }
        }else {
            remember_me.setChecked(false);
        }

    }

    public boolean CheckRememberMe(){
        boolean remember_me = false;
        SharedPreferences pref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        if (pref!=null){
            remember_me =  pref.getBoolean("remember_me",false);
            if (remember_me){
                if (pref.getString("username",null)!=null){
                    username.setText(pref.getString("username",null));
                    password.setText(pref.getString("password",null));
                }
            }
        }
        Log.d("shani","remember me ..... "+remember_me);
        return remember_me;
    }

    public void SaveRemember(boolean remember){
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("remember_me",remember);
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
    }

    private void Login() {

        progress_circular.setVisibility(View.VISIBLE);

        new RequestAsync().execute(username.getText().toString(),password.getText().toString());

    }


    private void GetAccessToken() {

       RequestQueue mRequestQueue = Volley.newRequestQueue(LoginActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("grantType", "secret");
        params.put("secretKey", getResources().getString(R.string.atom_secret_key));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APIs.GET_ACCESS_TOKEN, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("shani","response ..... "+response);
                // Some code
                /*{"header":{"code":1,"message":"Access token created successfully!","response_code":1},"body":{"accessToken":"94e683059c3855ffe30d1b9bd03e71c2d070be2986bbf365505c4cce266d2a66","refreshToken":"9b297246947e4301bcaa7267dc95f5d62b64d559d2ee8c8d30d2701d60162bb0","expiry":3600,"resellerId":"443","resellerUid":"res_5d4d5945a6207"}}*/

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

                        GenerateVpnAccount();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("shani","Error GetAccessToken "+e.getMessage());
                    Toast.makeText(LoginActivity.this, " Error occurred.", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle errors
                Log.d("shani","Error GetAccessToken VolleyError "+error.getMessage());
                Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                progress_circular.setVisibility(View.GONE);
            }
        });
        mRequestQueue.add(request);



    }
    public void GenerateVpnAccount() {

        Log.d("shani","uniqueID..........."+uniqueID);

        RequestQueue mRequestQueue = Volley.newRequestQueue(LoginActivity.this);

        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uniqueID);
        params.put("period", "180");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APIs.GENERATE_VPN_ACCOUNT, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("shani","response ..... "+response);
                // Some code
                /*{"header":{"code":1,"message":"Access token created successfully!","response_code":1},"body":{"accessToken":"94e683059c3855ffe30d1b9bd03e71c2d070be2986bbf365505c4cce266d2a66","refreshToken":"9b297246947e4301bcaa7267dc95f5d62b64d559d2ee8c8d30d2701d60162bb0","expiry":3600,"resellerId":"443","resellerUid":"res_5d4d5945a6207"}}*/

                try {

                    JSONObject header = response.getJSONObject("header");
                    if (header.getString("message").equals("VPN account has been generated")) {


                        Log.d("shani", "message......." + header.getString("message"));
                        Log.d("shani", "vpnUsername......." + response.getJSONObject("body").getString("vpnUsername"));

                        String vpn_username = response.getJSONObject("body").getString("vpnUsername");
                        String vpn_password = response.getJSONObject("body").getString("vpnPassword");

                        AtomDemoApplicationController.getInstance().getAtomManager().setUUID(uniqueID);

                        Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = getSharedPreferences("CONNECTION_DATA", MODE_PRIVATE).edit();
                        editor.putString("vpn_username", vpn_username);
                        editor.putString("vpn_password", vpn_password);
                        editor.apply();

                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                        String formattedDate = format.format(c);

                        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor_login = sharedPref.edit();
                        editor_login.putBoolean("login",true);
                        editor_login.putString("login_time",formattedDate);
                        editor_login.apply();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();

                            }
                        });


                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                                progress_circular.setVisibility(View.GONE);
                                Log.d("shani", "somethings went wrong account generate....");
                            }
                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                            progress_circular.setVisibility(View.GONE);
                        }
                    });
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle errors
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                        progress_circular.setVisibility(View.GONE);
                    }
                });
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("X-AccessToken", AtomDemoApplicationController.getInstance().GetAccessToken().getAccessToken());
                Log.d("shani","tokkkk......."+ AtomDemoApplicationController.getInstance().GetAccessToken().getAccessToken());
                return headers;
            }
        };
        mRequestQueue.add(request);



    }
    public class RequestAsync extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {

                Log.d("shani","username.................................."+strings[0]);

                // POST Request
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", strings[0]);
                postDataParams.put("password", strings[1]);

                return RequestHandler.sendPost(APIs.LOGIN,postDataParams);
            }
            catch(Exception e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error occurred. try catch", Toast.LENGTH_SHORT).show();
                        progress_circular.setVisibility(View.GONE);
                    }
                });
                return new String("Exception: " + e.getMessage());
            }
        }


        @Override
        protected void onPostExecute(String s) {
            if(s!=null){
                Log.d("shani","respinse........................"+s);

                if (s.equals("Exception: Unable to resolve host \"csvpn.com\": No address associated with hostname")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Check your internet connection, and try again.", Toast.LENGTH_SHORT).show();
                            progress_circular.setVisibility(View.GONE);
                        }
                    });

                }else {
                    try {
                        //{"uu_id":"102","status":1,"message":"Congratulations you Are successfuly  login"}
                        //{"id":"102","status":1,"message":"Congratulations you Are successfuly  login"}
                        //{"status":0,"message":"Login Failed"}

                        JSONObject object = new JSONObject(s);
                        String status = object.getString("status");
                        if (status.equals("0")){

                            if (object.getString("message").equals("your account is Disable, Please contact from admin")){

                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("You account is disabled. Please contact with admin to enable your account.")
                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                finish();
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                finish();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();


                            }else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                                        progress_circular.setVisibility(View.GONE);
                                    }
                                });

                            }

                        }else if (status.equals("1")){

                            uniqueID = object.getString("uu_id");

                            Log.d("shani","UUUUUUUUUUUUUUUUUUUUUUIDDDDDDDDD........... "+uniqueID);

                            SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("uu_id",object.getString("uu_id"));
                            editor.apply();

                            if (AtomDemoApplicationController.getInstance().GetAccessToken()==null){
                                GetAccessToken();
                            }else {
                                GenerateVpnAccount();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                                progress_circular.setVisibility(View.GONE);
                            }
                        });
                    }
                }





            }else {
                Log.d("shani","error respinse........................");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                        progress_circular.setVisibility(View.GONE);
                    }
                });
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String oldLanguage = LocaleManager.getLanguage(this);
        String language = LocaleManager.getLocale(this.getResources()).getLanguage();

        if (!oldLanguage.equals(language)){
            LocaleManager.setNewLocale(this,LocaleManager.getLanguage(this));
            finish();
            startActivity(getIntent());
        }
    }

}
