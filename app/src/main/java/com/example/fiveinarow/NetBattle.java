package com.example.fiveinarow;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NetBattle extends AppCompatActivity {

    private MediaPlayer lost;
    private MediaPlayer win;
    private Chess_Panel netPanel;
    //private Button netNewGame;
    private AlertDialog.Builder builder;
    public static int player;
    //private Button chooseBlack;
    //private Button chooseWhite;
    private Button regret;
    private Button admitLose;
    public static DatabaseReference mDatabase;
    //private Button start;
    private DrawerLayout drawerLayout;
    private ArrayList<Distribute> players = new ArrayList<>();
    private String dataPackage;
    private int checkIfEnemyLogout = 0;

    private static int SIGN_IN_REQUEST_CODE = 1;
    LinearLayout activity_net_battle;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign) {
            String myEmail = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("inBattle").setValue(0);
            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("enemy").setValue(null);
            if (checkIfEnemyLogout == 0) {
                mDatabase.child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);
            } else {
                againn();
            }
            again();
            //againn();
            //player = 0;
            netPanel.mode = 0;
            NetBattlePalyers.reminder = 0;
            NetBattlePalyers.reminderForAccept = 0;
            finish();
        }
        if (item.getItemId() == R.id.chat) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else{
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        if (item.getItemId() == R.id.save) {
            SaveSheetToFile saveSheetToFile = new SaveSheetToFile(NetBattle.this, netPanel);
            saveSheetToFile.save();
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_net_battle, "Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show();
                displayPiece();
                displayDistribute();
                mDatabase.child("Distribute").child(dealWith(getEmail())).setValue(new Distribute(getEmail()));
            } else {
                Snackbar.make(activity_net_battle, "We couldn't sign you in Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_battle);

        Intent intent = getIntent();
        dataPackage = intent.getStringExtra("childName");
        lost = MediaPlayer.create(this, R.raw.lost);
        win = MediaPlayer.create(this, R.raw.win);
        activity_net_battle = (LinearLayout) findViewById(R.id.activity_net_battle);
        netPanel = (Chess_Panel) findViewById(R.id.net_panel);
        //netNewGame = (Button) findViewById(R.id.netNewGame);
        regret = (Button) findViewById(R.id.regret);
        admitLose = (Button) findViewById(R.id.admitLose);
       // chooseBlack = (Button) findViewById(R.id.ChooseBlack);
       // chooseWhite = (Button) findViewById(R.id.ChooseWhite);
       // start = (Button) findViewById(R.id.Start);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        player = 0;

        netPanel.mode = 2;
        mDatabase = FirebaseDatabase.getInstance().getReference().child(dataPackage);


        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player == 0) {
                    player = 50;
                    mDatabase.child("Distribute").child(dealWith(getEmail())).setValue(new Distribute(getEmail()));
                }
            }
        });*/

        /*netNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                again();
                againn();
                player = 0;
            }
        });*/

        regret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Chess_Panel.myBlackArray.size() > 0 && (!(Chess_Panel.myWhiteArray.size() == 0 && player == 100))) {
                    mDatabase.child("Distribute").child(dealWith(getEmail())).child("regret").setValue(1);
                }
            }
        });

        admitLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Chess_Panel.myBlackArray.size() > 0) {
                    mDatabase.child("Distribute").child(dealWith(getEmail())).child("lost").setValue(1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetBattle.this);
                    builder.setTitle("你认输了，You lose!");
                    lost.start();
                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String myEmail = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("inBattle").setValue(0);
                            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("enemy").setValue(null);
                            again();
                            if (checkIfEnemyLogout == 0) {
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);
                            }
                            //againn();
                            //player = 0;
                            netPanel.mode = 0;
                            NetBattlePalyers.reminder = 0;
                            NetBattlePalyers.reminderForAccept = 0;
                            finish();
                        }
                    });
                    builder.setPositiveButton("Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            again();
                            if (player == 100) {
                                player = 200;
                                Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(200);
                            } else {
                                player = 100;
                                Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(100);
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }
        });
      /*  chooseBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player==0) {
                    player = 200;
                }
            }
        });

        chooseWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player == 0) {
                    player = 100;
                }
            }
        });*/

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else {
            Snackbar.make(activity_net_battle, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT)
                    .show();
            displayPiece();
            displayDistribute();
            mDatabase.child("Distribute").child(dealWith(getEmail())).setValue(new Distribute(getEmail()));
        }

        builder = new AlertDialog.Builder(NetBattle.this);
        builder.setTitle("Game Over");
        builder.setPositiveButton("Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                again();
                if(player == 100) {
                    player = 200;
                    Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                    mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(200);
                } else {
                    player = 100;
                    Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                    mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(100);
                }
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String myEmail = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("inBattle").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("enemy").setValue(null);
                again();
                if (checkIfEnemyLogout == 0) {
                    mDatabase.child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);
                }
                //againn();
                //player = 0;
                netPanel.mode = 0;
                NetBattlePalyers.reminder = 0;
                NetBattlePalyers.reminderForAccept = 0;
                finish();
            }
        });
        netPanel.setOnGameListener(new OnGameListener() {
            @Override
            public void onGameOver(int i) {
                String str = "";
                if ((i == Chess_Panel.WHITE_WIN && player == 100) || (i == Chess_Panel.BLACK_WIN && player == 200)) {
                    str = "You win";
                } else {
                    str = "You lose";
                }
                builder.setMessage(str);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 0;
                params.y = netPanel.getmUnder();
                dialogWindow.setAttributes(params);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        Intent serviceIntent = new Intent(NetBattle.this, NetBattleService.class);
        serviceIntent.putExtra("child", dataPackage);
        startService(serviceIntent);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView xp, yp;

        public UsersViewHolder(View v) {
            super(v);
            xp = (TextView) v.findViewById(R.id.x);
            yp = (TextView) v.findViewById(R.id.y);
        }
    }

    public static class DistributeViewHolder extends RecyclerView.ViewHolder {
        TextView emailp, ranp;

        public DistributeViewHolder(View v) {
            super(v);
            emailp = (TextView) v.findViewById(R.id.email);
            ranp = (TextView) v.findViewById(R.id.ran);
        }
    }


    public void displayDistribute() {
        RecyclerView listOfPlayers = (RecyclerView) findViewById(R.id.distributeView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        listOfPlayers.setLayoutManager(linearLayoutManager);
        FirebaseRecyclerAdapter<Distribute, DistributeViewHolder> adapter = new FirebaseRecyclerAdapter<Distribute, DistributeViewHolder>(Distribute.class, R.layout.listviewd, DistributeViewHolder.class, mDatabase.child("Distribute")) {
            @Override
            protected void populateViewHolder(DistributeViewHolder distributeViewHolder, Distribute model, int position) {
                distributeViewHolder.emailp.setText(model.getEmail());
                if (model.getColor() == 200) {
                    distributeViewHolder.ranp.setText("执黑");
                } else {
                    distributeViewHolder.ranp.setText("执白");
                }

                if (!model.getEmail().equals(getEmail()) && model.getOffline() == 1) {
                    Toast.makeText(NetBattle.this, "对方退出战斗", Toast.LENGTH_SHORT).show();
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("offline").setValue(0);
                    checkIfEnemyLogout = 1;
                }

                if (!model.getEmail().equals(getEmail()) && model.getRegret() == 1 && model.getAgreeRegret() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetBattle.this);
                    builder.setTitle("对方要悔棋");
                    builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("agreeRegret").setValue(3);
                        }
                    });
                    builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("agreeRegret").setValue(2);
                            if (player == 200) {
                                if (Chess_Panel.myBlackArray.size() == Chess_Panel.myWhiteArray.size()) {
                                    dealWithArrays();
                                } else {
                                    dealWithArrays();
                                    dealWithArrays();
                                }
                            } else if (player == 100) {
                                if (Chess_Panel.myBlackArray.size() == Chess_Panel.myWhiteArray.size()) {
                                    dealWithArrays();
                                    dealWithArrays();
                                } else {
                                    dealWithArrays();
                                }
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }


                if (model.getEmail().equals(getEmail()) && model.getRegret() == 1 && model.getAgreeRegret() == 3) {
                    Toast.makeText(NetBattle.this, "对方不同意悔棋", Toast.LENGTH_SHORT).show();
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("regret").setValue(0);
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("agreeRegret").setValue(0);
                }

                if (model.getEmail().equals(getEmail()) && model.getRegret() == 1 && model.getAgreeRegret() == 2) {
                    //dosomethingherej
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("regret").setValue(0);
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("agreeRegret").setValue(0);
                    if (player == 200) {
                        if (Chess_Panel.myBlackArray.size() == Chess_Panel.myWhiteArray.size()) {
                            removeLatestPiece();
                            dealWithArrays();
                            removeLatestPiece();
                            dealWithArrays();
                        } else {
                            removeLatestPiece();
                            dealWithArrays();
                        }
                    } else if (player == 100) {
                        if (Chess_Panel.myBlackArray.size() == Chess_Panel.myWhiteArray.size()) {
                            removeLatestPiece();
                            dealWithArrays();
                        } else {
                            removeLatestPiece();
                            dealWithArrays();
                            removeLatestPiece();
                            dealWithArrays();
                        }
                    }
                }

                if(!model.getEmail().equals(getEmail()) && model.getLost() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetBattle.this);
                    builder.setTitle("对方已经认输，You win!");
                    win.start();
                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String myEmail = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("inBattle").setValue(0);
                            FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("enemy").setValue(null);
                            again();
                            if (checkIfEnemyLogout == 0) {
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);
                            }
                            //againn();
                            //player = 0;
                            netPanel.mode = 0;
                            NetBattlePalyers.reminder = 0;
                            NetBattlePalyers.reminderForAccept = 0;
                            finish();
                        }
                    });
                    builder.setPositiveButton("Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            again();
                            if(player == 100) {
                                player = 200;
                                Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(200);
                            } else {
                                player = 100;
                                Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                                mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(100);
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    mDatabase.child("Distribute").child(dealWith(model.getEmail())).child("lost").setValue(0);
                }
                players.add(new Distribute(model.getEmail(), model.getRan()));
                if(players.size() == 2 && player == 0) {
                    if(players.get(0).getEmail() == FirebaseAuth.getInstance().getCurrentUser().getEmail()){
                        if(players.get(0).getRan() > players.get(1).getRan()) {
                            player = 200;
                            Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(200);
                        } else {
                            player = 100;
                            Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(100);
                        }
                    }
                    if(players.get(1).getEmail() == FirebaseAuth.getInstance().getCurrentUser().getEmail()){
                        if(players.get(1).getRan() > players.get(0).getRan()) {
                            player = 200;
                            Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(200);
                        } else {
                            player = 100;
                            Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Distribute").child(dealWith(getEmail())).child("color").setValue(100);
                        }
                    }
                }
            }
        };
        listOfPlayers.setAdapter(adapter);
    }

    public void displayPiece() {
       RecyclerView listOfMessage = (RecyclerView) findViewById(R.id.pieceView);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
       linearLayoutManager.setReverseLayout(true);
       listOfMessage.setLayoutManager(linearLayoutManager);
       FirebaseRecyclerAdapter<Piece, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Piece, UsersViewHolder>(Piece.class, R.layout.list_view, UsersViewHolder.class, mDatabase.child("Pieces")) {
           @Override
           protected void populateViewHolder(UsersViewHolder usersViewHolder, Piece model, int position) {
               usersViewHolder.xp.setText(model.getX()+"");
               usersViewHolder.yp.setText(model.getY()+"");

               Point npoint = new Point(model.getX(), model.getY());

               if(model.getType()==200 && !Chess_Panel.myBlackArray.contains(npoint)) {
                   Chess_Panel.myBlackArray.add(npoint);
               }
               if(model.getType()==100 && !Chess_Panel.myWhiteArray.contains(npoint)) {
                   Chess_Panel.myWhiteArray.add(npoint);
               }
               netPanel.invalidate();
           }
       };
       adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
           @Override
           public void onItemRangeInserted(int positionStart, int itemCount) {
               super.onItemRangeInserted(positionStart, itemCount);
               int friendlyMessageCount = adapter.getItemCount();
               int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
               // If the recycler view is initially being loaded or the
               // user is at the bottom of the list, scroll to the bottom
               // of the list to show the newly added message.
               if (lastVisiblePosition == -1 ||
                       (positionStart >= (friendlyMessageCount - 1) &&
                               lastVisiblePosition == (positionStart - 1))) {
                   listOfMessage.scrollToPosition(positionStart);
               }
           }
       });
       listOfMessage.setAdapter(adapter);
    }

    private void removeAll() {
        Query applesQuery = mDatabase.child("Pieces"); //.orderByChild("type").equalTo(200);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("NetBattle", "onCancelled", databaseError.toException());
            }
        });
    }

    private void again() {
        Query applesQuery = mDatabase.child("Pieces"); //.orderByChild("type").equalTo(200);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("NetBattle", "onCancelled", databaseError.toException());
            }
        });
     /*   Query disQuery = mDatabase.child("Distribute"); //.orderByChild("type").equalTo(200);
        disQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("NetBattle", "onCancelled", databaseError.toException());
            }
        });*/
        netPanel.restartGame();
        players.clear();
    }

    private void removeLatestPiece() {
        int x = Chess_Panel.myBlackArray.size() + Chess_Panel.myWhiteArray.size();
        Query usersQuery = mDatabase.child("Pieces").orderByChild("number").equalTo(x - 1);
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "onCancelled", databaseError.toException());
            }
        });
    }
    private void dealWithArrays() {
        if (Chess_Panel.myBlackArray.size() > Chess_Panel.myWhiteArray.size()) {
            Chess_Panel.myBlackArray.remove(Chess_Panel.myBlackArray.size() - 1);
        } else {
            Chess_Panel.myWhiteArray.remove(Chess_Panel.myWhiteArray.size() - 1);
        }
        netPanel.invalidate();
    }

    private void againn() {
        Query disQuery = mDatabase.child("Distribute"); //.orderByChild("type").equalTo(200);
        disQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("NetBattle", "onCancelled", databaseError.toException());
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public void onBackPressed() {
        String myEmail = dealWith(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("inBattle").setValue(0);
        FirebaseDatabase.getInstance().getReference().child("Players").child(myEmail).child("enemy").setValue(null);
        if (checkIfEnemyLogout == 0) {
            mDatabase.child("Distribute").child(dealWith(getEmail())).child("offline").setValue(1);
        } else {
            againn();
        }
        again();
        //againn();
        //player = 0;
        netPanel.mode = 0;
        NetBattlePalyers.reminder = 0;
        NetBattlePalyers.reminderForAccept = 0;
        finish();
        // your code.
    }
}
