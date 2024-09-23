package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class potholeAdapter2 extends RecyclerView.Adapter<potholeAdapter2.PotholeViewHolder> {

    private Context context;
    private List<Pothole> potholeList;

    public potholeAdapter2(Context context, List<Pothole> potholeList) {
        this.context = context;
        this.potholeList = potholeList;
    }

    @NonNull
    @Override
    public PotholeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pothole_items2, parent, false);
        return new PotholeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotholeViewHolder holder, int position) {
        Pothole pothole = potholeList.get(position);
        holder.bind(pothole);
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    public class PotholeViewHolder extends RecyclerView.ViewHolder {
        TextView roadTextView, locationTextView, sizeTextView, usernameTextView, TextViewdate;
        ImageView photoImageView;
        Button bidButton, viewNotificationsButton;

        public PotholeViewHolder(@NonNull View itemView) {
            super(itemView);
            roadTextView = itemView.findViewById(R.id.roadTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            bidButton = itemView.findViewById(R.id.bidButton);
            viewNotificationsButton = itemView.findViewById(R.id.viewNotificationsButton);
            TextViewdate = itemView.findViewById(R.id.TextViewdate);

            bidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Pothole pothole = potholeList.get(position);
                        Intent intent = new Intent(context, biddingactivity.class);
                        intent.putExtra("potholeId", pothole.getId()); // Pass pothole ID to bidding activity
                        context.startActivity(intent);
                    }
                }
            });

            viewNotificationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Pothole pothole = potholeList.get(position);
                        Intent intent = new Intent(context, NotificationActivity.class);
                        intent.putExtra("potholeId", pothole.getId()); // Pass pothole ID to NotificationsActivity
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Pothole pothole) {
            roadTextView.setText(pothole.getRoad());
            locationTextView.setText(pothole.getLocation());
            sizeTextView.setText(pothole.getPotholeSize());
            usernameTextView.setText(pothole.getUsername());
            TextViewdate.setText(pothole.getDate()); // Set date if available

            if (pothole.getMediaUrl() != null) {
                Glide.with(context)
                        .load(pothole.getMediaUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery) // Default placeholder image
                        .error(android.R.drawable.ic_delete) // Default error image
                        .into(photoImageView);
            } else {
                photoImageView.setImageResource(android.R.drawable.ic_menu_gallery);  // Set a default image if no URL
            }
        }
    }
}


