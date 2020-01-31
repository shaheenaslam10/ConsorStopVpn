package com.shaheen.developer.consorstopvpn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.atom.core.models.AccessToken;
import com.atom.core.models.Country;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.atom.sdk.android.exceptions.AtomException;
import com.google.gson.Gson;
import com.shaheen.developer.consorstopvpn.Adapter.ApplicationListAdapter;
import com.shaheen.developer.consorstopvpn.Adapter.LocationListAdapter;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;
import com.shaheen.developer.consorstopvpn.Models.PInfo;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class LocationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    public LocationFragment() {}
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private SearchView searchView;
    private RecyclerView recyclerView;
    private LocationListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Country> arrayList = new ArrayList<>();
    ProgressBar progress_circular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location, container, false);

        progress_circular = (ProgressBar) v.findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);
        searchView = (SearchView)v.findViewById(R.id.searchView);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);


        AtomDemoApplicationController.getInstance().getAtomManager().getCountries(new CollectionCallback<Country>() {
            @Override
            public void onSuccess(List<Country> list) {

                arrayList.addAll(list);
                SetAdapter();
            }

            @Override
            public void onError(AtomException e) {}

            @Override
            public void onNetworkError(AtomException e) {}
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


        return v;
    }

    public void RefreshLocation(){

        Intent data = new Intent();
        String text = "country";
        data.setData(Uri.parse(text));
        getActivity().setResult(RESULT_OK, data);
        getActivity().finish();
    }

    private void SetAdapter(){

        progress_circular.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationListAdapter(arrayList, getActivity(),LocationFragment.this);
        recyclerView.setAdapter(mAdapter);
    }

    private void ApplyFilters(String string){

        ArrayList<Country> searchTemList = new ArrayList<>();
        for (int i = 0; i <arrayList.size() ; i++) {
            String appName_lower = arrayList.get(i).getName().toLowerCase();
            String string_lower = string.toLowerCase();
            if (appName_lower.contains(string_lower)){
                searchTemList.add(arrayList.get(i));
            }
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationListAdapter(searchTemList, getActivity(),LocationFragment.this);
        recyclerView.setAdapter(mAdapter);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
