package com.ftc5466.citrusscouter;

import android.provider.BaseColumns;

public class TeamsContract implements BaseColumns {
    public static final String TABLE_NAME = "Teams";
    public static final String COLUMN_TEAM_NAME = "teamName";
    public static final String COLUMN_TEAM_NUMBER = "teamNumber";
    public static final String COLUMN_BEGINS_LATCHED = "beginsLatched";
    public static final String COLUMN_CLAIMS_DEPOT = "claimsDepot";
    public static final String COLUMN_DETECTS_GOLD_MINERAL = "detectsGoldMineral";
    public static final String COLUMN_PARKS_IN_CRATER_AUTONOMOUS = "parksInCraterAutonomous";
    public static final String COLUMN_MINERALS_IN_DEPOT = "mineralsInDepot";
    public static final String COLUMN_MINERALS_IN_LANDER = "mineralsInLander";
    public static final String COLUMN_ENDS_LATCHED = "endsLatched";
    public static final String COLUMN_PARTIAL_PARK = "partialPark";
    public static final String COLUMN_FULL_PARK = "fullPark";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_TEAM_NUMBER + " INTEGER PRIMARY KEY," +
            COLUMN_TEAM_NAME + " TEXT," +
            COLUMN_BEGINS_LATCHED + " BOOLEAN," +
            COLUMN_CLAIMS_DEPOT + " BOOLEAN," +
            COLUMN_DETECTS_GOLD_MINERAL + " BOOLEAN," +
            COLUMN_PARKS_IN_CRATER_AUTONOMOUS + " BOOLEAN," +
            COLUMN_MINERALS_IN_DEPOT + " INTEGER," +
            COLUMN_MINERALS_IN_LANDER + " INTEGER," +
            COLUMN_ENDS_LATCHED + " BOOLEAN," +
            COLUMN_PARTIAL_PARK + " BOOLEAN," +
            COLUMN_FULL_PARK + " BOOLEAN)";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String[] KEYS = {
            BaseColumns._ID, COLUMN_TEAM_NAME, COLUMN_TEAM_NUMBER,
            COLUMN_BEGINS_LATCHED, COLUMN_CLAIMS_DEPOT, COLUMN_DETECTS_GOLD_MINERAL,
            COLUMN_PARKS_IN_CRATER_AUTONOMOUS, COLUMN_MINERALS_IN_DEPOT, COLUMN_MINERALS_IN_LANDER,
            COLUMN_ENDS_LATCHED, COLUMN_PARTIAL_PARK, COLUMN_FULL_PARK
    };
}
