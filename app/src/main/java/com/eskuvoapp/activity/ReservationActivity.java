package com.eskuvoapp.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.eskuvoapp.R;
import com.eskuvoapp.model.Reservation;
import com.eskuvoapp.receiver.NotificationReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private TextView venueNameText, selectedDateText;
    private Button selectDateButton, reserveButton;

    private String venueId;
    private String venueName;
    private String selectedDate;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        venueNameText = findViewById(R.id.venue_name_text);
        selectedDateText = findViewById(R.id.selected_date_text);
        selectDateButton = findViewById(R.id.select_date_button);
        reserveButton = findViewById(R.id.reserve_button);

        Intent intent = getIntent();
        venueId = intent.getStringExtra("venue_id");
        venueName = intent.getStringExtra("venue_name");
        venueNameText.setText(venueName);

        selectDateButton.setOnClickListener(v -> showDatePicker());

        reserveButton.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Válassz ki egy dátumot!", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAndMakeReservation();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reservation_channel",
                    "Foglalások",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(selected.getTime());
                    selectedDateText.setText("Kiválasztott dátum: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void checkAndMakeReservation() {
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "ismeretlen";

        db.collection("reservations")
                .whereEqualTo("venueId", venueId)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reservationNotification();
                    scheduleReminder(venueName, selectedDate, venueId);
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Ez a helyszín már foglalt erre a napra!", Toast.LENGTH_LONG).show();
                    } else {
                        // Foglalás mentése
                        db.collection("reservations")
                                .add(new Reservation(venueId, venueName, selectedDate, userEmail))
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this, "Sikeres foglalás!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void reservationNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "reservation_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Foglalás megerősítve")
                .setContentText("Lefoglaltad a(z) " + venueName + " helyszínt erre a napra: " + selectedDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        notificationManager.notify(1001, builder.build());
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleReminder(String venueName, String date, String venueId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date reservationDate = sdf.parse(date);

            Calendar calendar = Calendar.getInstance();
            if (reservationDate != null) {
                calendar.setTime(reservationDate);
                calendar.set(Calendar.HOUR_OF_DAY, 8); // Reggel 8
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("venue_name", venueName);
                intent.putExtra("date", date);

                int requestCode = venueId.hashCode() + date.hashCode();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
