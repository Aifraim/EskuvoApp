package com.eskuvoapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eskuvoapp.R;
import com.eskuvoapp.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;
    private OnReservationClickListener listener;
    private List<String> reservationIds = new ArrayList<>();

    public ReservationAdapter(List<Reservation> reservations, List<String> reservationIds, OnReservationClickListener listener) {
        this.reservations = reservations;
        this.reservationIds = reservationIds;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.venueName.setText(reservation.getVenueName());
        holder.date.setText("Dátum: " + reservation.getDate());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReservationClick(reservations.get(position), reservationIds.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView venueName, date;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            venueName = itemView.findViewById(R.id.reservation_venue_name);
            date = itemView.findViewById(R.id.reservation_date);
        }
    }

    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation, String documentId);
    }

}
