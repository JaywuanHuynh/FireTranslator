package com.project.firetranslator.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.firetranslator.R;
import com.project.firetranslator.data.model.Translation;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final Context context;
    private List<Translation> mList = new ArrayList<Translation>();

    public MyAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setSavedList(List<Translation> list){
        this.mList.clear();
        this.mList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.word_item,parent, false );
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Translation words = mList.get(position);
        holder.inputTV.setText(words.getInputText());
        holder.outputTV.setText(words.getOutputText());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView inputTV;
        TextView outputTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inputTV = itemView.findViewById(R.id.tv_input);
            outputTV = itemView.findViewById(R.id.tv_output);
        }
    }
}
