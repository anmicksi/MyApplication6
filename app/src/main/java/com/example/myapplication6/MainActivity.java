package com.example.myapplication6;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.provider.Settings;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "My channel";
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    showNotification();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.POST_NOTIFICATIONS},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });
        Button startService = findViewById(R.id.button2);
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    requestPermissions();
                }
                else {
                    Intent serviceIntent = new Intent(MainActivity.this,
                            MyService.class);
                    startService(serviceIntent);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            }
        }
        else if (Settings.canDrawOverlays(this)) {
            Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
            startService(serviceIntent);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My channel";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @SuppressLint("MissingPermission")
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.dog)
                .setContentTitle("Напоминание")
                .setContentText("Погулять с собакой")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFY_ID, builder.build());
    }
    public void requestPermissions() {
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        if (myIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(myIntent);
        }
    }
}
