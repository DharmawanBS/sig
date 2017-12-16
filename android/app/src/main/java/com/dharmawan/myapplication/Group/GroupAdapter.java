package com.dharmawan.myapplication.Group;

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
import com.dharmawan.myapplication.Session;
import com.dharmawan.myapplication.UserObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmawan on 12/3/17.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{

    Context context;
    List<GroupObject> listData;

    public GroupAdapter(Context context, List<GroupObject> listData) {
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
        final GroupObject groupObject = listData.get(position);
        holder.name.setText(groupObject.getName());
        holder.status.setText(groupObject.getId());
        holder.status.setVisibility(View.GONE);
        ArrayList<UserObject> leader = groupObject.getLeader();
        boolean is_leader = false;
        final Session session = new Session(context);
        String id_user = session.getPreferences("id");
        for(int i=0;i<leader.size();i++) {
            if (leader.get(i).getId().equals(id_user)) {
                is_leader = true;
                break;
            }
        }
        final boolean finalIs_leader = is_leader;
        holder.groupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(MapsActivity.class,groupObject.getId(), finalIs_leader);
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

    private void openIntent(Class page,String id,boolean leader){
        Intent openPage = new Intent(context,page);
        //openPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //openPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putBoolean("leader",leader);
        openPage.putExtras(bundle);
        context.startActivity(openPage);
    }
}