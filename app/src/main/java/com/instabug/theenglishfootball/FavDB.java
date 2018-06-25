package com.instabug.theenglishfootball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Class for handling all database operations for Match class
 */

public class FavDB extends SQLiteOpenHelper {

    /**
     * Constructor with version 1 as its our first release
     */
    public FavDB(Context context) {
        super(context, "theEnglishFootball", null,
                1);
    }

    /**
     *  Create Favourite table
     *
     *  @param SQLiteDatabase
     *  @return
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAV_TABLE = "create table Favourite ( id integer primary key ,team1 " +
                "text not null,team2 text not null,result text not null,date text not null," +
                "isFinished int not null,api_id integer not null,unique (api_id) on " +
                "conflict ignore)";
        db.execSQL(CREATE_FAV_TABLE);
    }

    /**
     *  Upgrade the database by recreating the table
     *
     *  @param SQLiteDatabase, old version, new version
     *  @return
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS Favourite");
        // Create tables again
        onCreate(db);
    }

    /**
     *  Add new Match record
     *
     *  @param Match
     *  @return
     */
    public void addMatch(Match match) {
        SQLiteDatabase mydb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put("team1", match.getTeam1());
        values.put("team2", match.getTeam2());
        values.put("result", match.getResult());
        values.put("date", match.getDateAsString());
        values.put("isFinished", match.isFinished());
        values.put("api_id", match.getApi_id());

        mydb.insert("Favourite", null, values);
        mydb.close();
    }

    /**
     * Get all matches
     *
     *  @param
     *  @return ArrayList<Match>
     */
    public ArrayList<Match> getAllMatchs() throws ParseException {
        ArrayList<Match> matchList = new ArrayList<Match>();

        String selectQuery = "SELECT  * FROM Favourite";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Match match = new Match();
                match.setId(cursor.getInt(0));
                match.setTeam1(cursor.getString(1));
                match.setTeam2(cursor.getString(2));
                match.setResult(cursor.getString(3));
                match.setDate(cursor.getString(4));
                match.setFinished((cursor.getInt(5) != 0));
                match.setApi_id(cursor.getInt(6));

                matchList.add(match);
            } while (cursor.moveToNext());
        }
        db.close();

        return matchList;
    }

    /**
     * Update Match by its ID
     *
     *  @param Match
     *  @return no of updated rows
     */
    public int updateMatch(Match match)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("team1", match.getTeam1());
        values.put("team2", match.getTeam2());
        values.put("result", match.getResult());
        values.put("date", match.getDateAsString());
        values.put("isFinished", match.isFinished());
        values.put("api_id", match.getApi_id());

        int updateItems = db.update("Favourite", values,"id = ?",
                new String[] {String.valueOf(match.getId())} );
        db.close();
        return updateItems;
    }

    /**
     * Delete Match by its ID
     *
     *  @param ID
     *  @return
     */
    public void deleteMatchByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Favourite", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
