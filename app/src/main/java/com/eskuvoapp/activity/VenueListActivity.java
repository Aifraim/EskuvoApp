package com.eskuvoapp.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eskuvoapp.R;
import com.eskuvoapp.adapter.VenueAdapter;
import com.eskuvoapp.model.Venue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VenueListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VenueAdapter adapter;
    private List<Venue> venueList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);

        recyclerView = findViewById(R.id.venue_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VenueAdapter(venueList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("venues").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        venueList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Venue venue = doc.toObject(Venue.class);
                            if (venue != null) {
                                venue.setId(doc.getId());
                                venueList.add(venue);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Hiba a lekérdezés során", task.getException());
                    }
                });
    }
}
