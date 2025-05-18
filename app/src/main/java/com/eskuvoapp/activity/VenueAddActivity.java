package com.eskuvoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eskuvoapp.R;
import com.eskuvoapp.model.Venue;
import com.google.firebase.firestore.FirebaseFirestore;

public class VenueAddActivity extends AppCompatActivity {

    private EditText nameInput, locationInput, capacityInput;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_add);

        nameInput = findViewById(R.id.name_input);
        locationInput = findViewById(R.id.location_input);
        capacityInput = findViewById(R.id.capacity_input);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);

        db = FirebaseFirestore.getInstance();

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();
            String capacityStr = capacityInput.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(capacityStr)) {
                Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "A férőhely csak szám lehet", Toast.LENGTH_SHORT).show();
                return;
            }

            Venue venue = new Venue(name, location, capacity);

            db.collection("venues").add(venue)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Helyszín mentve", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba mentés közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(VenueAddActivity.this, VenueListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        findViewById(R.id.save_button).startAnimation(slideIn);
        findViewById(R.id.cancel_button).startAnimation(slideIn);
    }
}
