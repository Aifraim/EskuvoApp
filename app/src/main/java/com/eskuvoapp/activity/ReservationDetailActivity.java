package com.eskuvoapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eskuvoapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationDetailActivity extends AppCompatActivity {

    private TextView venueNameText, venueLocationText, venueCapacityText, reservationDateText, errorText;
    private Button selectNewDateButton, saveButton, deleteButton;

    private String reservationId, venueId, venueName, reservationDate;
    private String selectedDate;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean dateAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        venueNameText = findViewById(R.id.venue_name_text);
        venueLocationText = findViewById(R.id.venue_location_text);
        venueCapacityText = findViewById(R.id.venue_capacity_text);
        reservationDateText = findViewById(R.id.reservation_date_text);
        errorText = findViewById(R.id.error_text);
        selectNewDateButton = findViewById(R.id.select_new_date_button);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);

        reservationId = getIntent().getStringExtra("reservationId");
        venueId = getIntent().getStringExtra("venueId");
        venueName = getIntent().getStringExtra("venueName");
        reservationDate = getIntent().getStringExtra("reservationDate");
        selectedDate = reservationDate;

        venueNameText.setText(venueName);
        reservationDateText.setText("Foglalás dátuma: " + reservationDate);

        loadVenueDetails();

        selectNewDateButton.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> {
            if (dateAvailable) {
                updateReservationDate();
            }
        });

        deleteButton.setOnClickListener(v -> deleteReservation());
        findViewById(R.id.cancel_button).setOnClickListener(v -> finish());
    }

    private void loadVenueDetails() {
        db.collection("venues").document(venueId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String location = doc.getString("location");
                        Long capacity = doc.getLong("capacity");

                        venueLocationText.setText("Cím: " + location);
                        venueCapacityText.setText("Férőhely: " + (capacity != null ? capacity : "-"));
                    }
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(selected.getTime());

                    checkDateAvailability();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        picker.show();
    }

    private void checkDateAvailability() {
        db.collection("reservations")
                .whereEqualTo("venueId", venueId)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        errorText.setText("Ez a dátum már foglalt!");
                        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        saveButton.setEnabled(false);
                        dateAvailable = false;
                    } else {
                        errorText.setText("");
                        saveButton.setEnabled(true);
                        dateAvailable = true;
                    }
                    reservationDateText.setText("Új dátum: " + selectedDate);
                });
    }

    private void updateReservationDate() {
        db.collection("reservations").document(reservationId)
                .update("date", selectedDate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Dátum sikeresen módosítva!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteReservation() {
        db.collection("reservations").document(reservationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Foglalás törölve", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba törlés közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
