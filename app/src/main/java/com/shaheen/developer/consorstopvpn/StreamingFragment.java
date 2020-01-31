package com.shaheen.developer.consorstopvpn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shaheen.developer.consorstopvpn.API.APIs;
import com.shaheen.developer.consorstopvpn.Adapter.LocationListAdapter;
import com.shaheen.developer.consorstopvpn.Adapter.StreamingListAdapter;
import com.shaheen.developer.consorstopvpn.Models.ChannelListModel;
import com.shaheen.developer.consorstopvpn.Models.StreamingLocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StreamingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    public StreamingFragment() {}
    public static StreamingFragment newInstance(String param1, String param2) {
        StreamingFragment fragment = new StreamingFragment();
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


    View v;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private StreamingListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<ChannelListModel> arrayList = new ArrayList<>();
    ProgressBar progress_circular;
    TextView status;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_streaming, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        progress_circular = (ProgressBar) v.findViewById(R.id.progress_circular);
        status = (TextView) v.findViewById(R.id.status);
        searchView = (SearchView)v.findViewById(R.id.searchView);

        progress_circular.setVisibility(View.VISIBLE);
        status.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if (MainActivity.haveInternet){

            if (AtomDemoApplicationController.getInstance().GetAccessToken().getAccessToken()!=null){
                GetChannelList();
            }else {
                GetAccessToken();
            }

        }else {
            progress_circular.setVisibility(View.GONE);
            status.setVisibility(View.VISIBLE);
            status.setText(getResources().getString(R.string.no_internet));
            recyclerView.setVisibility(View.GONE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               /* if (s.length()<1){
                    SetAdapter();
                }else {
                    ApplyFilters(s);
                }*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                Log.d("shani","textChange....."+s);
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

    private void GetAccessToken() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
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

                        GetChannelList();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_list), Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText(getResources().getString(R.string.error_list));
                    recyclerView.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error getting list.", Toast.LENGTH_SHORT).show();
                progress_circular.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.error_list));
                recyclerView.setVisibility(View.GONE);
            }
        });
        mRequestQueue.add(request);




    }


    private void ApplyFilters(String string){

        Log.d("shani","in apply filter......");
        ArrayList<ChannelListModel> searchTemList = new ArrayList<>();
        for (int i = 0; i <arrayList.size() ; i++) {
            String appName_lower = arrayList.get(i).getName().toLowerCase();
            String string_lower = string.toLowerCase();
            if (appName_lower.contains(string_lower)){
                searchTemList.add(arrayList.get(i));
            }
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StreamingListAdapter(searchTemList, getActivity(),StreamingFragment.this);
        recyclerView.setAdapter(mAdapter);
    }

    private void SetAdapter(){

        progress_circular.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StreamingListAdapter(arrayList, getActivity(),StreamingFragment.this);
        recyclerView.setAdapter(mAdapter);

    }


    public void RefreshStreamList(){

        Intent data = new Intent();
        String text = "streaming";
        data.setData(Uri.parse(text));
        getActivity().setResult(RESULT_OK, data);
        getActivity().finish();
    }


    private void GetChannelList() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

        HashMap<String, String> params = new HashMap<>();
        /*params.put("grantType", "secret");
        params.put("secretKey", getResources().getString(R.string.atom_secret_key));
*/
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIs.GET_ChannelsList, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.d("shani","response chanells ..... "+response);
                // Some code

                try {
                    JSONObject header = response.getJSONObject("header");
                    if (header.getString("message").equals("Success")){

                        JSONObject body = response.getJSONObject("body");
                        JSONArray channelArray = body.getJSONArray("channels");
                        for (int i = 0; i <channelArray.length() ; i++) {


                            JSONObject channelObj = channelArray.getJSONObject(i);
                            String id = channelObj.getString("id");
                            String name = channelObj.getString("name");
                            String channel_url = channelObj.getString("channel_url");
                            String order = channelObj.getString("order");
                            String icon_url = channelObj.getString("icon_url");
                            String package_name_android_tv = channelObj.getString("package_name_android_tv");
                            String package_name_android = channelObj.getString("package_name_android");
                            String package_name_amazon_fs = channelObj.getString("package_name_amazon_fs");
                            String is_free = channelObj.getString("is_free");
                            String is_new = channelObj.getString("is_new");

                            ChannelListModel channelListModel = new ChannelListModel(id,name,channel_url,order,icon_url,package_name_android_tv,package_name_android,package_name_amazon_fs,is_free,is_new);
                            arrayList.add(channelListModel);

                        }
                        SetAdapter();

                    }else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_list), Toast.LENGTH_SHORT).show();
                        progress_circular.setVisibility(View.GONE);
                        status.setVisibility(View.VISIBLE);
                        status.setText(getResources().getString(R.string.error_list));
                        recyclerView.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_list), Toast.LENGTH_SHORT).show();
                    Log.d("shani","json exception....."+e.getMessage());
                    progress_circular.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText(getResources().getString(R.string.error_list));
                    recyclerView.setVisibility(View.GONE);
                }catch (NullPointerException e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_list), Toast.LENGTH_SHORT).show();
                    Log.d("shani","NullPointerException exception....."+e.getMessage());
                    progress_circular.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status.setText(getResources().getString(R.string.error_list));
                    recyclerView.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_list), Toast.LENGTH_SHORT).show();
                Log.d("shani","volley error......."+ error.getMessage());

                progress_circular.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.error_list));
                recyclerView.setVisibility(View.GONE);

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                Log.d("shani","tokkkk......."+ AtomDemoApplicationController.getInstance().GetAccessToken().getAccessToken());
                headers.put("X-AccessToken", AtomDemoApplicationController.getInstance().GetAccessToken().getAccessToken());
                return headers;
            }
        };
        mRequestQueue.add(request);




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
