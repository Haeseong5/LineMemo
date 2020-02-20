package com.haeseong5.android.linememo.memo;

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
import com.haeseong5.android.linememo.memo.database.model.Memo;

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MyViewHolder> {

    private ArrayList<Memo> memos;

    private OnItemClickListener mListener = null ;
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    // constructor
    public MemoAdapter(ArrayList<Memo> memos){
        this.memos = memos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate item_layout
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_memo2, null);

        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        Glide.with(context).load(memos.get(position).getImage()).into(holder.itemImage); //Glide
        holder.itemTitle.setText(memos.get(position).getTitle());
        holder.itemContent.setText(memos.get(position).getContent());
        Log.d("adapter ", memos.get(position).getTitle());

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
            itemTitle = itemLayoutView.findViewById(R.id.item_text1);
            itemContent = itemLayoutView.findViewById(R.id.item_text2);
            itemImage = itemLayoutView.findViewById(R.id.item_icon);

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }
}