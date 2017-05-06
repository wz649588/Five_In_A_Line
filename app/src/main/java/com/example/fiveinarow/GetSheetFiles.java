package com.example.fiveinarow;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSheetFiles extends AppCompatActivity {

    ListView fileList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sheet_files);
        fileList = (ListView) findViewById(R.id.get_file_list);
        File folder = new File("/data/data/com.example.fiveinarow/files");
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> listFiles = new ArrayList<File>(Arrays.asList(listOfFiles));
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            files.add(listFiles.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GetSheetFiles.this, android.R.layout.simple_expandable_list_item_1,
        files);
        fileList.setAdapter(adapter);
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = files.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(GetSheetFiles.this);
                builder.setTitle("选择操作");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File deleteFile = listFiles.remove(position);
                        deleteFile.delete();
                        files.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(GetSheetFiles.this,filename+" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("读取棋谱", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReadSheet readSheet = new ReadSheet(filename, GetSheetFiles.this);
                        String chessSheet = readSheet.load();
                        Intent intent= new Intent(GetSheetFiles.this, LoadSheetActivity.class);
                        intent.putExtra("sheet", chessSheet);
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filesreturn, menu);//给当前活动创建菜单
        return true;//若false，则无法显示菜单
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_main:
                finish();
            default:
        }
        return true;
    }//菜单中的item作用
}
