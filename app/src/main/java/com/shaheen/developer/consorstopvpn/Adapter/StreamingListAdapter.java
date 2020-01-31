package com.shaheen.developer.consorstopvpn.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.google.gson.Gson;
import com.shaheen.developer.consorstopvpn.Models.ChannelListModel;
import com.shaheen.developer.consorstopvpn.R;
import com.shaheen.developer.consorstopvpn.StreamingFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StreamingListAdapter   extends RecyclerView.Adapter<StreamingListAdapter.ViewHolder> {



    private ArrayList<ChannelListModel> arrayList = new ArrayList<>();
    private Context context;
    StreamingFragment fragment;
    String res;

    public StreamingListAdapter(ArrayList<ChannelListModel> arrayList, Context context,StreamingFragment fragment) {
        this.arrayList = arrayList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public StreamingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.streaming_list_item, viewGroup, false);

        return new StreamingListAdapter.ViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final StreamingListAdapter.ViewHolder holder, final int position) {


        final ChannelListModel model = arrayList.get(position);
        holder.name.setText(model.getName());


        Picasso.get()
                .load(model.getIcon_url())
                .resize(90, 70)
                .centerInside()
                .into(holder.icon);


        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
        if (pref!=null){
            String st_streaming =  pref.getString("streaming_detail",null);
            if (st_streaming!=null){
                ChannelListModel chanel = gson.fromJson(st_streaming,ChannelListModel.class);
                if (chanel.getId().equals(model.getId())){
                    if (pref.getString("connection_type",null)!=null  &&  pref.getString("connection_type",null).equals("streaming")){
                        holder.play.setImageDrawable(context.getResources().getDrawable(R.drawable.play_activated));
                    }
                }
            }else {
                Log.d("shani","st_streaming prefs is null..");
            }
        }else {
            Log.d("shani","chanel prefs is null..");
        }


        holder.container_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                String ChannelToGson = gson.toJson(model);
                SharedPreferences sharedPref = context.getSharedPreferences("CONNECTION_DATA",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("connection_type","streaming");
                editor.putString("streaming_detail",ChannelToGson);
                editor.apply();


                fragment.RefreshStreamList();

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
        ImageView play, icon;
        LinearLayout container_stream;


        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            play = (ImageView) itemView.findViewById(R.id.play);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            container_stream = (LinearLayout) itemView.findViewById(R.id.container_stream);
        }
    }
}

