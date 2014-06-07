package org.randoamissecours;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OutingOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Outings.db";
	public static final String TABLE_NAME = "outing";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_BEGINNING = "beginning";
	public static final String COLUMN_ENDING = "ending";
	public static final String COLUMN_ALERT = "alert";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	
	public static final String SQL_CREATE_DATABASE =
			"CREATE TABLE " + TABLE_NAME + " (" +
		    COLUMN_ID +" INTEGER PRIMARY KEY," +
			COLUMN_NAME + " TEXT," +
			COLUMN_DESCRIPTION + " TEXT," +
			COLUMN_STATUS + " INTEGER," +
			COLUMN_BEGINNING + " TEXT," +
			COLUMN_ENDING + " TEXT," +
			COLUMN_ALERT + " TEXT," +
			COLUMN_LATITUDE + " REAL," +
			COLUMN_LONGITUDE + " REAL" +
			")";
	public static final String SQL_DELETE_DATABASE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;

	public OutingOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache, so clear the database and recreate it
		db.execSQL(SQL_DELETE_DATABASE);
        onCreate(db);
	}
}