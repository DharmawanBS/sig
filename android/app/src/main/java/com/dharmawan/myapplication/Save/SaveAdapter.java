package com.dharmawan.myapplication.Save;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dharmawan.myapplication.R;

import java.util.List;

/**
 * Created by dharmawan on 12/8/17.
 */

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder>{

    Context context;
    List<SaveObject> listData;

    public SaveAdapter(Context context, List<SaveObject> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_object,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SaveObject saveObject = listData.get(position);
        holder.name.setText(saveObject.getTitle());
        holder.status.setText(saveObject.getId());
        holder.status.setVisibility(View.GONE);
        holder.groupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(Maps2Activity.class,saveObject.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView groupCard;
        TextView status;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_object_name);
            status = itemView.findViewById(R.id.tv_object_status);
            groupCard = itemView.findViewById(R.id.cv_object);
        }
    }

    private void openIntent(Class page,String id){
        Intent openPage = new Intent(context,page);
        //openPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //openPage.addFlopenPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);ags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        openPage.putExtras(bundle);
        context.startActivity(openPage);
    }
}
