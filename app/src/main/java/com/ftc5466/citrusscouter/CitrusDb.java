package com.ftc5466.citrusscouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class CitrusDb extends SQLiteOpenHelper  {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Citrus.db";

    public CitrusDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long insertTeam(Team team) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        // Team Information
        values.put(TeamsContract.COLUMN_TEAM_NAME, team.getTeamName());
        values.put(TeamsContract.COLUMN_TEAM_NUMBER, team.getTeamNumber());
        // Autonomous
        values.put(TeamsContract.COLUMN_BEGINS_LATCHED, team.beginsLatched());
        values.put(TeamsContract.COLUMN_CLAIMS_DEPOT, team.isClaimsDepot());
        values.put(TeamsContract.COLUMN_DETECTS_GOLD_MINERAL, team.canDetectGoldMineral());
        values.put(TeamsContract.COLUMN_PARKS_IN_CRATER_AUTONOMOUS, team.isParkInCraterAutonomous());
        // TeleOp
        values.put(TeamsContract.COLUMN_MINERALS_IN_DEPOT, team.getMineralsInDepot());
        values.put(TeamsContract.COLUMN_MINERALS_IN_LANDER, team.getMineralsInLander());
        // End Game
        values.put(TeamsContract.COLUMN_ENDS_LATCHED, team.isEndsLatched());
        values.put(TeamsContract.COLUMN_PARTIAL_PARK, team.isPartialParkInCrater());
        values.put(TeamsContract.COLUMN_FULL_PARK, team.isFullParkInCrater());

        return db.insert(TeamsContract.TABLE_NAME, null, values);
    }

    public ArrayList<Team> getTeams() {
        SQLiteDatabase db = getReadableDatabase();

        String sortOrder = TeamsContract.COLUMN_TEAM_NUMBER + " ASC";
        Cursor cursor = db.query(TeamsContract.TABLE_NAME, null, null, null, null, null, sortOrder);
        ArrayList<Team> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new Team(cursor));
        }

        cursor.close();

        return results;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TeamsContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TeamsContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
