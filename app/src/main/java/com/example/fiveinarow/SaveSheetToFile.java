package com.example.fiveinarow;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.TwitterCollection;

import java.io.File;

/**
 * Created by wz649 on 2017/4/29.
 */

public class SaveSheetToFile {

    private Context context;
    private Chess_Panel savePanel;

    public SaveSheetToFile(Context context, Chess_Panel savePanel) {
        this.context = context;
        this.savePanel = savePanel;
    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("输入文件名");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String modifiedName = modify(input.getText().toString());
                SaveSheet saveSheet = new SaveSheet(savePanel.myBlackArray, savePanel.myWhiteArray, modifiedName, context);
                saveSheet.save();
                Toast.makeText(context, "已保存为文件 " + modifiedName, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private boolean checkFileExist(String filename) {
        File folder = new File("/data/data/com.example.fiveinarow/files");
        File[] listOfFiles = folder.listFiles();
        for(File file : listOfFiles) {
            if (file.getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    private String modify(String name) {
        if(!checkFileExist(name)) return name;
        int i = 1;
        while(true) {
            if(!checkFileExist(name + "(" + i + ")")) return name + "(" + i + ")";
            i++;
        }
    }
}
