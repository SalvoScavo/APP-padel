package com.example.padel.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.padel.Obj.Notifica;
import com.example.padel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BackgroundService extends Service
{

    ArrayList<Notifica> notifiche;

    private DatabaseReference ref;
    private FirebaseAuth aut;
    private static final String CHANNEL_ID = "PADEL";

    public void onCreate()
    {
        super.onCreate();
        notifiche = new ArrayList<>();
        aut = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("Notifiche");
        Log.d("Ser","Service attivo");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        Notifica n = ds.getValue(Notifica.class);
                        if(n.getIdDestinatario().equals(aut.getCurrentUser().getUid()))
                            notifiche.add(n);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        Notifica n = ds.getValue(Notifica.class);
                        if(n.getIdDestinatario().equals(aut.getCurrentUser().getUid())&&!notifiche.contains(ds.getKey()))
                        {
                            notifiche.add(n);
                            sendNotification("PADEL",n.getMessaggio());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        createNotificationChannel();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERV","Servizio partito");
        sendNotification("PROVA","NOTIFICA DI PROVA");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service does not support binding
        return null;
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell) // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        //Log.d("Serv","ASDFAD");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Padel Notifications";
            String description = "Channel for Firebase notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
