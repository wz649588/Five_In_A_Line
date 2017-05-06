package com.example.fiveinarow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Chess_Panel extends View {
    private int myPanelWidth;
    private float myLineHeight;
    private int maxLine = 15;


    private MediaPlayer lost;
    private MediaPlayer put;
    private MediaPlayer win;
    public Canvas canvas;
    private Paint myPaint;
    private Bitmap myWhitePiece;
    private Bitmap myBlackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f /4;

    private boolean isGameOver;
    public static int WHITE_WIN = 0;
    public static int BLACK_WIN = 1;
    public static boolean isBlack = true;

    public static List<Point> myWhiteArray = new ArrayList<Point>();
    public static List<Point> myBlackArray = new ArrayList<Point>();

    private OnGameListener onGameListener;
    private int mUnder;

    public static int mode;
    public static ChessType[][] chessMap = new ChessType[15][15];
    private ChessType computerType = ChessType.WHITE;
    private ChessType playerType = ChessType.BLACK;
    public static ComputerPlayer computerPlayer = new ComputerPlayer(chessMap, ChessType.WHITE, ChessType.BLACK);

    public Chess_Panel(Context context){
        this(context, null);
    }

    public Chess_Panel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }


    public void setOnGameListener(OnGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }


    private void init() {
        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setStyle(Paint.Style.STROKE);
        mode = 0;

        lost = MediaPlayer.create(getContext(), R.raw.lost);
        put = MediaPlayer.create(getContext(), R.raw.put);
        win = MediaPlayer.create(getContext(), R.raw.win);


        myWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        myBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                chessMap[i][j] = ChessType.NONE;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y);



            if (myWhiteArray.contains(p) || myBlackArray.contains(p)) {
                return false;
            }

            put.start();

            if (mode == 0) {
                if (isBlack) {
                    myBlackArray.add(p);
                    chessMap[p.x][p.y] = ChessType.BLACK;
                } else {
                    myWhiteArray.add(p);
                    chessMap[p.x][p.y] = ChessType.WHITE;
                }
                invalidate();
                isBlack = !isBlack;
            } else if (mode == 1){
                if (playerType == ChessType.BLACK) {
                    myBlackArray.add(p);
                    chessMap[p.x][p.y] = ChessType.BLACK;
                } else {
                    myWhiteArray.add(p);
                    chessMap[p.x][p.y] = ChessType.WHITE;
                }
                invalidate();
                Point comp = computerPlayer.start();
                chessMap[comp.x][comp.y] = this.computerType;
                if (this.computerType == ChessType.BLACK) {
                    myBlackArray.add(comp);
                } else {
                    myWhiteArray.add(comp);
                }
                invalidate();
            } else if(mode == 2){
                Piece piece;
                if(myBlackArray.size() == myWhiteArray.size() && NetBattle.player == 200){
                    piece = new Piece(p.x, p.y, NetBattle.player, myBlackArray.size() + myWhiteArray.size());
                    NetBattle.mDatabase.child("Pieces").child((myBlackArray.size() + myWhiteArray.size())+"").setValue(piece);
                } else if(myBlackArray.size() == myWhiteArray.size() + 1 && NetBattle.player == 100){
                    piece = new Piece(p.x, p.y, NetBattle.player, myBlackArray.size() + myWhiteArray.size());
                    NetBattle.mDatabase.child("Pieces").child((myBlackArray.size() + myWhiteArray.size())+"").setValue(piece);
                }
            }
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x / myLineHeight), (int)(y/myLineHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myPanelWidth = w;
        myLineHeight = myPanelWidth * 1.0f / maxLine;
        mUnder = h - (h - myPanelWidth) / 2;

        int pieceWidth = (int) (myLineHeight * ratioPieceOfLineHeight);
        myWhitePiece = Bitmap.createScaledBitmap(myWhitePiece, pieceWidth, pieceWidth, false);
        myBlackPiece = Bitmap.createScaledBitmap(myBlackPiece, pieceWidth, pieceWidth, false);
    }

    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    public void drawBoard(Canvas canvas) {
        int w = myPanelWidth;
        float lineHeight = myLineHeight;
        int startX = (int) (lineHeight / 2);
        int endX = (int) (w - lineHeight / 2);
        for (int i = 0; i <= maxLine; i++) {
            int y = (int) ((i + 0.5) * lineHeight);
            canvas.drawLine(startX, y, endX, y, myPaint);
            canvas.drawLine(y, startX, y, endX, myPaint);
        }
    }

    public void drawPiece(Canvas canvas) {
        int n1 = myWhiteArray.size();
        int n2 = myBlackArray.size();
        for (int i = 0; i < n1; i++) {
            Point whitePoint = myWhiteArray.get(i);
            canvas.drawBitmap(myWhitePiece, (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * myLineHeight, (whitePoint.y + (
                    1 - ratioPieceOfLineHeight) / 2) * myLineHeight, null);
        }
        for (int i = 0; i < n2; i++) {
            Point blackPoint = myBlackArray.get(i);
            canvas.drawBitmap(myBlackPiece, (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * myLineHeight, (blackPoint.y + (
                    1 - ratioPieceOfLineHeight) / 2) * myLineHeight, null);
        }
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(myWhiteArray);
        boolean blackWin = checkFiveInLine(myBlackArray);

        if (whiteWin || blackWin) {
            if (mode == 0) {
                win.start();
            } else if(mode ==1) {
                if (whiteWin) {
                    lost.start();
                } else {
                    win.start();
                }
            } else {
                if ((whiteWin && NetBattle.player == 100) || (blackWin && NetBattle.player == 200)) {
                    win.start();
                } else {
                    lost.start();
                }
            }

            if (onGameListener != null) {
                onGameListener.onGameOver(whiteWin? WHITE_WIN : BLACK_WIN);
            }
        }
    }

    private boolean checkFiveInLine(List<Point> myArray) {
        for (Point p : myArray) {
            int x = p.x;
            int y = p.y;
            boolean win_flag = checkHorizontal(x, y, myArray) || checkVertical(x, y, myArray)
                    || checkLeftDiagonal(x, y, myArray) || checkRightDiagonal(x, y, myArray);
            if (win_flag) {
                return true;
            }
        }
        return false;
    }

    public int getmUnder() {
        return mUnder;
    }

    private boolean checkHorizontal(int x, int y, List<Point> myArray) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x+i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x-i, y))) {
                count++;
            } else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> myArray) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x, y-i))){
                count++;
            } else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> myArray) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x-i, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x+i, y-i))){
                count++;
            } else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> myArray) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x-i, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for (int i = 1; i < 5; i++) {
            if (myArray.contains(new Point(x+i, y+i))){
                count++;
            } else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    protected void restartGame() {
        myWhiteArray.clear();
        myBlackArray.clear();
        isGameOver = false;
        isBlack = true;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                chessMap[i][j] = ChessType.NONE;
            }
        }
        invalidate();
    }
}
