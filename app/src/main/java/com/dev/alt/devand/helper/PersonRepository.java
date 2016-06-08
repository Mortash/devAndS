package com.dev.alt.devand.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersonRepository extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moments";

    // Table Names
    private static final String TABLE_PERSON = "person";

    // PERSON Table - column names
    private static final String KEY_LOGIN = "login";
    private static final String KEY_MAIL = "mail";
    private static final String KEY_SOCIALKEY = "socialKey";
    private static final String KEY_LOGGEDIN = "loggedIn";

    // Table Create Statements
    private static final String CREATE_TABLE_PERSON = "CREATE TABLE "
            + TABLE_PERSON + "(" + KEY_LOGIN
            + " TEXT," + KEY_MAIL + " TEXT," + KEY_SOCIALKEY
            + " TEXT," + KEY_LOGGEDIN + " INTEGER)";

    public PersonRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);

        db.execSQL(CREATE_TABLE_PERSON);
    }

    /**
     * CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new Person
    public void addPerson(PersonEntity person) {
        //Log.d("add person", "ajout person");
        //Log.d("add person", person.getLogin());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, person.getLogin()); // Contact Name
        values.put(KEY_MAIL, person.getMail()); // Contact Phone
        values.put(KEY_SOCIALKEY, person.getSocialKey()); // Contact Phone
        values.put(KEY_LOGGEDIN, person.getLoggedIn()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_PERSON, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Person
    public PersonEntity getPerson(String login) {
        //Log.d("get person", "recherche person");
        //Log.d("get person", login);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON, new String[] { KEY_LOGIN,
                        KEY_MAIL, KEY_SOCIALKEY, KEY_LOGGEDIN }, KEY_LOGIN + "=?",
                new String[] { String.valueOf(login) }, null, null, null, null);
        if (cursor.getCount() != 0)
            cursor.moveToFirst();
        else
            return null;

        PersonEntity person = new PersonEntity(cursor.getString(0), cursor.getString(1),
                cursor.getString(2), cursor.getInt(3));

        cursor.close();
        db.close(); // Closing database connection
        return person;
    }

    // Getting single Person
    public boolean existPerson(String login) {
        //Log.d("exist person", "existe person");
        //Log.d("exist person", login);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON, new String[] { KEY_LOGIN }, KEY_LOGIN + "=?",
                new String[] { String.valueOf(login) }, null, null, null, null);

        if (cursor.getCount()==0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    // Updating single person
    public int updatePerson(PersonEntity person) {
        //Log.d("update person", "maj person");
        //Log.d("update person", person.getLogin());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, person.getLogin());
        values.put(KEY_MAIL, person.getMail());
        values.put(KEY_SOCIALKEY, person.getSocialKey());
        values.put(KEY_LOGGEDIN, person.getLoggedIn());

        return db.update(TABLE_PERSON, values, KEY_LOGIN + " = ?",
                new String[] { String.valueOf(person.getLogin()) });
    }

    // Deleting single person
    public void deletePerson(PersonEntity person) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PERSON, KEY_LOGIN + " = ?",
                new String[] { String.valueOf(person.getLogin()) });
        db.close();
    }
}
