package com.example.uber_clone;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uber_clone.interfaces.RecyclerViewClickListener;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<String> recArrayList = new ArrayList<>();
    //private Context context;
    //for setting up click listener in list item
    private RecyclerViewClickListener recyclerViewClickListener;


    public RecyclerAdapter(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public RecyclerAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        return  new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //holder.view.setText(String.valueOf(recArrayList.get(position).getId()));


        int index = position;
        holder.tv_id.setText(recArrayList.get(index));

        //setting click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing the clicked position to the interface
                recyclerViewClickListener.onRecViewItemClick(index);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recArrayList.size();
    }

    //getting clicked data
    public String getItemData(int position) {
        return recArrayList.get(position);
    }



    //updating the data of the recView array
    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterData(ArrayList<String> object) {
        this.recArrayList = object;
        notifyDataSetChanged();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_id;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tv_id = itemView.findViewById(android.R.id.text1);
        }

        public void setTvText(String s){
            tv_id.setText(s);
        }
    }
}