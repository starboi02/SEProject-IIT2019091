package com.example.anomalydetector;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    ArrayList<ECGData> list;
    Context context;

    public MyAdapter(Context context,ArrayList<ECGData> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.array_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        ECGData data= list.get(position);
        holder.textView.setText((position+1)+"  "+data.getName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ResultActivity.class);
                intent.putExtra("value",data.getValue());
                intent.putExtra("data",convertArrayToList(data.getData()));
                context.startActivity(intent);
            }
        });
    }

    public float[] convertArrayToList(List<Float> arrayList){
        float[] list = new float[140];
        for(int i=0;i<arrayList.size();i++)
            list[i]=arrayList.get(i);

        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.report_name);
            layout=itemView.findViewById(R.id.layout);
        }
    }

}
