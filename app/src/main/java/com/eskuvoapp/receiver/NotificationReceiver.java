package com.eskuvoapp.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.eskuvoapp.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Android 13+ permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Jogosultság nincs meg → ne próbálkozzunk
                return;
            }
        }

        String venueName = intent.getStringExtra("venue_name");
        String date = intent.getStringExtra("date");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reservation_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Ma van a foglalásod!")
                .setContentText("A(z) " + venueName + " helyszínre mára van foglalásod! (" + date + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(context).notify(2001, builder.build());
    }

}
