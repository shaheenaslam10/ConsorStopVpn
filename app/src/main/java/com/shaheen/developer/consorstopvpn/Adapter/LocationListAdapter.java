package com.shaheen.developer.consorstopvpn.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atom.core.models.Country;
import com.google.gson.Gson;
import com.shaheen.developer.consorstopvpn.LocationFragment;
import com.shaheen.developer.consorstopvpn.Models.CountryImageMatcher;
import com.shaheen.developer.consorstopvpn.R;

import java.util.ArrayList;

public class LocationListAdapter  extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {



    private ArrayList<Country> arrayList = new ArrayList<>();
    private Context context;
    LocationFragment fragment;
    String res;

    public LocationListAdapter(ArrayList<Country> arrayList, Context context,LocationFragment fragment) {
        this.arrayList = arrayList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locations_list_item, viewGroup, false);
        return new LocationListAdapter.ViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationListAdapter.ViewHolder holder, final int position) {


        final Country model = arrayList.get(position);
        holder.name.setText(model.getName());

        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
        if (pref!=null){

            String st_country =  pref.getString("country_detail",null);
            if (st_country!=null){
                Country country = gson.fromJson(st_country,Country.class);
                if (country.getName().equals(model.getName())){
                    if (pref.getString("connection_type",null)!=null  &&  pref.getString("connection_type",null).equals("country")){
                        Log.d("shani","EQUALLLLLLLL/////.  "+country.getName()+"===="+model.getName());
                        holder.selected.setImageDrawable(context.getResources().getDrawable(R.drawable.location_list_active));
                    }else {
                        holder.selected.setImageDrawable(context.getResources().getDrawable(R.drawable.location_list_inactive));
                    }
                }
            }else {
                Log.d("shani","st_country prefs is null..");
            }
        }else {
            Log.d("shani","country prefs is null..");
        }

        if (CountryImageMatcher.GetImageId(model.getName()) != -1){
            holder.icon.setImageDrawable(context.getResources().getDrawable(CountryImageMatcher.GetImageId(model.getName())));
        }else {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.location));
        }

        holder.container_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                String countryToGson = gson.toJson(model);
                SharedPreferences sharedPref = context.getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("connection_type","country");
                editor.putString("country_detail",countryToGson);
                editor.apply();

                fragment.RefreshLocation();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView selected, icon;
        LinearLayout container_location;


        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            selected = (ImageView) itemView.findViewById(R.id.selected);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            container_location = (LinearLayout) itemView.findViewById(R.id.container_location);
        }
    }
}
