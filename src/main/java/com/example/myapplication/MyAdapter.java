package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    String TAG = "MyAdapter";

    String[] startTime, endTime, parameterName, parameterUnit;
    int[] image;
    Context context;

    public MyAdapter(@NonNull Context ct, @NonNull String[] s1, @NonNull String[] s2, @NonNull String[] s3, @NonNull String[] s4, @NonNull int img[]) {
        context = ct;
        startTime = s1;
        endTime = s2;
        parameterName = s3;
        parameterUnit = s4;
        image = img;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText1;
        ImageView imgv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.textView);
            imgv = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.type_a, parent, false);
        } else {
            view = inflater.inflate(R.layout.type_b, parent, false);
        }


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
        //classify TypeA and TypeB
        final int dataCount = position / 2;
        //TypeA for text
        if (position % 2 == 0) {
            String s = "";
            s = s + startTime[dataCount] + '\n' + endTime[dataCount] + '\n'
                    + parameterName[dataCount] + parameterUnit[dataCount] + " "
            ;
            holder.myText1.setText("" + s);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.changeActivity(dataCount);
                }
            });

        } else {
            //TypeB for image
            try {
                if (Integer.parseInt(parameterName[dataCount]) > 26) {
                    Log.d(TAG, "onBindViewHolder: " + image[0]);
                    holder.imgv.setImageResource(image[0]);
                } else {
                    holder.imgv.setImageResource(image[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        if (startTime != null) {

            return startTime.length * 2;
        } else {

            return 0;

        }
    }

    @Override
    public int getItemViewType(int position) {

        return position % 2;
    }
}
