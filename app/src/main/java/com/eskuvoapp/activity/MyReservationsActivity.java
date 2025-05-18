package com.eskuvoapp.activity;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eskuvoapp.R;
import com.eskuvoapp.adapter.ReservationAdapter;
import com.eskuvoapp.model.Reservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyReservationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<Reservation> reservationList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<String> reservationIds = new ArrayList<>();
    private TextView emptyText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        emptyText = findViewById(R.id.empty_text);

        recyclerView = findViewById(R.id.reservations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(reservationList, reservationIds, (reservation, documentId) -> {
            Intent intent = new Intent(MyReservationsActivity.this, ReservationDetailActivity.class);
            intent.putExtra("reservationId", documentId);
            intent.putExtra("venueId", reservation.getVenueId());
            intent.putExtra("venueName", reservation.getVenueName());
            intent.putExtra("reservationDate", reservation.getDate());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadReservations();
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadReservations();
    }

    private void loadReservations() {
        String email = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "";

        db.collection("reservations")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reservationList.clear();
                    reservationIds.clear();
                    querySnapshot.forEach(doc -> {
                        Reservation res = doc.toObject(Reservation.class);
                        reservationList.add(res);
                        reservationIds.add(doc.getId());
                    });

                    adapter.notifyDataSetChanged();

                    if (reservationList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);

                        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                        emptyText.startAnimation(fadeIn);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.GONE);
                    }
                });
    }
}
