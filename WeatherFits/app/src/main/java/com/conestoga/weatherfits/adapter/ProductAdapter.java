package com.conestoga.weatherfits.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.weatherfits.R;
import com.conestoga.weatherfits.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private FirebaseUser user;

    public ProductAdapter(Context context, List<Product> productList, FirebaseUser user){
        super();
        this.context = context;
        this.productList = productList;
        this.user = user;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product item = productList.get(position);
        holder.txtProductTitle.setText(item.getName());
        Uri uri = Uri.parse(item.getImage());
        Picasso.get().load(uri).into(holder.imgProductImage);
        holder.btnGetProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri btnUri = Uri.parse(item.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, btnUri);
                context.startActivity(intent);
            }
        });

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("favs").child(item.getName())
                        .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR IN INSERTION: ", e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProductImage, btnGetProduct;
        public TextView txtProductTitle;
        public ImageButton btnFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductImage = (ImageView) itemView.findViewById(R.id.imgProductImage);
            txtProductTitle = (TextView) itemView.findViewById(R.id.txtProductTitle);
            btnGetProduct = (ImageButton) itemView.findViewById(R.id.btnGetProduct);
            btnFav = (ImageButton) itemView.findViewById(R.id.btnFav);
        }
    }
}
