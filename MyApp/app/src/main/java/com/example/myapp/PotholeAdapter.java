package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PotholeAdapter extends RecyclerView.Adapter<PotholeAdapter.PotholeViewHolder> {

    private Context context;
    private List<Pothole> potholeList;

    public PotholeAdapter(Context context, List<Pothole> potholeList) {
        this.context = context;
        this.potholeList = potholeList;
    }

    @NonNull
    @Override
    public PotholeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pothole_items, parent, false);
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
        TextView roadTextView, locationTextView, sizeTextView, usernameTextView, dateTextView;
        ImageView mediaImageView;

        public PotholeViewHolder(@NonNull View itemView) {
            super(itemView);
            roadTextView = itemView.findViewById(R.id.roadTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            mediaImageView = itemView.findViewById(R.id.photoImageView);
        }

        public void bind(Pothole pothole) {
            roadTextView.setText(pothole.getRoad());
            locationTextView.setText(pothole.getLocation());
            sizeTextView.setText(pothole.getPotholeSize());
            usernameTextView.setText(pothole.getUsername());
            dateTextView.setText(pothole.getDate());

            // Load image using Glide with built-in Android drawables as placeholders
            if (pothole.getMediaUrl() != null && !pothole.getMediaUrl().isEmpty()) {
                Glide.with(context)
                        .load(pothole.getMediaUrl())
                        .apply(new RequestOptions()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.stat_notify_error)
                                .centerCrop()) // Optional: Crop image to fit ImageView
                        .into(mediaImageView);
            } else {
                mediaImageView.setImageResource(android.R.drawable.ic_menu_gallery); // Default image if URL is null or empty
            }
        }
    }
}