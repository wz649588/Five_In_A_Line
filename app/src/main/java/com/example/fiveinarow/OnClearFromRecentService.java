package com.example.fiveinarow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OnClearFromRecentService extends Service {
    public OnClearFromRecentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        Log.d("ClearFromRecentService", FirebaseAuth.getInstance().getCurrentUser().getEmail()+"");
        //FirebaseDatabase.getInstance().getReference().child("Players").child(dealWith(getEmail())).child("inBattle").setValue(3);
        //FirebaseDatabase.getInstance().getReference().child("Players").child(dealWith(getEmail())).child("userName").setValue(getEmail());
        /* Query usersQuery = FirebaseDatabase.getInstance().getReference().child("Players").orderByChild("userName").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "onCancelled", databaseError.toException());
            }
        });*/


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
