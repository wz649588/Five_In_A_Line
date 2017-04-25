package com.example.fiveinarow;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;


public class NetBattle extends AppCompatActivity {

    private Chess_Panel netPanel;
    private Button netNewGame;
    private AlertDialog.Builder builder;
    public static int player = 0;
    private Button chooseBlack;
    private Button chooseWhite;
    private DatabaseReference mDatabase;
    private Button start;
    private ArrayList<Distribute> players = new ArrayList<>();

    private static int SIGN_IN_REQUEST_CODE = 1;
    LinearLayout activity_net_battle;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign) {
            again();
            player = 0;
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_net_battle, "You have been signed out", Snackbar.LENGTH_SHORT).show();
                    netPanel.mode = 0;
                    finish();
                }
            });
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

        activity_net_battle = (LinearLayout) findViewById(R.id.activity_net_battle);
        netPanel = (Chess_Panel) findViewById(R.id.net_panel);
        netNewGame = (Button) findViewById(R.id.netNewGame);
        chooseBlack = (Button) findViewById(R.id.ChooseBlack);
        chooseWhite = (Button) findViewById(R.id.ChooseWhite);
        start = (Button) findViewById(R.id.Start);

        netPanel.mode = 2;
        mDatabase = FirebaseDatabase.getInstance().getReference();


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player == 0) {
                    player = 50;
                    mDatabase.child("Distribute").push().setValue(new Distribute(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                }
            }
        });

        netNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                again();
                player = 0;
            }
        });
        chooseBlack.setOnClickListener(new View.OnClickListener() {
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
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else {
            Snackbar.make(activity_net_battle, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT)
                    .show();
            displayPiece();
            displayDistribute();
        }



        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder = new AlertDialog.Builder(NetBattle.this);
        builder.setTitle("Game Over");
        builder.setPositiveButton("Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                again();
                if(player == 100) {
                    player = 200;
                } else {
                    player = 100;
                }
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                again();
                player = 0;
                netPanel.mode = 0;
                startActivity(new Intent(NetBattle.this, MainActivity.class));
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
        FirebaseRecyclerAdapter<Distribute, DistributeViewHolder> adapter = new FirebaseRecyclerAdapter<Distribute, DistributeViewHolder>(Distribute.class, R.layout.listviewd, DistributeViewHolder.class, FirebaseDatabase.getInstance().
                getReference().child("Distribute")) {
            @Override
            protected void populateViewHolder(DistributeViewHolder distributeViewHolder, Distribute model, int position) {
                distributeViewHolder.emailp.setText(model.getEmail());
                distributeViewHolder.ranp.setText(model.getRan()+"");

                players.add(new Distribute(model.getEmail(), model.getRan()));
                if(players.size() == 2 && player == 50) {
                    if(players.get(0).getEmail() == FirebaseAuth.getInstance().getCurrentUser().getEmail()){
                        if(players.get(0).getRan() > players.get(1).getRan()) {
                            player = 200;
                            Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                        } else {
                            player = 100;
                            Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(players.get(1).getEmail() == FirebaseAuth.getInstance().getCurrentUser().getEmail()){
                        if(players.get(1).getRan() > players.get(0).getRan()) {
                            player = 200;
                            Toast.makeText(NetBattle.this, "你执黑", Toast.LENGTH_SHORT).show();
                        } else {
                            player = 100;
                            Toast.makeText(NetBattle.this, "你执白", Toast.LENGTH_SHORT).show();
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
       FirebaseRecyclerAdapter<Piece, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Piece, UsersViewHolder>(Piece.class, R.layout.list_view, UsersViewHolder.class, FirebaseDatabase.getInstance().
               getReference().child("Pieces")) {
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
        Query applesQuery = FirebaseDatabase.getInstance().getReference().child("Pieces"); //.orderByChild("type").equalTo(200);
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
        Query applesQuery = FirebaseDatabase.getInstance().getReference().child("Pieces"); //.orderByChild("type").equalTo(200);
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
        Query disQuery = FirebaseDatabase.getInstance().getReference().child("Distribute"); //.orderByChild("type").equalTo(200);
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
        netPanel.restartGame();
        players.clear();
    }
}
