package com.shaheen.developer.consorstopvpn;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.shaheen.developer.consorstopvpn.Adapter.ApplicationListAdapter;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;
import com.shaheen.developer.consorstopvpn.Models.PInfo;
import com.shaheen.developer.consorstopvpn.SqlieDataBase.DatabaseHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplitTunnelingActivity extends AppCompatActivity {


    Toolbar toolbar;
    SwitchCompat split_switch;
    RecyclerView mRecyclerView;
    ApplicationListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ImageView search, cancel;
    SearchView searchView;
    ArrayList<PInfo> arrayList = new ArrayList<>();
    LinearLayout no_list_layout,list_layout, searchLayour, switchLayout;
    ProgressBar progress_circular;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_tunneling);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.split_tunneling);


        databaseHelper = new DatabaseHelper(SplitTunnelingActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progress_circular = (ProgressBar) findViewById(R.id.progress_circular);
        searchView = (SearchView) findViewById(R.id.searchView);
        switchLayout = (LinearLayout) findViewById(R.id.switchLayout);
        searchLayour = (LinearLayout) findViewById(R.id.searchLayour);
        no_list_layout = (LinearLayout) findViewById(R.id.no_list_layout);
        list_layout = (LinearLayout) findViewById(R.id.list_layout);
        cancel = (ImageView) findViewById(R.id.cancel);
        search = (ImageView) findViewById(R.id.search);
        split_switch = (SwitchCompat)findViewById(R.id.split_switch);



        SharedPreferences pref = getSharedPreferences("CONNECTION_DATA", Context.MODE_PRIVATE);
        if (pref!=null){
            String split_onoff =  pref.getString("split_onoff",null);
            if (split_onoff!=null){
                if (split_onoff.equals("ON")){
                    split_switch.setChecked(true);
                    ShowItem(true);
                }else {
                    split_switch.setChecked(false);
                    ShowItem(false);
                }
            }else {
                Log.d("shani","st_country prefs is null..");
                ShowItem(false);
                split_switch.setChecked(false);
            }
        }else {
            ShowItem(false);
            split_switch.setChecked(false);
        }



        split_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ShowItem(isChecked);
            }
        });




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (s.length()<1){
                    SetAdapter();
                }else {
                    ApplyFilters(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length()<1){
                    SetAdapter();
                }else {
                    ApplyFilters(s);
                }
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLayout.setVisibility(View.GONE);
                searchLayour.setVisibility(View.VISIBLE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayour.setVisibility(View.GONE);
                switchLayout.setVisibility(View.VISIBLE);
                SetAdapter();
            }
        });


    }

    public void ShowItem(boolean isChecked){

        split_switch.setClickable(false);

        SharedPreferences sharedPref = getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (isChecked){

            no_list_layout.setVisibility(View.GONE);
            progress_circular.setVisibility(View.VISIBLE);
            MyAsyncTask asyncTask = new MyAsyncTask();
            asyncTask.execute();
            editor.putString("split_onoff","ON");

        }else {

            split_switch.setClickable(true);
            no_list_layout.setVisibility(View.VISIBLE);
            list_layout.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            editor.putString("split_onoff","OFF");
        }
        editor.apply();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String  successed = "success";
            arrayList = getInstalledApps(false);
            UpdateList();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    SetAdapter();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            split_switch.setClickable(true);
                            no_list_layout.setVisibility(View.GONE);
                            list_layout.setVisibility(View.VISIBLE);
                            progress_circular.setVisibility(View.GONE);
                            search.setVisibility(View.VISIBLE);
                            Log.d("shani","list get success.....");
                        }
                    },3000);

                }
            });

            return successed;
        }

        protected void onPostExecute(String success) {
            Log.d("shani","onPostExecute called.......");
        }
    }

    public void UpdateList(){
        int updateIndex = 0;
        for (int i = 0; i <arrayList.size() ; i++) {
            if ( CheckExistence(arrayList.get(i).getPname())){
                Collections.swap(arrayList, i, updateIndex);
                updateIndex++;
                Log.d("shani","swaped............"+arrayList.get(i).getPname());
            }
        }
    }

    private boolean CheckExistence(String packageName){
        boolean result = false;

        ArrayList<PInfo> list = databaseHelper.getAllAPPs();
        for (int i = 0; i <list.size() ; i++) {
            if (list.get(i).getPname().equals(packageName)){
                result = true;
            }
        }

        return result;
    }

    public void ApplyFilters(String string){

        ArrayList<PInfo> searchTemList = new ArrayList<>();
        for (int i = 0; i <arrayList.size() ; i++) {
            String appName_lower = arrayList.get(i).getAppname().toLowerCase();
            String string_lower = string.toLowerCase();
            if (appName_lower.contains(string_lower)){
                searchTemList.add(arrayList.get(i));
            }
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicationListAdapter(searchTemList, SplitTunnelingActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void SetAdapter(){

        Log.d("shani","in adapter......");

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicationListAdapter(arrayList, SplitTunnelingActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }

            PInfo newInfo = new PInfo();
            newInfo.setAppname( p.applicationInfo.loadLabel(getPackageManager()).toString());
            newInfo.setPname(p.packageName);
            newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
            res.add(newInfo);
        }
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("shani","called....");
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
