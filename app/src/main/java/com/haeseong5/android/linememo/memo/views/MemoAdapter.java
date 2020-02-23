package com.haeseong5.android.linememo.memo.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haeseong5.android.linememo.R;
import com.haeseong5.android.linememo.memo.database.models.Memo;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MyViewHolder> {

    private static final String TAG = MemoAdapter.class.getName();
    private ArrayList<Memo> memos;
    private Context context;
    private OnItemClickListener mListener = null ;
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public MemoAdapter(Context context, ArrayList<Memo> memos){
        this.context = context;
        this.memos = memos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_memo, null);

        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{
            Glide.with(context).load(memos.get(position).getThumbNail()).into(holder.itemImage); //Glide
        }catch (Exception e){
            Log.d(TAG ,e.toString());
        }
        holder.itemTitle.setText(memos.get(position).getTitle());
        holder.itemContent.setText(memos.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if(memos != null)
            return memos.size();
        else
            return 0;
    }

    // inner static class
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTitle;
        public TextView itemContent;
        public ImageView itemImage;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemTitle = itemLayoutView.findViewById(R.id.item_memo_tv_title);
            itemContent = itemLayoutView.findViewById(R.id.item_memo_tv_content);
            itemImage = itemLayoutView.findViewById(R.id.item_memo_iv_image);

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