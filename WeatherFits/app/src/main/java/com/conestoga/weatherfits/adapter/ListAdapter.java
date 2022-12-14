package com.conestoga.weatherfits.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.weatherfits.R;
import com.conestoga.weatherfits.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    Context context;
    ArrayList<Product> list;

    public ListAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View v = LayoutInflater.from(context).inflate(R.layout.fav_items,parent,false);
     return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product fav = list.get(position);
        holder.name.setText(fav.getName());
        holder.link.setText(fav.getLink());
        Uri uri = Uri.parse(fav.getImage());
        Picasso.get().load(uri).into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView name,link;
        ImageView pic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtName);
            link = itemView.findViewById(R.id.txtLink);
            pic = itemView.findViewById(R.id.imgPic);

        }
    }




}
