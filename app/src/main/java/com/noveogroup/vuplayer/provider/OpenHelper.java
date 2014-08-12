package com.noveogroup.vuplayer.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vuplayer.db";
    private static final int DATABASE_VERSION = 1;
    private static OpenHelper instance = null;

    private OpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static OpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new OpenHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String str = "CREATE TABLE " + ContentDescriptor.Notes.TABLE_NAME + " ( " +
                ContentDescriptor.Notes.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ContentDescriptor.Notes.Cols.WORD_COMBINATION + " TEXT NOT NULL," +
                ContentDescriptor.Notes.Cols.SOURCE + " TEXT," +
                ContentDescriptor.Notes.Cols.COMMENT + " TEXT" +
                " );";
        Log.d("OpenHelper", "str create = " + str);
        sqLiteDatabase.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContentDescriptor.Notes.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

}
