package com.example.fiveinarow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class NetBattleService extends Service {
    private String child;

    public NetBattleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NetBattleService", "Service Started");
        child = intent.getStringExtra("child");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NetBattleService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("NetBattleService", "END");
        Log.d("NetBattleService", child);
        FirebaseDatabase.getInstance().getReference().child(child).child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);

        stopSelf();
    }

    private String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    private String dealWith(String emaill) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < emaill.length(); i++){
            if(Character.isLetterOrDigit(emaill.charAt(i))) {
                sb.append(emaill.charAt(i));
            }
        }
        return sb.toString();
    }
}
