package com.haeseong5.android.linememo.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haeseong5.android.linememo.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private ArrayList<ImageItem> images;
    private Context context;
    // constructor
    public ImageAdapter(Context context, ArrayList<ImageItem> images){
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate item_layout
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, null);

        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(images.get(position).getBitmap()).into(holder.itemImage); //Glide
    }

    @Override
    public int getItemCount() {
        if(images != null)
            return images.size();
        else
            return 0;
    }

    // inner static class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemImage = itemLayoutView.findViewById(R.id.iv_item_image);
        }
    }
}