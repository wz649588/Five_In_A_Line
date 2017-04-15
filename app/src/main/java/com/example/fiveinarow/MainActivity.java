package com.example.fiveinarow;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private Chess_Panel panel;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        panel = (Chess_Panel) findViewById(R.id.main_panel);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Game Over");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setPositiveButton("Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                panel.restartGame();
            }
        });

        panel.setOnGameListener(new OnGameListener() {
            @Override
            public void onGameOver(int i) {
                String str = "";
                if (i == Chess_Panel.WHITE_WIN) {
                    str = "White win";
                } else if (i == Chess_Panel.BLACK_WIN) {
                    str = "Black win";
                }
                builder.setMessage(str);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 0;
                params.y = panel.getmUnder();
                dialogWindow.setAttributes(params);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }
}
