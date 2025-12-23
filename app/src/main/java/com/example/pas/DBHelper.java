package com.example.pas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pas_messages.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE messages (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT NOT NULL," +
                        "content TEXT NOT NULL," +
                        "image_uri TEXT," +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    public boolean insertMessage(String title, String content, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("image_uri", imageUri);
        return db.insert("messages", null, cv) != -1;
    }

    public Cursor getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM messages ORDER BY id DESC", null);
    }

    public Cursor getMessageById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM messages WHERE id=?", new String[]{String.valueOf(id)});
    }

    public boolean updateMessage(int id, String title, String content, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("image_uri", imageUri);
        return db.update("messages", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("messages", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor searchMessages(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + keyword + "%";
        return db.rawQuery(
                "SELECT * FROM messages WHERE title LIKE ? OR content LIKE ? ORDER BY id DESC",
                new String[]{like, like}
        );
    }
}
