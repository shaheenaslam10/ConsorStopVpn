package com.shaheen.developer.consorstopvpn;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;

public class StreamLocationActivity extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    LinearLayout streaming_layout, location_layout;
    ImageView stream_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_location);


        location_layout = (LinearLayout)findViewById(R.id.location_layout);
        streaming_layout = (LinearLayout)findViewById(R.id.streaming_layout);
        stream_icon = (ImageView) findViewById(R.id.stream_icon);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        location_layout.setOnClickListener(this);
        streaming_layout.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.getStringExtra("activity").equals("stream")){

            ReplaceStreamFragment();

        }else if (intent.getStringExtra("activity").equals("location")){

            ReplaceLocationFragment();
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("shani","called....");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.streaming_layout){

            streaming_layout.setBackgroundColor(getResources().getColor(R.color.orange));
            location_layout.setBackgroundColor(getResources().getColor(R.color.white));

            ClearBackStack();
            ReplaceStreamFragment();


        }else if (v.getId() == R.id.location_layout){
            streaming_layout.setBackgroundColor(getResources().getColor(R.color.white));
            location_layout.setBackgroundColor(getResources().getColor(R.color.orange));

            ClearBackStack();
            ReplaceLocationFragment();

        }

    }


    public void ReplaceStreamFragment(){

        getSupportActionBar().setTitle(getResources().getString(R.string.streaming));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        StreamingFragment fragment = new StreamingFragment();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

        streaming_layout.setBackground(getResources().getDrawable(R.drawable.stream_tab_border_active));
        location_layout.setBackground(getResources().getDrawable(R.drawable.location_tab_border));
        stream_icon.setImageDrawable(getResources().getDrawable(R.drawable.streaming_active));

    }
    public void ReplaceLocationFragment(){

        getSupportActionBar().setTitle(getResources().getString(R.string.location));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LocationFragment fragment = new LocationFragment();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

        streaming_layout.setBackground(getResources().getDrawable(R.drawable.stream_tab_border));
        location_layout.setBackground(getResources().getDrawable(R.drawable.location_tab_border_active));
        stream_icon.setImageDrawable(getResources().getDrawable(R.drawable.streaming_active));
    }

    public void ClearBackStack(){
        FragmentManager fm = this.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
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
