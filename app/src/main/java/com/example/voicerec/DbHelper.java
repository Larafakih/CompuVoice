package com.example.voicerec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CalculatorOperations";

    // Contacts table name
    private static final String TABLE_OPERATIONS = "operations";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_RESULT = "result";

    public DbHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_OPERATIONS_TABLE =
                "CREATE TABLE " + TABLE_OPERATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_FORMULA + " TEXT," + KEY_RESULT + " TEXT" + ")";
        db.execSQL(CREATE_OPERATIONS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATIONS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    long addOperation(Operation operation) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMULA, operation.getFormula());
        values.put(KEY_RESULT, operation.getResult());

        // Inserting Row
        long id = db.insert(TABLE_OPERATIONS, null, values);
        db.close(); // Closing database connection
        return id;
    }


    //delete an operation
    int deleteOperation(Operation operation)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMULA, operation.getFormula());
        values.put(KEY_RESULT, operation.getResult());
        int l = db.delete(TABLE_OPERATIONS,KEY_ID + " =? ", new String[]{String.valueOf(operation.getId())});

        db.close(); // Closing database connection
        return l;
    }
    int clearOperations()
    {
        SQLiteDatabase db = this.getWritableDatabase();

      int l=  db.delete(TABLE_OPERATIONS, "1", null);

        db.close(); // Closing database connection
        return l;
    }

    // Getting single operation
    Operation getOperation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPERATIONS, new String[]{KEY_ID,
                        KEY_FORMULA, KEY_RESULT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Operation operation = new Operation(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));
            // return operation
            return operation;
        }
        db.close();
        return null;
    }

    //getting list of Operations
    List<Operation> getAllOperations() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OPERATIONS;
        Cursor cursor = db.rawQuery(query, null);

        List<Operation> operations = new ArrayList<Operation>();

        if (cursor.moveToFirst()) {
            do {
                Operation operation = new Operation(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2));

                operations.add(operation);

            } while (cursor.moveToNext());
        }
        db.close();
// return operations
        return operations;
    }
}
