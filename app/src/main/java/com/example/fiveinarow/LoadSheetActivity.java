package com.example.fiveinarow;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class LoadSheetActivity extends AppCompatActivity {

    private Chess_Panel sheetPanel;
    private String sheet;
    private ImageButton left;
    private ImageButton right;
    int position = 0;
    int sheetSize;
    private String[] sheetArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_sheet);
        Intent intent = getIntent();
        sheet = intent.getStringExtra("sheet");
        left = (ImageButton) findViewById(R.id.to_left);
        right = (ImageButton) findViewById(R.id.to_right);
        sheetArray = sheet.split(",");
        sheetSize = sheetArray.length / 2 - 1;
        Log.d("LoadSheetActivity", sheetArray.length+"");
        sheetPanel = (Chess_Panel) findViewById(R.id.sheet_panel);
        sheetPanel.mode = 3;

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= sheetSize) {
                    if (position % 2 == 0){
                        sheetPanel.myBlackArray.add(new Point(Integer.parseInt(sheetArray[position * 2]), Integer.parseInt(sheetArray[position * 2 +1 ])));
                        sheetPanel.invalidate();
                    }
                    else {
                        sheetPanel.myWhiteArray.add(new Point(Integer.parseInt(sheetArray[position * 2]), Integer.parseInt(sheetArray[position * 2 +1 ])));
                        sheetPanel.invalidate();
                    }
                    position++;
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    if (position % 2 != 0) {
                        sheetPanel.myBlackArray.remove(position / 2);
                        sheetPanel.invalidate();
                    } else {
                        sheetPanel.myWhiteArray.remove(position / 2 - 1);
                        sheetPanel.invalidate();
                    }
                    position--;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sheetPanel.restartGame();
        sheetPanel.mode = 0;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.goback, menu);//给当前活动创建菜单
        return true;//若false，则无法显示菜单
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_new:
                sheetPanel.restartGame();
                finish();
            default:
        }
        return true;
    }//菜单中的item作用
}