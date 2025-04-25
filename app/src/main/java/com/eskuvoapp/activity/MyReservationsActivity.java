package com.eskuvoapp.activity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        recyclerView = findViewById(R.id.reservations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(reservationList);
        recyclerView.setAdapter(adapter);

        loadReservations();
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void loadReservations() {
        String email = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "";

        db.collection("reservations")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reservationList.clear();
                    querySnapshot.forEach(doc -> {
                        Reservation res = doc.toObject(Reservation.class);
                        reservationList.add(res);
                    });
                    adapter.notifyDataSetChanged();
                });
    }
}
