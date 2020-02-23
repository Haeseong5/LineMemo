package com.haeseong5.android.linememo.memo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.utils.DbBitmapUtility;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private ArrayList<byte[]> imageDTOS;
    private Context context;

    private ImageAdapter.OnItemClickListener mListener = null ;
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(ImageAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }
    // constructor
    public ImageAdapter(Context context, ArrayList<byte[]> imageDTOS){
        this.context = context;
        this.imageDTOS = imageDTOS;
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
        if(imageDTOS.get(position) != null){
            Bitmap bitmap = DbBitmapUtility.getImage(imageDTOS.get(position));
            Glide.with(context).load(bitmap).into(holder.ivImageItem); //Glide
        }
    }

    @Override
    public int getItemCount() {
        if(imageDTOS != null)
            return imageDTOS.size();
        else
            return 0;
    }

    // inner class
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImageItem;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ivImageItem = itemLayoutView.findViewById(R.id.image_item_iv_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

        }

    }
}