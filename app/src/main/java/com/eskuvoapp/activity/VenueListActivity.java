package com.eskuvoapp.activity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eskuvoapp.R;
import com.eskuvoapp.adapter.VenueAdapter;
import com.eskuvoapp.model.Venue;
import com.google.firebase.auth.FirebaseAuth;
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

        loadVenues();

        FloatingActionButton addButton = findViewById(R.id.add_venue_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(VenueListActivity.this, VenueAddActivity.class);
            startActivity(intent);
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("EsküPoint");

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(VenueListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.action_add) {
                startActivity(new Intent(this, VenueAddActivity.class));
                return true;

            } else if (id == R.id.action_toggle_theme) {
                int currentMode = AppCompatDelegate.getDefaultNightMode();
                if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                return true;

            } else if (id == R.id.action_reservations) {
                Toast.makeText(this, "Foglalásaim funkció még nincs kész 😉", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVenues();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadVenues() {
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
                    }
                });
    }
}
