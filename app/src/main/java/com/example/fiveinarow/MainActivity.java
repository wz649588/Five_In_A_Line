package com.example.fiveinarow;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Chess_Panel panel;
    private AlertDialog.Builder builder;
    private Button newGame;
    private Button unDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = (Chess_Panel) findViewById(R.id.main_panel);
        newGame = (Button) findViewById(R.id.new_game);
        unDo = (Button) findViewById(R.id.undo);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.restartGame();
            }
        });
        unDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panel.mode == 0) {
                    if (panel.isBlack == true && panel.myWhiteArray.size() > 0) {
                        panel.myWhiteArray.remove(panel.myWhiteArray.size() - 1);
                        panel.isBlack = false;
                        panel.invalidate();
                    } else if (panel.myBlackArray.size() > 0) {
                        panel.myBlackArray.remove(panel.myBlackArray.size() - 1);
                        panel.isBlack = true;
                        panel.invalidate();
                    }
                } else {
                    if (panel.myWhiteArray.size() > 0) {
                        panel.chessMap[panel.myWhiteArray.get(panel.myWhiteArray.size() - 1).x]
                                [panel.myWhiteArray.get(panel.myWhiteArray.size() - 1).y] = ChessType.NONE;
                        panel.myWhiteArray.remove(panel.myWhiteArray.size() - 1);
                    }
                    if (panel.myBlackArray.size() > 0) {
                        panel.chessMap[panel.myBlackArray.get(panel.myBlackArray.size() - 1).x]
                                [panel.myBlackArray.get(panel.myBlackArray.size() - 1).y] = ChessType.NONE;
                        panel.myBlackArray.remove(panel.myBlackArray.size() - 1);
                    }
                     panel.invalidate();
                }
            }
        });
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Game Over");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
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
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);//给当前活动创建菜单
        return true;//若false，则无法显示菜单
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.p_vc:
                if (panel.mode == 1) {
                    Toast.makeText(this, "已经是人机了", Toast.LENGTH_SHORT).show();
                } else {
                    panel.mode = 1;
                    panel.restartGame();
                    Log.d("MainActivity", "" + panel.mode);
                }
                break;
            case R.id.p_vp:
                if (panel.mode == 0) {
                    Toast.makeText(this, "已经是双人对战了", Toast.LENGTH_SHORT).show();
                } else {
                    panel.mode = 0;
                    panel.restartGame();
                    Log.d("MainActivity", "" + panel.mode);
                }
                break;
            default:
        }
        return true;
    }//菜单中的item作用

}
