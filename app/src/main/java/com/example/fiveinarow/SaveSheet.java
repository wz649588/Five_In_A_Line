package com.example.fiveinarow;

import android.content.Context;
import android.graphics.Point;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wz649 on 2017/4/26.
 */

public class SaveSheet {
    private List<Point> whiteArray;
    private List<Point> blackArray;
    String fileName;
    String inputText;
    Context context;
    public SaveSheet(List<Point> blackArray, List<Point> whiteArray, String fileName, Context context) {
        this.blackArray = blackArray;
        this.whiteArray = whiteArray;
        this.context = context;
        this.fileName = fileName;
        inputText = parseText();
    }

    private String parseText() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < blackArray.size()) {
            sb.append(blackArray.get(i).x + ",");
            sb.append(blackArray.get(i).y + ",");
            if (i < whiteArray.size()){
                sb.append(whiteArray.get(i).x + ",");
                sb.append(whiteArray.get(i).y + ",");
            }
            i++;
        }
        return sb.toString();
    }

    public void save() {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
