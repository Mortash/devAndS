package com.dev.alt.devand.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseRepository extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moments";

    // Table Names
    private static final String TABLE_PERSON = "person";
    private static final String TABLE_PICTURE = "picture";

    // PERSON Table - column names
    private static final String KEY_LOGIN = "login";
    private static final String KEY_MAIL = "mail";
    private static final String KEY_SOCIALKEY = "socialKey";
    private static final String KEY_LOGGEDIN = "loggedIn";

    private static final String KEY_IDPICTURE = "idpicture";
    private static final String KEY_IDWALK = "idwalk";
    private static final String KEY_TAKENDATE = "takendate";
    private static final String KEY_AUTOLOCATION = "autolocation";
    private static final String KEY_PATHPICTURE = "pathpicture";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    // Table Create Statements
    private static final String CREATE_TABLE_PERSON = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PERSON + "(" + KEY_LOGIN
            + " TEXT," + KEY_MAIL + " TEXT," + KEY_SOCIALKEY
            + " TEXT," + KEY_LOGGEDIN + " INTEGER);";

    private static final String CREATE_TABLE_PICTURE = "CREATE TABLE "
            + TABLE_PICTURE + "("
            + KEY_IDPICTURE + " INTEGER,"
            + KEY_IDWALK + " INTEGER,"
            + KEY_TAKENDATE + " TEXT,"
            + KEY_AUTOLOCATION + " BOOLEAN,"
            + KEY_PATHPICTURE + " TEXT,"
            + KEY_LATITUDE + " DOUBLE,"
            + KEY_LONGITUDE + " DOUBLE,"
            + KEY_LOGIN + " TEXT);";

    public DataBaseRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("personRepo","je crée pr");
        db.execSQL(CREATE_TABLE_PERSON);
        db.execSQL(CREATE_TABLE_PICTURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("personRepo","je recrée pr");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE + ";");

        db.execSQL(CREATE_TABLE_PERSON);
        db.execSQL(CREATE_TABLE_PICTURE);
    }

    /**
     * CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new Person
    public void addPerson(PersonEntity person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, person.getLogin());
        values.put(KEY_MAIL, person.getMail());
        values.put(KEY_SOCIALKEY, person.getSocialKey());
        values.put(KEY_LOGGEDIN, person.getLoggedIn());

        // Inserting Row
        db.insert(TABLE_PERSON, null, values);
        db.close();
    }

    // Getting single Person
    public PersonEntity getPerson(String login) {
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
        db.close();

        return person;
    }

    // Getting single Person
    public boolean existPerson(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSON, new String[] { KEY_LOGIN }, KEY_LOGIN + "=?",
                new String[] { String.valueOf(login) }, null, null, null, null);

        if (cursor.getCount()==0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    // Updating single person
    public int updatePerson(PersonEntity person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, person.getLogin());
        values.put(KEY_MAIL, person.getMail());
        values.put(KEY_SOCIALKEY, person.getSocialKey());
        values.put(KEY_LOGGEDIN, person.getLoggedIn());

        int res = db.update(TABLE_PERSON, values, KEY_LOGIN + " = ?",new String[] { String.valueOf(person.getLogin()) });
        db.close();

        return res;
    }

    // Deleting single person
    public void deletePerson(PersonEntity person) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PERSON, KEY_LOGIN + " = ?",
                new String[] { String.valueOf(person.getLogin()) });
        db.close();
    }


    /**
     * CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new Picture
    public void addPicture(PictureEntity picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDPICTURE, picture.getIdPicture());
        values.put(KEY_IDWALK, picture.getIdWalk());
        values.put(KEY_TAKENDATE, picture.getTakendate());
        values.put(KEY_AUTOLOCATION, picture.getAutolocation());
        values.put(KEY_PATHPICTURE, picture.getPathPicture());
        values.put(KEY_LATITUDE, picture.getLatitude());
        values.put(KEY_LONGITUDE, picture.getLongitude());
        values.put(KEY_LOGIN, picture.getLogin());

        // Inserting Row
        db.insert(TABLE_PICTURE, null, values);
        db.close();
    }

    // Getting single Picture
    public PictureEntity getPicture(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PICTURE, new String[] { KEY_IDPICTURE, KEY_IDWALK,
                        KEY_TAKENDATE, KEY_AUTOLOCATION, KEY_PATHPICTURE, KEY_LATITUDE,
                        KEY_LONGITUDE, KEY_LOGIN }, KEY_IDPICTURE + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor.getCount() != 0)
            cursor.moveToFirst();
        else {
            db.close();
            return null;
        }

        PictureEntity picture = new PictureEntity(cursor.getInt(0), cursor.getInt(1),
                cursor.getString(2), (cursor.getInt(3)!=0), cursor.getString(4),
                cursor.getDouble(5), cursor.getDouble(6), cursor.getString(7));

        cursor.close();
        db.close();

        return picture;
    }

    // Getting single Picture
    public boolean existPicture(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PICTURE, new String[] { KEY_IDPICTURE }, KEY_IDPICTURE + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor.getCount()==0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    // Getting single Picture
    public List<PictureEntity> getAllPicture() {
        Log.e("picturRepo","getAllPerson");
        SQLiteDatabase db = this.getReadableDatabase();
        List<PictureEntity> res = new ArrayList<>();

        Cursor cursor = db.query(TABLE_PICTURE, new String[] {  }, "",
                new String[] { }, null, null, null, null);

        if (cursor.getCount()!=0) {
            try {
                while (cursor.moveToNext()) {
                    res.add(new PictureEntity(cursor.getInt(0), cursor.getInt(1),
                            cursor.getString(2), (cursor.getInt(3)!=0), cursor.getString(4),
                            cursor.getDouble(5), cursor.getDouble(6), cursor.getString(7)));
                }
            } finally {
                cursor.close();
            }

        }
        db.close();
        return res;
    }

    // Updating single picture
    public int updatePicture(PictureEntity picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDPICTURE, picture.getIdPicture());
        values.put(KEY_IDWALK, picture.getIdWalk());
        values.put(KEY_TAKENDATE, picture.getTakendate());
        values.put(KEY_AUTOLOCATION, picture.getAutolocation());
        values.put(KEY_PATHPICTURE, picture.getPathPicture());
        values.put(KEY_LATITUDE, picture.getLatitude());
        values.put(KEY_LONGITUDE, picture.getLongitude());
        values.put(KEY_LOGIN, picture.getLogin());

        int res = db.update(TABLE_PICTURE, values, KEY_IDPICTURE + " = ?", new String[] { String.valueOf(picture.getIdPicture()) });
        db.close();
        return res;
    }

    // Deleting single picture
    public void deletePicture(PictureEntity picture) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PICTURE, KEY_IDPICTURE + " = ?",
                new String[] { String.valueOf(picture.getIdPicture()) });
        db.close();
    }
}
