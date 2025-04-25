package com.eskuvoapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eskuvoapp.R;
import com.eskuvoapp.model.Venue;

import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueViewHolder> {

    private List<Venue> venues;
    private OnVenueClickListener listener;

    public interface OnVenueClickListener {
        void onVenueClick(Venue venue);
    }

    public VenueAdapter(List<Venue> venues, OnVenueClickListener listener) {
        this.venues = venues;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venues.get(position);
        holder.nameText.setText(venue.getName());
        holder.locationText.setText(venue.getLocation());
        holder.capacityText.setText("Férőhely: " + venue.getCapacity());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVenueClick(venue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, locationText, capacityText;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.venue_name);
            locationText = itemView.findViewById(R.id.venue_location);
            capacityText = itemView.findViewById(R.id.venue_capacity);
        }
    }
}
