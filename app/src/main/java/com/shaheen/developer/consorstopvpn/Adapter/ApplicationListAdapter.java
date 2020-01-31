package com.shaheen.developer.consorstopvpn.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shaheen.developer.consorstopvpn.Models.PInfo;
import com.shaheen.developer.consorstopvpn.R;
import com.shaheen.developer.consorstopvpn.SqlieDataBase.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Shani on 8/16/2019.
 */

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {



    ArrayList<PInfo> arrayList = new ArrayList<>();
    Context context;
    String res;
    DatabaseHelper databaseHelper;

    public ApplicationListAdapter(ArrayList<PInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.applications_list_item, viewGroup, false);
        return new ApplicationListAdapter.ViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final ApplicationListAdapter.ViewHolder holder, final int position) {


        final PInfo model = arrayList.get(position);

       holder.icon.setImageDrawable(model.getIcon());
       holder.name.setText(model.getAppname());


       holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

               if (b){
                   databaseHelper.InsertApp(model.getAppname(),model.getPname(),model.getVersionName(),model.getVersionCode());
               }else {
                   ArrayList<PInfo> list = databaseHelper.getAllAPPs();
                   for (int i = 0; i < list.size(); i++) {
                       if (list.get(i).getPname().equals(model.getPname())){
                           databaseHelper.deleteNote(list.get(i));
                       }
                   }
               }
           }
       });
        if (CheckExistence(model.getPname())){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
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
        ImageView icon;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.app_name);
            icon = (ImageView) itemView.findViewById(R.id.app_icon);
            checkBox = (CheckBox)itemView.findViewById(R.id.app_check);
        }
    }
}
