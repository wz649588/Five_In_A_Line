package com.example.fiveinarow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NetBattle extends AppCompatActivity {

    private Chess_Panel netPanel;
    private Button netNewGame;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign) {
            Toast.makeText(this, "signed out", Toast.LENGTH_SHORT).show();
            netPanel.restartGame();
            finish();
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_battle);

        netPanel = (Chess_Panel) findViewById(R.id.net_panel);
        netNewGame = (Button) findViewById(R.id.netNewGame);

        netNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netPanel.restartGame();
            }
        });
    }
}
