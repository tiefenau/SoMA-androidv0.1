package de.uni_koblenz.soma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 07.06.2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "soma_database";
    private static int DATABASE_VERSION = 1;

    private static final String TABLE_LOCATION = "location";

    // Location Columns
    private static final String KEY_LOCATION_ID = "id";
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    private static final String KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_BEARING = "bearing";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_SPEED = "speed";
    private static final String KEY_LOCATION_TIME = "timestamp";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY, " +
                KEY_LOCATION_ACCURACY + " TEXT, " +
                KEY_LOCATION_ALTITUDE + " TEXT, " +
                KEY_LOCATION_BEARING + " TEXT, " +
                KEY_LOCATION_LATITUDE + " TEXT, " +
                KEY_LOCATION_LONGITUDE + " TEXT, " +
                KEY_LOCATION_TIME + " TEXT, " +
                KEY_LOCATION_SPEED + " TEXT" +
                ")";

        db.execSQL(CREATE_LOCATION_TABLE);
        Log.d(TAG, "onCreate " + db + " " + CREATE_LOCATION_TABLE);
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
            {

            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public long addLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id;

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_LOCATION_ACCURACY, location.getAccuracy());
            values.put(KEY_LOCATION_ALTITUDE, location.getAltitude());
            values.put(KEY_LOCATION_BEARING, location.getBearing());
            values.put(KEY_LOCATION_LATITUDE, location.getLatitude());
            values.put(KEY_LOCATION_LONGITUDE, location.getLongitude());
            values.put(KEY_LOCATION_SPEED, location.getSpeed());
            values.put(KEY_LOCATION_TIME, location.getTime());

            Log.i(TAG, "INSERT VALUES " + values);
            id = db.insertOrThrow(TABLE_LOCATION, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "SUCCESS: " + TABLE_LOCATION + " NEW ID: " + id);
            return id;
        } catch (Exception e) {
            Log.w(TAG, e);
            return -2;
        } finally {
            db.endTransaction();
        }
    }



    /*
     * Get all locations in the database
     */
    public List<DatabaseEntryObject> getLocations() {
        Log.d(TAG, "getLocations");

        String LOCATIONS_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_LOCATION);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATIONS_SELECT_QUERY, null);

        List<DatabaseEntryObject> locations = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    DatabaseEntryObject loc = new DatabaseEntryObject(
                            cursor.getInt(cursor.getColumnIndex(KEY_LOCATION_ID)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ACCURACY)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ALTITUDE)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_BEARING)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LATITUDE)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LONGITUDE)),
                            cursor.getLong(cursor.getColumnIndex(KEY_LOCATION_TIME)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_SPEED))
                    );
                    locations.add(loc);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "Error while trying to get locations from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return locations;
    }

    /**
     * Count all location data
     */
    public long getLocationsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT COUNT(*) FROM %s", TABLE_LOCATION), null);
        cursor.moveToFirst();
        long c = cursor.getInt(0);
        cursor.close();
        return c;
    }

    /**
     * Delete location
     */
    public void deleteLocation(int locationId) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_LOCATION, "id=?", new String[]{String.valueOf(locationId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "deleteLocation: " + locationId + " OK");
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to location");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}