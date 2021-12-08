package com.example.screentofacedistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    public Database(Context Context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(Context,"ScreenToFaceDistance",factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("Create table comments(id integer primary key autoincrement, comment text)");

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int ver, int ver2) {
        sqLiteDatabase.execSQL("drop table comments");
        onCreate(sqLiteDatabase);
    }

    public void insertComment(String text)
    {
        this.getWritableDatabase().execSQL("insert into comments(comment) values('"+text+"')");

    }
    public Cursor viewComments() {
        Cursor cursor=this.getReadableDatabase().rawQuery("select * from comments", null);

        return cursor;
    }

    public int GetId(String text) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor getNoteId = myDB.rawQuery("select id from comments where comment = '"+text+"'",null);
        return getNoteId.getColumnIndex("id");
    }
    public void deleteComment(String tv) {
        int id = GetId(tv);
        this.getWritableDatabase().execSQL("delete from comments where id= "+id+"");
    }

    public void updateComment(ArrayList aL) {
        this.getWritableDatabase().execSQL("drop table comments");
        this.getWritableDatabase().execSQL("Create table comments(id integer primary key autoincrement, comment text)");
        for(int i = 0; i< aL.size(); i++) {
            String text = (String) aL.get(i);
            this.getWritableDatabase().execSQL("insert into comments(comment) values('"+text+"')");
        }
    }
}
