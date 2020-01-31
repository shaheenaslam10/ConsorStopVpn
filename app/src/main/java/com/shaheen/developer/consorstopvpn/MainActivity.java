package com.shaheen.developer.consorstopvpn;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.atom.core.models.Channel;
import com.atom.core.models.Country;
import com.atom.core.models.Protocol;
import com.atom.sdk.android.AtomManager;
import com.atom.sdk.android.ConnectionDetails;
import com.atom.sdk.android.VPNProperties;
import com.atom.sdk.android.VPNStateListener;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.atom.sdk.android.exceptions.AtomException;
import com.atom.sdk.android.exceptions.AtomValidationException;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;
import com.shaheen.developer.consorstopvpn.Models.AppData;
import com.shaheen.developer.consorstopvpn.Models.ChannelListModel;
import com.shaheen.developer.consorstopvpn.Models.CountryImageMatcher;
import com.shaheen.developer.consorstopvpn.Models.PInfo;
import com.shaheen.developer.consorstopvpn.SqlieDataBase.DatabaseHelper;
import com.shaheen.developer.consorstopvpn.SqlieDataBase.SQLiteDatabaseHandler;
import com.shaheen.developer.consorstopvpn.Utils.NetworkUtil;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, VPNStateListener, LocationListener {

    RippleBackground rippleBackground, rippleBackground_main;
    ImageView connect_image, connectedCountryIcon;
    LinearLayout streaming_layout, location_layout, connected_layout, connected_countryLayout;
    RelativeLayout not_connect_layout;
    public static final int LOCATION_CODE = 239;
    public static final int STREAM_CODE = 229;
    TextView secure, location, version, connectedName, connect_status, opt_location, opt_streaming, tap_to_connect,connect_to;
    Chronometer cmTimer;
    long elapsedTime;
    boolean resume = false, fromOtherActivity = false;
    Protocol c_Protocol_tcp, c_Protocol_udp;
    List<Protocol> protocolList;
    DatabaseHelper databaseHelper;
    Context context;
    DrawerLayout drawer;
    NavigationView navigationView;
    String main_status = "nothing";
    Handler handler;
    LoadingDots dots;
    static boolean haveInternet = true;
    boolean toastDisabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("shani", "checking onCreate.....");

        AtomManager.addVPNStateListener(this);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");

        Log.d("shani", "network state....." + NetworkUtil.getConnectivityStatusString(context));
        connect_to = (TextView) findViewById(R.id.connect_to);
        tap_to_connect = (TextView) findViewById(R.id.tap_to_connect);
        opt_streaming = (TextView) findViewById(R.id.opt_streaming);
        opt_location = (TextView) findViewById(R.id.opt_location);
        dots = (LoadingDots) findViewById(R.id.dots);
        cmTimer = (Chronometer) findViewById(R.id.connect_time);
        connect_status = (TextView) findViewById(R.id.connect_status);
        connectedName = (TextView) findViewById(R.id.connectedName);
        secure = (TextView) findViewById(R.id.secure);
        location = (TextView) findViewById(R.id.location);
        streaming_layout = (LinearLayout) findViewById(R.id.streaming_layout);
        location_layout = (LinearLayout) findViewById(R.id.location_layout);
        connected_layout = (LinearLayout) findViewById(R.id.connected_layout);
        connected_countryLayout = (LinearLayout) findViewById(R.id.connected_countryLayout);
        not_connect_layout = (RelativeLayout) findViewById(R.id.not_connect_layout);

        connect_image = (ImageView) findViewById(R.id.connect_image);
        connectedCountryIcon = (ImageView) findViewById(R.id.connectedIcon);
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground_main = (RippleBackground) findViewById(R.id.content_main);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        CheckSelectedFirstTime();
        UpdateLocation(null);
        GetProtocols();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.text_color));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(1).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(2).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(3).setActionView(R.layout.action_icon_layout);
        navigationView.getMenu().getItem(4).setActionView(R.layout.action_icon_layout);

        version = (TextView) navigationView.findViewById(R.id.version);
        SetVersion();

        streaming_layout.setOnClickListener(this);
        connected_countryLayout.setOnClickListener(this);
        location_layout.setOnClickListener(this);
        connect_image.setOnClickListener(this);
        location.setOnClickListener(this);

        CheckStatusAndUpdate();

        Log.d("shani", "status...." + IsVpnConnected());
        Log.d("shani", "ReturnMillis()...." + ReturnMillis());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IsVpnConnected()) {
                    MakeConnectionWhileDisconnected();
                }
            }
        }, 1000);


    }


    public void CheckSelectedFirstTime() {

        boolean getList = false;
        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref != null) {
            String connection_type = pref.getString("connection_type", null);
            if (connection_type != null) {
                if (!connection_type.equals("streaming") && !connection_type.equals("country")) {
                    getList = true;
                }
            } else {
                getList = true;
            }
        } else {
            getList = true;
        }

        if (getList) {
            AtomDemoApplicationController.getInstance().getAtomManager().getCountries(new CollectionCallback<Country>() {
                @Override
                public void onSuccess(List<Country> list) {

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals("United States")) {
                            Gson gson = new Gson();
                            String countryToGson = gson.toJson(list.get(i));
                            SharedPreferences sharedPref = context.getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("connection_type", "country");
                            editor.putString("country_detail", countryToGson);
                            editor.apply();

                            UpdateConnectedCountryDropDown();
                        }
                    }
                }

                @Override
                public void onError(AtomException e) {
                }

                @Override
                public void onNetworkError(AtomException e) {
                }
            });
        }

    }

    public void GetProtocols() {

        AtomDemoApplicationController.getInstance().getAtomManager().getProtocols(new CollectionCallback<Protocol>() {
            @Override
            public void onSuccess(List<Protocol> protocols) {
                protocolList = protocols;
                for (int i = 0; i < protocolList.size(); i++) {
                    if (protocolList.get(i).getName().equals("TCP")) {
                        c_Protocol_tcp = protocolList.get(i);
                    }
                    if (protocolList.get(i).getName().equals("UDP")) {
                        c_Protocol_udp = protocolList.get(i);
                    }
                }
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onError(AtomException atomException) {
                Log.d("shani", atomException.getMessage() + " : " + atomException.getCode());
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onNetworkError(AtomException atomException) {
                Log.d("shani", atomException.getMessage() + " : " + atomException.getCode());
            }
        });
    }

    public void MakeConnection() {

        connect_status.setText(getResources().getString(R.string.checking_status));

        if (!haveInternet) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        } else {
            if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this).equals(AtomManager.VPNStatus.DISCONNECTED)) {

                VPNProperties.Builder vpnProperties = null;
                boolean possible = false;
                SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                if (pref != null) {

                    String connection_type = pref.getString("connection_type", null);
                    if (connection_type != null) {

                        if (connection_type.equals("streaming")) {

                            Channel channelMain = new Channel();
                            Gson gson = new Gson();
                            if (pref != null) {
                                String st_streaming = pref.getString("streaming_detail", null);
                                if (st_streaming != null) {
                                    ChannelListModel chanel = gson.fromJson(st_streaming, ChannelListModel.class);
                                    channelMain.setId(Integer.parseInt(chanel.getId()));
                                    channelMain.setName(chanel.getName());
                                    channelMain.setIconUrl(chanel.getIcon_url());
                                    channelMain.setOrder(Integer.parseInt(chanel.getOrder()));
                                    channelMain.setUrl(chanel.getChannel_url());
                                    channelMain.setPackageNameAndroid(chanel.getPackage_name_android());
                                    channelMain.setPackageNameAndroidTv(chanel.getPackage_name_android_tv());
                                    channelMain.setPackageNameAmazonFireStick(chanel.getPackage_name_amazon_fs());
                                    try {
                                        possible = true;
                                        vpnProperties = new VPNProperties.Builder(channelMain, c_Protocol_tcp);
                                    } catch (AtomValidationException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d("shani", "st_streaming prefs is null..");
                                }
                            } else {
                                Log.d("shani", "chanel prefs is null..");
                            }

                        } else if (connection_type.equals("country")) {

                            Gson gson_c = new Gson();
                            SharedPreferences pref_c = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                            if (pref_c != null) {
                                String st_country = pref_c.getString("country_detail", null);
                                if (st_country != null) {
                                    Country country = gson_c.fromJson(st_country, Country.class);

                                    try {
                                        possible = true;
                                        vpnProperties = new VPNProperties.Builder(country, c_Protocol_tcp);
                                    } catch (AtomValidationException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d("shani", "st_country prefs is null..");
                                }
                            } else {
                                Log.d("shani", "country prefs is null..");
                            }

                        } else {

                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.choose_country_or_channel), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, getResources().getString(R.string.choose_country_or_channel), Toast.LENGTH_SHORT).show();
                }


                if (possible) {
                    // check for app list

                    SharedPreferences prefsplit = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                    if (prefsplit != null) {
                        String split_onoff = prefsplit.getString("split_onoff", null);
                        if (split_onoff != null) {
                            if (split_onoff.equals("ON")) {
                                ArrayList<PInfo> list = databaseHelper.getAllAPPs();
                                String[] app_list = new String[list.size()];
                                boolean contains = false;
//                            Log.d("shani","list size.........."+list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    app_list[i] = list.get(i).getPname();
//                                Log.d("shani", "app_list ..... " + app_list[i]);
                                    contains = true;
                                }
                                if (contains) {
                                    vpnProperties.withSplitTunneling(app_list);
                                }
                            }
                        }
                    }

                    vpnProperties.withSecondaryProtocol(c_Protocol_udp);

                    connected_layout.setVisibility(View.VISIBLE);
                    cmTimer.setVisibility(View.INVISIBLE);
                    connect_status.setVisibility(View.VISIBLE);
                    not_connect_layout.setVisibility(View.GONE);
                    dots.setVisibility(View.VISIBLE);
                    LayoutClickUpdate(false);
                    AtomDemoApplicationController.getInstance().getAtomManager().connect(MainActivity.this, vpnProperties.build());
                    CheckStateAndRunAgain();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.choose_country_or_channel), Toast.LENGTH_SHORT).show();
                }

            } else if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this).equals(AtomManager.VPNStatus.CONNECTED)) {
                Log.d("shani", "in connected , perform disconnect....");
                try {
                    if (fromOtherActivity) {
                        SaveCurrentDate(true);
                    } else {
                        SaveCurrentDate(false);
                    }
                    fromOtherActivity = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                AtomDemoApplicationController.getInstance().getAtomManager().disconnect(MainActivity.this);
                connected_layout.setVisibility(View.GONE);
                not_connect_layout.setVisibility(View.VISIBLE);

            } else if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this).equals(AtomManager.VPNStatus.CONNECTING)) {
                Log.d("shani", "in connecting , perform cancel....");
                try {
                    if (fromOtherActivity) {
                        SaveCurrentDate(true);
                    } else {
                        SaveCurrentDate(false);
                    }
                    fromOtherActivity = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                AtomDemoApplicationController.getInstance().getAtomManager().cancel(MainActivity.this);
                connected_layout.setVisibility(View.GONE);
                not_connect_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void MakeConnectionWhileDisconnected() {

        long timerValue = 0;
        long timerValue_elapsed = 0;

        Log.d("shani", "in MakeConnectionWhileDisconnected");


        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref != null) {
            String timer_elapsed = pref.getString("timer_elapsed", null);
            String timer = pref.getString("timer", null);
            if (timer_elapsed != null  &&  timer != null) {
               Log.d("shani","timer_elapsed ++++++++++++++++++++++  ");
                timerValue = Long.parseLong(timer);
                timerValue_elapsed = Long.parseLong(timer_elapsed);
            }
        }

        main_status = "connected";
        connect_status.setText(getResources().getString(R.string.connectedddd));
        connected_layout.setVisibility(View.VISIBLE);
        cmTimer.setVisibility(View.INVISIBLE);
        connect_status.setVisibility(View.VISIBLE);
        not_connect_layout.setVisibility(View.GONE);
        dots.setVisibility(View.VISIBLE);
        ConnectedLayoutSettingStarting(timerValue, timerValue_elapsed);
    }

    public void CheckStateAndRunAgain() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String s = main_status;
                if (s.equals("Validating") || s.equals("Getting fastest server") || s.equals("Generating new user")) {
                    //handler.postDelayed(this, 4000);
                    Log.d("shani", "Retrying.......");
                    MakeConnection();
                }
            }
        }, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri data_uri = data.getData();
            fromOtherActivity = true;
            Log.d("shani", "3data_uri ..... " + data_uri);
            if (IsVpnConnected()) {
                Log.d("shani", "vpn connecyted already..... ");
                try {
                    SaveCurrentDate(true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CancelVPNConnection();
            }
            UpdateConnectedCountryDropDown();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MakeConnection();
                        }
                    }, 1000);
                }
            }, 500);

        } else {
            Log.d("shani", "mull data..... ");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.split_tunnel) {
            startActivity(new Intent(MainActivity.this, SplitTunnelingActivity.class));
        } else if (id == R.id.protocol) {
            startActivity(new Intent(MainActivity.this, ProtocolActivity.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.logout) {
            ShowLogoutDialog();
        } else if (id == R.id.action_change_language) {


            String vpn_status = AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this);
            Log.d("shani", "status before language........" + vpn_status);
            Log.d("shani", "status before language........" + VPNState.DISCONNECTED);
            if (vpn_status.equalsIgnoreCase(VPNState.CONNECTED)) {
                CannotShowLanguageDialog();
            } else if (vpn_status.equalsIgnoreCase(VPNState.DISCONNECTED)) {
                showChangeLangDialog();
            } else if (vpn_status.equalsIgnoreCase(VPNState.CONNECTING)) {
                CannotShowLanguageDialog();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showChangeLangDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);
        if (LocaleManager.getLanguage(MainActivity.this).equals("pl")) {
            spinner1.setSelection(1);
        } else {
            spinner1.setSelection(0);
        }

        dialogBuilder.setTitle(getResources().getString(R.string.language_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.language_dialog_message));
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch (langpos) {
                    case 0: //English
                        setNewLocale("en", false);
                        CheckStatusAndUpdate();
                        return;
                    case 1: //Polish
                        setNewLocale("pl", false);
                        CheckStatusAndUpdate();
                        return;
                    default: //By default set to english
                        setNewLocale("en", false);
                        CheckStatusAndUpdate();
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void CannotShowLanguageDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(getResources().getString(R.string.language_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.language_dialog_message_cannot));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void ShowLogoutDialog() {

        String message = null;
        if (IsVpnConnected()) {
            message = getResources().getString(R.string.disconnect_logout_confirmation);
        } else {
            message = getResources().getString(R.string.logout_confirmation);
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.logout))
                .setMessage(message)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        CancelVPNConnection();

                        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("login", false);
                        editor.apply();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.streaming_layout:

                Intent intent = new Intent(MainActivity.this, StreamLocationActivity.class);
                intent.putExtra("activity", "stream");
                startActivityForResult(intent, STREAM_CODE);

                break;
            case R.id.location_layout:
                Intent intent1 = new Intent(MainActivity.this, StreamLocationActivity.class);
                intent1.putExtra("activity", "location");
                startActivityForResult(intent1, LOCATION_CODE);

                break;
            case R.id.connect_image:

                if (haveInternet) {
                    if (!IsVpnConnected()) {
                        Animations();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MakeConnection();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.connected_countryLayout:

                GoToStreamOrLocation();

                break;

            case R.id.location:


                ShowChannelNavigationDialog();

                break;


            default:
                break;
        }
    }

    public void GoToStreamOrLocation() {

        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref != null) {
            String connection_type = pref.getString("connection_type", null);
            if (connection_type != null) {
                if (connection_type.equals("country")) {
                    Intent intent1 = new Intent(MainActivity.this, StreamLocationActivity.class);
                    intent1.putExtra("activity", "location");
                    startActivityForResult(intent1, LOCATION_CODE);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, StreamLocationActivity.class);
                    intent1.putExtra("activity", "stream");
                    startActivityForResult(intent1, LOCATION_CODE);
                }
            } else {
                Intent intent1 = new Intent(MainActivity.this, StreamLocationActivity.class);
                intent1.putExtra("activity", "location");
                startActivityForResult(intent1, LOCATION_CODE);
            }
        } else {
            Intent intent1 = new Intent(MainActivity.this, StreamLocationActivity.class);
            intent1.putExtra("activity", "location");
            startActivityForResult(intent1, LOCATION_CODE);
        }
    }

    public void Animations() {

        rippleBackground.startRippleAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(connect_image, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(connect_image, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        connect_image.setVisibility(View.VISIBLE);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rippleBackground.stopRippleAnimation();
            }
        }, 1500);
    }

    public void StopAnimations() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(connect_image, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(connect_image, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        connect_image.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    public void SetVersion() {
        try {
            String versionName = this.getPackageManager()
                    .getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
            version.setText(getResources().getString(R.string.app_version) + "  " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void CheckStatusAndUpdate() {


        opt_location.setText(getResources().getString(R.string.location));
        opt_streaming.setText(getResources().getString(R.string.streaming));
        tap_to_connect.setText(getResources().getString(R.string.tap_to_connect));
        connect_to.setText(getResources().getString(R.string.connect_to));

        String vpn_status = AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this);
        if (vpn_status.equals(VPNState.CONNECTED)) {
            Log.d("shani", "connected__________________________________________");
            ConnectedLayoutSetting();
        } else if (vpn_status.equals(VPNState.DISCONNECTED)) {
            Log.d("shani", "disconnected__________________________________________");
            DisconnectedLayoutSetting();
        } else {
            Log.d("shani", "disconnected__________________________________________");
            DisconnectedLayoutSetting();
        }
    }

    public void ConnectedLayoutSettingStarting(long starting_value, long timerValue_elapsed) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dots.setVisibility(View.GONE);
                LayoutClickUpdate(true);

                cmTimer.setVisibility(View.VISIBLE);
                cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer arg0) {
                        if (!resume) {
                            long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) / 60;
                            long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) % 60;
                            elapsedTime = SystemClock.elapsedRealtime();
                        } else {
                            long minutes = ((elapsedTime - cmTimer.getBase()) / 1000) / 60;
                            long seconds = ((elapsedTime - cmTimer.getBase()) / 1000) % 60;
                            elapsedTime = elapsedTime + 1000;
                        }

                        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr....... " + String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
                        Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr elapsed....... " + String.valueOf(SystemClock.elapsedRealtime()));
                        editor.putString("timer", String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
                        editor.putString("timer_elapsed", String.valueOf(SystemClock.elapsedRealtime()));
                        editor.apply();
                    }
                });
                Log.d("shani", "ellapsed Time ........ " + starting_value);
                Log.d("shani", "ellapsed Time ........ " + SystemClock.elapsedRealtime());
                Log.d("shani", "ellapsed Time ........ " + (SystemClock.elapsedRealtime() - starting_value));
//                cmTimer.setBase(SystemClock.elapsedRealtime() - starting_value);
//                cmTimer.setBase(SystemClock.elapsedRealtime() - timerValue_elapsed);
                cmTimer.setBase(SystemClock.elapsedRealtime() - ((SystemClock.elapsedRealtime() - timerValue_elapsed)+starting_value));
                cmTimer.start();

                connect_image.setImageDrawable(getResources().getDrawable(R.drawable.tap_connected));
                UpdateConnectedCountryDropDown();
                boolean isCountry = false;
                SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                if (pref != null) {
                    String connection_type = pref.getString("connection_type", null);
                    if (connection_type != null) {
                        if (connection_type.equals("country")) {
                            isCountry = true;
                        } else {
                            ShowChannelNavigationDialog();
                        }
                    }
                }
                if (isCountry) {
                    UpdateLocation(AtomDemoApplicationController.getInstance().getAtomManager().getConnectionDetails().getCountry());
                }
                secure.setText(getResources().getString(R.string.secured));
                rippleBackground_main.startRippleAnimation();
            }
        });

    }

    public void ConnectedLayoutSetting() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dots.setVisibility(View.GONE);
                LayoutClickUpdate(true);
                ShowTimer();
                connect_image.setImageDrawable(getResources().getDrawable(R.drawable.tap_connected));
                UpdateConnectedCountryDropDown();
                boolean isCountry = false;
                SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                if (pref != null) {
                    String connection_type = pref.getString("connection_type", null);
                    if (connection_type != null) {
                        if (connection_type.equals("country")) {
                            isCountry = true;
                        } else {
                            ShowChannelNavigationDialog();
                        }
                    }
                }
                if (isCountry) {
                    UpdateLocation(AtomDemoApplicationController.getInstance().getAtomManager().getConnectionDetails().getCountry());
                }
                secure.setText(getResources().getString(R.string.secured));
                rippleBackground_main.startRippleAnimation();
            }
        });

    }

    public void ShowChannelNavigationDialog() {

        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref != null) {
            String connection_type = pref.getString("connection_type", null);
            if (connection_type != null) {
                if (connection_type.equals("streaming")) {

                    String streaming_detail = pref.getString("streaming_detail", null);
                    location.setVisibility(View.VISIBLE);
                    location.setText(getResources().getString(R.string.go_to_channel));

                    Gson gson = new Gson();
                    ChannelListModel chanel = gson.fromJson(streaming_detail, ChannelListModel.class);
                    String message = "You are connected to channel " + chanel.getName() + " . Do you want to navigate to this channel now?";

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Channel")
                            .setMessage(message)
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chanel.getChannel_url()));
                                    startActivity(browserIntent);

                                }
                            })
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("Cancel", null)
                            .setIcon(R.drawable.play_activated)
                            .show();
                }
            }
        }


    }

    public void ShowTimer() {

        cmTimer.setVisibility(View.VISIBLE);
        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                if (!resume) {
                    long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) / 60;
                    long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) % 60;
                    elapsedTime = SystemClock.elapsedRealtime();
                } else {
                    long minutes = ((elapsedTime - cmTimer.getBase()) / 1000) / 60;
                    long seconds = ((elapsedTime - cmTimer.getBase()) / 1000) % 60;
                    elapsedTime = elapsedTime + 1000;
                }
                SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr....... " + String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
                Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr elapsed....... " + String.valueOf(SystemClock.elapsedRealtime()));
                editor.putString("timer", String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
                editor.putString("timer_elapsed", String.valueOf(SystemClock.elapsedRealtime()));
                editor.apply();
            }
        });
        Log.d("shani", "ellapsed Time ........ " + SystemClock.elapsedRealtime());
        cmTimer.setBase(SystemClock.elapsedRealtime() - ReturnMillis());
        cmTimer.start();
    }

    public long ReturnMillis() {

        long lng = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);
        Date Date1 = null;
        try {
            Date1 = sdf.parse(myDate);
            SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
            if (sharedPref != null) {
                String sharedDate = sharedPref.getString("start_time", null);

                Log.d("shani", "sharedDate....." + sharedDate);
                if (sharedDate != null && !sharedDate.equals("null")) {
                    Date Date2 = sdf.parse(sharedDate);
                    long millse = Date1.getTime() - Date2.getTime();
                    long mills = Math.abs(millse);
                    Log.d("shani", "time in millis....." + mills);
                    Log.d("shani", "sharedDate if....." + sharedDate);
//                    lng = mills;

                    lng = Long.getLong(sharedDate);
                } else {
                    lng = 0;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lng;
    }

    public void SaveCurrentDate(boolean empty) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);

        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (empty) {
            Log.d("shani", "date save save save save....... " + cmTimer.getBase());
            Log.d("shani", "date save save save save....... " + String.valueOf(SystemClock.elapsedRealtime()));
            Log.d("shani", "date save save save save....... " + String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
            editor.putString("start_time", String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
            editor.apply();
        } else {
            Log.d("shani", "innnnnnnnnnnnnnnnnnnnnnnnnnnn..............");
            editor.putString("start_time", "nullllll");
            editor.apply();
        }

    }

    public void DisconnectedLayoutSetting() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dots.setVisibility(View.GONE);
                LayoutClickUpdate(true);
                connected_layout.setVisibility(View.GONE);
                not_connect_layout.setVisibility(View.VISIBLE);
                connect_image.setImageDrawable(getResources().getDrawable(R.drawable.tap_connect));
                UpdateLocation(null);
                UpdateConnectedCountryDropDown();
                StopAnimations();
                rippleBackground_main.stopRippleAnimation();
            }
        });
    }

    public void UpdateConnectedCountryDropDown() {

        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref != null) {
            String connection_type = pref.getString("connection_type", null);
            if (connection_type != null) {
                if (connection_type.equals("streaming")) {
                    Gson gson = new Gson();
                    String st_streaming = pref.getString("streaming_detail", null);
                    if (st_streaming != null) {
                        ChannelListModel chanel = gson.fromJson(st_streaming, ChannelListModel.class);
                        Picasso.get()
                                .load(chanel.getIcon_url())
                                .resize(40, 40)
                                .centerInside()
                                .into(connectedCountryIcon);

                        connectedName.setText(chanel.getName());

                    } else {
                        UpdateConnectedCountryDropDownEmpty();
                    }

                } else if (connection_type.equals("country")) {

                    Gson gson = new Gson();
                    String country_detail = pref.getString("country_detail", null);
                    if (country_detail != null) {

                        Country country = gson.fromJson(country_detail, Country.class);
                        connectedName.setText(country.getName());
                        connectedCountryIcon.setImageDrawable(getResources().getDrawable(CountryImageMatcher.GetImageId(country.getName())));

                    } else {
                        UpdateConnectedCountryDropDownEmpty();
                    }
                }
            } else {
                UpdateConnectedCountryDropDownEmpty();
            }
        } else {
            UpdateConnectedCountryDropDownEmpty();
        }
    }

    public void UpdateConnectedCountryDropDownEmpty() {
        connectedName.setText(getResources().getString(R.string.choose_country_channel));
        connectedCountryIcon.setImageDrawable(getResources().getDrawable(R.drawable.location));
    }

    public void UpdateLocation(String server_loc) {

        if (server_loc == null) {
            secure.setText(getResources().getString(R.string.not_secured));
            location.setVisibility(View.GONE);
        } else {
            Log.d("shani", "server location is not nullllllll.....");
            location.setVisibility(View.VISIBLE);
            location.setText(server_loc);
            secure.setText(getResources().getString(R.string.secured));
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        //You had this as int. It is advised to have Lat/Loing as double.
        double lat = loc.getLatitude();
        double lng = loc.getLongitude();
        Log.d("shani", "latitude...." + lat);
        Log.d("shani", "longitude...." + lng);
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String country = addresses.get(1).getCountryName();
        String city = addresses.get(1).getLocality();
        Log.d("shani", "fnialAddress...." + city + ", " + country);
        location.setText(city + ", " + country); //This will display the final address.
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected() {
        main_status = "connected";
        Log.d("shani", "onConnected simple....");
        connect_status.setText(getResources().getString(R.string.connectedddd));
        ConnectedLayoutSetting();
    }

    @Override
    public void onConnected(ConnectionDetails connectionDetails) {
        main_status = "connected";
        Log.d("shani", "onConnected connection detail...." + connectionDetails.getServerAddress());
        Log.d("shani", "onConnected connection detail...." + connectionDetails.getCountry());
        connect_status.setText(getResources().getString(R.string.connectedddd));
        ConnectedLayoutSetting();
    }

    @Override
    public void onConnecting() {
        main_status = "connecting";
        connect_status.setText(getResources().getString(R.string.connecting));
    }

    @Override
    public void onRedialing(AtomException e, ConnectionDetails connectionDetails) {
        main_status = "redialing";
    }

    @Override
    public void onDialError(AtomException e, ConnectionDetails connectionDetails) {
        Log.d("shani", "DialError...." + e.getMessage());
        main_status = "error";
        if (e.getMessage().equals(getResources().getString(R.string.no_internet))) {
            connect_status.setText(getResources().getString(R.string.no_internet));
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        DisconnectedLayoutSetting();
    }

    @Override
    public void onStateChange(String s) {
        Log.d("shani", "state...." + s);
        main_status = s;
        if (!s.equals("Disconnected")) {
            if (s.equals("Waiting for usable network")) {
                connect_status.setText(getResources().getString(R.string.waiting_for_useabale_network));
            } else if (s.equals("Validating")) {
                connect_status.setText(getResources().getString(R.string.validating));
            } else if (s.equals("Generating new user")) {
                connect_status.setText(getResources().getString(R.string.generating_new_userssasads));
            } else if (s.equals("Getting fastest server")) {
                connect_status.setText(getResources().getString(R.string.getting_fastest_server));
            } else if (s.equals("Connecting")) {
                connect_status.setText(getResources().getString(R.string.connecting));
            } else if (s.equals("Waiting for server reply")) {
                connect_status.setText(getResources().getString(R.string.waiting_for_server_reply));
            } else if (s.equals("Server replied")) {
                connect_status.setText(getResources().getString(R.string.server_replied));
            } else if (s.equals("Connected")) {
                connect_status.setText(getResources().getString(R.string.connectedddd));
            } else if (s.equals("Disconnected")) {
                connect_status.setText(getResources().getString(R.string.disconnectedd));
            }
        }
        if (s.equals("No network available")) {
            connect_status.setText(getResources().getString(R.string.no_network_available));
            DisconnectedLayoutSetting();
        }
    }

    @Override
    public void onDisconnected(boolean b) {
        Log.d("shani", "onDisconnected...." + b);
        main_status = "disconnected";
        connect_status.setText(getResources().getString(R.string.disconnectedd));
        DisconnectedLayoutSetting();

    }

    @Override
    public void onDisconnected(ConnectionDetails connectionDetails) {
        Log.d("shani", "onDisconnected....");
        main_status = "disconnected";
        connect_status.setText(getResources().getString(R.string.disconnectedd));
        DisconnectedLayoutSetting();
    }

    @Override
    public void onPacketsTransmitted(String s, String s1) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        AtomManager.addVPNStateListener(this);

        navigationView.getMenu().getItem(0).setTitle(getResources().getString(R.string.split_tunneling));
        navigationView.getMenu().getItem(1).setTitle(getResources().getString(R.string.protocol));
        navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.language));
        navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.about));
        navigationView.getMenu().getItem(4).setTitle(getResources().getString(R.string.logout));

        opt_location.setText(getResources().getString(R.string.location));
        opt_streaming.setText(getResources().getString(R.string.streaming));
        connect_status.setText(getResources().getString(R.string.connected));
        tap_to_connect.setText(getResources().getString(R.string.tap_to_connect));
        connect_to.setText(getResources().getString(R.string.connect_to));

        Log.d("shani", "checking onResume()");

       /* IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        if (!isReceiverRegistered){
            try {
                //Register or UnRegister your broadcast receiver here
                registerReceiver(new NetworkChangeReceiver(), intentFilter);
                isReceiverRegistered = true;
            } catch(IllegalArgumentException e) {
                Log.d("shani","onResume Register receiver exception....");
                e.printStackTrace();
            }
        }*/


        String oldLanguage = LocaleManager.getLanguage(this);
        String language = LocaleManager.getLocale(this.getResources()).getLanguage();

        Log.d("shani", "oldLanguage ......" + oldLanguage);
        Log.d("shani", "language ......" + language);

        if (!oldLanguage.equals(language)) {
            Log.d("shani", "resume language ......");
            LocaleManager.setNewLocale(this, LocaleManager.getLanguage(this));
            finish();
            startActivity(getIntent());
        }

        Log.d("shani", "checking onResume() end");
    }

    @Override
    protected void onPause() {
        Log.d("shani","checking onPause()");
        super.onPause();
        Log.d("shani","checking onPause() end");
    }

    @Override
    protected void onStop() {

        Log.d("shani","checking onStop()");

      /*  if (isReceiverRegistered){
            try {
                //Register or UnRegister your broadcast receiver here
                unregisterReceiver(new NetworkChangeReceiver());
                isReceiverRegistered = false;
            } catch(IllegalArgumentException e) {
                Log.d("shani","onPause unregister receiver exception....");
                e.printStackTrace();
            }
        }*/

        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr....... " + String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
        Log.d("shani", "tiiiiiiiiiiiimeeeeeeeeeerrrrrrrrrrrrr elapsed....... " + String.valueOf(SystemClock.elapsedRealtime()));
        editor.putString("timer", String.valueOf(SystemClock.elapsedRealtime() - cmTimer.getBase()));
        editor.putString("timer_elapsed", String.valueOf(SystemClock.elapsedRealtime()));
        editor.apply();

        try {
            if (IsVpnConnected())
                SaveCurrentDate(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        super.onStop();

        Log.d("shani","checking onStop() end");
    }

    @Override
    protected void onDestroy() {
        Log.d("shani","checking onDestroy()");

        super.onDestroy();
        AtomManager.removeVPNStateListener(this);

        Log.d("shani","checking onDestroy() end");
    }

    public boolean IsVpnConnected() {
        if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this).equals(AtomManager.VPNStatus.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void CancelVPNConnection() {
        AtomDemoApplicationController.getInstance().getAtomManager().disconnect(MainActivity.this);
    }

    public void LayoutClickUpdate(boolean clickable) {

        if (clickable) {
            connected_countryLayout.setOnClickListener(this);
            streaming_layout.setOnClickListener(this);
            location_layout.setOnClickListener(this);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            connected_countryLayout.setOnClickListener(null);
            streaming_layout.setOnClickListener(null);
            location_layout.setOnClickListener(null);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.d("shani", "network receiver......" + status);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    haveInternet = false;
                    main_status = "disconnected";
                    connect_status.setText(getResources().getString(R.string.disconnectedd));
                    InternetChangeDisconnectedLayoutSetting();
                } else {
                    haveInternet = true;
                }
            }
        }
    }

    public void InternetChangeDisconnectedLayoutSetting() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!toastDisabled) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_disconnected), Toast.LENGTH_SHORT).show();
                    toastDisabled = true;
                }

                dots.setVisibility(View.GONE);
                LayoutClickUpdate(true);
                connected_layout.setVisibility(View.GONE);
                not_connect_layout.setVisibility(View.VISIBLE);
                connect_image.setImageDrawable(getResources().getDrawable(R.drawable.tap_connect));
                UpdateLocation(null);
                UpdateConnectedCountryDropDown();
                rippleBackground_main.stopRippleAnimation();
                if (IsVpnConnected()) {
                    AtomDemoApplicationController.getInstance().getAtomManager().disconnect(MainActivity.this);
                } else if (AtomDemoApplicationController.getInstance().getAtomManager().getCurrentVpnStatus(MainActivity.this) == AtomManager.VPNStatus.CONNECTING) {
                    AtomDemoApplicationController.getInstance().getAtomManager().cancel(MainActivity.this);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toastDisabled = false;
                    }
                }, 5000);
            }
        });
    }

    private boolean setNewLocale(String language, boolean restartProcess) {

        Log.d("shani", "checking locale.....");
        Log.d("shani", "selected....." + language);
        LocaleManager.setNewLocale(this, language);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        //finish();
        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Language changed.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
