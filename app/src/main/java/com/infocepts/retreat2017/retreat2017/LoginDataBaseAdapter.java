package com.infocepts.retreat2017.retreat2017;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by jjagwan on 30-01-2017.
 */
public class LoginDataBaseAdapter {
    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;
    static final String DATABASE_CREATE = "create table " + "LOGIN" + "( "
            + "ID" + " integer primary key autoincrement,"
            + "EMPLOYEENAME  text,EMPLOYEEID text); ";
    public SQLiteDatabase db;
    private final Context context;
    private DataBaseHelper dbHelper;

    public LoginDataBaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    public LoginDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String userName, String password) {
        ContentValues newValues = new ContentValues();
        newValues.put("EMPLOYEENAME", userName);
        newValues.put("EMPLOYEEID", password);
        db.insert("LOGIN", null, newValues);

    }

    public int deleteEntry(String UserName) {

        String where = "EMPLOYEENAME=?";
        int numberOFEntriesDeleted = db.delete("LOGIN", where,
                new String[] { UserName });
        return numberOFEntriesDeleted;
    }

    public String getSinlgeEntry(String userName) {
        Cursor cursor = db.query("LOGIN", null, " EMPLOYEENAME=?",
                new String[] { userName }, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("EMPLOYEEID"));
        cursor.close();
        return password;
    }

    public void updateEntry(String userName, String password) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("EMPLOYEENAME", userName);
        updatedValues.put("EMPLOYEEID", password);

        String where = "USERNAME = ?";
        db.update("LOGIN", updatedValues, where, new String[] { userName });
    }
}
