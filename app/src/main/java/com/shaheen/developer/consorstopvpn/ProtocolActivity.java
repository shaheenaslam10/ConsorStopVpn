package com.shaheen.developer.consorstopvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.atom.core.models.Country;
import com.google.gson.Gson;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;

public class ProtocolActivity extends AppCompatActivity {

    Toolbar toolbar;
    RadioGroup protocol_group;
    AppCompatRadioButton udp_btn,tcp_btn,both_btn;
    LinearLayout udp_container, tcp_container, both_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);


        both_container = (LinearLayout) findViewById(R.id.both_container);
        udp_container = (LinearLayout) findViewById(R.id.udp_container);
        tcp_container = (LinearLayout) findViewById(R.id.tcp_container);

        both_btn = (AppCompatRadioButton)findViewById(R.id.both_btn);
        udp_btn = (AppCompatRadioButton)findViewById(R.id.udp_btn);
        tcp_btn = (AppCompatRadioButton)findViewById(R.id.tcp_btn);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        protocol_group = (RadioGroup) findViewById(R.id.protocol_group);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.protocol);


        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref!=null){
            String protocol =  pref.getString("protocol",null);
            if (protocol!=null){
                if (protocol.equals("tcp")){
                    tcp_btn.setChecked(true);
                }else  if (protocol.equals("udp")){
                    udp_btn.setChecked(true);
                }else {
                    both_btn.setChecked(true);
                }
            }else {
                both_btn.setChecked(true);
            }
        }else {
            both_btn.setChecked(true);
        }





        both_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                both_btn.setChecked(true);
            }
        });
        udp_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udp_btn.setChecked(true);
            }
        });
        tcp_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcp_btn.setChecked(true);
            }
        });


        protocol_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.d("shani","checked Changes........");

                AppCompatRadioButton checkedRadioButton = (AppCompatRadioButton)group.findViewById(checkedId);
                SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if (checkedRadioButton.getId() == R.id.tcp_btn){
                    editor.putString("protocol","tcp");
                }else if (checkedRadioButton.getId() == R.id.udp_btn){
                    editor.putString("protocol","udp");
                }else {
                    editor.putString("protocol","both");
                }
                editor.apply();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
