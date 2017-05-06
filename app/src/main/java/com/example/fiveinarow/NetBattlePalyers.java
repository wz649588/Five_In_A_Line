package com.example.fiveinarow;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class NetBattlePalyers extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    LinearLayout players_layout;
    private DatabaseReference playerDatabase;
    ListView listOfPlayers;
    public static int reminder = 0;
    public static int reminderForAccept = 0;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                displayPlayers();
                Snackbar.make(players_layout, "Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show();
                String childName = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                playerDatabase.child("Players").child(childName).setValue(new Player(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            } else {
                Snackbar.make(players_layout, "We couldn't sign you in Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_battle_palyers);
        players_layout = (LinearLayout) findViewById(R.id.players_layout);
        playerDatabase = FirebaseDatabase.getInstance().getReference();
        listOfPlayers = (ListView) findViewById(R.id.players);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else {
            Snackbar.make(players_layout, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT)
                    .show();
            displayPlayers();
            String childName = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            playerDatabase.child("Players").child(childName).setValue(new Player(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        }

        listOfPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView status = (TextView) view.findViewById(R.id.in_battle);
                TextView player = (TextView) view.findViewById(R.id.player);
                String state = status.getText().toString();
                if (state.equals("挑战")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetBattlePalyers.this);
                    builder.setTitle("选择操作");
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("挑战他", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String childName = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                           // playerDatabase.child("Players").child(childName).child("enemy").setValue(player.getText().toString());
                            //playerDatabase.child("Players").child(childName).child("inBattle").setValue(1);
                            playerDatabase.child("Players").child(childName).setValue(new Player(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    1,player.getText().toString()));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }
        });
        startService(new Intent(this, OnClearFromRecentService.class));
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

    private void displayPlayers() {
        FirebaseListAdapter<Player> adapter = new FirebaseListAdapter<Player>(this, Player.class, R.layout.player_list_item, FirebaseDatabase.getInstance().
                getReference().child("Players")) {
            @Override
            protected void populateView(View v, Player model, int position) {

                TextView playerName, inBattle, rivalName;
                String isInBattle;

                playerName = (TextView) v.findViewById(R.id.player);
                inBattle = (TextView) v.findViewById(R.id.in_battle);
                rivalName = (TextView) v.findViewById(R.id.rival);

                playerName.setText(model.getUserName());
                if(model.getInBattle() == 0) {
                    if(!model.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        isInBattle = "挑战";
                    } else {
                        isInBattle = "在线";
                    }
                } else if (model.getInBattle() == 1){
                    isInBattle = "等待请求中";
                } else if (model.getInBattle() == 2){
                    isInBattle = "战斗中";
                } else if (model.getInBattle() == 3){
                    isInBattle = "下线";
                } else {
                    isInBattle = "对方拒绝";
                }
                inBattle.setText(isInBattle);
                rivalName.setText(model.getEnemy());

                if(model.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                        model.getInBattle() == 2 && reminder == 0) {
                    reminder = 1;
                    String battleChildName = generate(dealWith(model.getUserName()), dealWith(model.getEnemy()));
                    Intent intent = new Intent(NetBattlePalyers.this, NetBattle.class);
                    intent.putExtra("childName", battleChildName);
                    startActivity(intent);
                }

                if (model.getInBattle() == 4 && model.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    playerDatabase.child("Players").child(dealWith(model.getUserName())).child("inBattle").setValue(0);
                    playerDatabase.child("Players").child(dealWith(model.getUserName())).child("enemy").setValue(null);
                    Toast.makeText(NetBattlePalyers.this, "对方拒绝应战", Toast.LENGTH_SHORT).show();
                }

                if (reminderForAccept == 0 && model.getInBattle() == 1 && model.getEnemy().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    reminderForAccept = 1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetBattlePalyers.this);
                    builder.setTitle("接受应战请求？");
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            playerDatabase.child("Players").child(dealWith(model.getUserName())).child("inBattle").setValue(4);
                            reminderForAccept = 0;
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("接受", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String childName = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            playerDatabase.child("Players").child(dealWith(model.getUserName())).child("inBattle").setValue(2);
                            playerDatabase.child("Players").child(childName).child("enemy").setValue(model.getUserName());
                            playerDatabase.child("Players").child(childName).child("inBattle").setValue(2);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                }
            }
        };
        listOfPlayers.setAdapter(adapter);
    }

    String generate(String name1, String name2) {
        char[] array = new char[name1.length() + name2.length()];
        int i = 0, j = 0;
        for(; i < name1.length(); i++) {
            array[i] = name1.charAt(i);
        }
        for(; j < name2.length(); j++) {
            array[i+j] = name2.charAt(j);
        }
        Arrays.sort(array);
        return new String(array);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out_players, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutp:
                remove();
                finish();
                /*AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(players_layout, "You have been signed out", Snackbar.LENGTH_SHORT).show();
                        finish();
                    }
                });*/
            default:
        }
        return true;
    }//菜单中的item作用

    private void remove() {
        Query usersQuery = playerDatabase.child("Players").orderByChild("userName").equalTo(getEmail());
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
        });
    }//remove a node

    private String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("NetBattlePalyers", "checkifright");
        remove();
        finish();
    }

}
