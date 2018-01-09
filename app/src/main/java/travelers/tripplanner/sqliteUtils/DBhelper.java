package travelers.tripplanner.sqliteUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

    //Fields for the class
    private static final String DatabaseName = "trips.db";
    public static final int version = 1;

    public DBhelper(Context context) {
        super(context, DatabaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Trip(TripID INTEGER PRIMARY KEY AUTOINCREMENT,TripName text, visitNum INTEGER)");
        db.execSQL("create table Visit(VisitID INTEGER PRIMARY KEY AUTOINCREMENT,VisitName text, Type text, Address text, photoURL text, Rating REAL, TripId INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Trip");
        db.execSQL("DROP TABLE IF EXISTS Visit");
        onCreate(db);
    }
}