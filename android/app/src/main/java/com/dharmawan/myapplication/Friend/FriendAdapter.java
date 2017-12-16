package com.dharmawan.myapplication.Friend;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dharmawan.myapplication.Constant;
import com.dharmawan.myapplication.R;
import com.dharmawan.myapplication.UserObject;

import java.util.List;

/**
 * Created by dharmawan on 12/3/17.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    Context context;
    List<UserObject> listData;

    public FriendAdapter(Context context, List<UserObject> listData) {
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
        final UserObject friendObject = listData.get(position);
        holder.name.setText(friendObject.getName());
        holder.status.setText(friendObject.getLast_status());
        if (!friendObject.getPhoto().equals("null")) {
            Glide.with(context).load(Constant.URL+"/photo/" + friendObject.getPhoto()).into(holder.photo);
        }
        holder.friendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(friendObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView friendCard;
        TextView name;
        TextView status;
        ImageView photo;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_object_name);
            status = itemView.findViewById(R.id.tv_object_status);
            photo = itemView.findViewById(R.id.iv_object_photo);
            friendCard = itemView.findViewById(R.id.cv_object);
        }
    }
    private void showDialog(UserObject friendObject){
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_friend,null);
        TextView name = v.findViewById(R.id.tv_name);
        TextView status = v.findViewById(R.id.tv_status);
        TextView datetime = v.findViewById(R.id.tv_datetime);
        ImageView photo = v.findViewById(R.id.iv_photo);
        name.setText(friendObject.getName());
        status.setText(friendObject.getLast_status());
        datetime.setText(friendObject.getDatetime());
        if (!friendObject.getPhoto().equals("null")) {
            Glide.with(context).load(Constant.URL+"/photo/" + friendObject.getPhoto()).into(photo);
        }
        AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        myDialog.setView(v);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        myDialog.show();
    }
}