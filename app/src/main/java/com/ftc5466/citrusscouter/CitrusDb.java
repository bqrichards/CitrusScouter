package com.ftc5466.citrusscouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CitrusDb extends SQLiteOpenHelper  {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Citrus.db";
    private static CitrusDb db;

    // Matchlist
    private String matchlistFilename;
    public JSONArray matchlist;

    private CitrusDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        matchlistFilename = PreferenceManager.getDefaultSharedPreferences(context).getString("MATCHLIST_FILENAME", null);

        if (matchlistFilename != null) {
            // Try to load matchlist
            String contents = readFromFile(context, matchlistFilename);
        }
    }

    public static void init(Context context) {
        db = new CitrusDb(context);
    }

    public static CitrusDb getInstance() {
        return db;
    }

    public JSONArray newMatchlist(String filename, int matches) {
        matchlistFilename = filename;
        matchlist = new JSONArray();

        for (int i = 0; i < matches; i++) {
            JSONArray array = new JSONArray(); // Create an array of 4 placeholders
            array.put(0);
            array.put(0);
            array.put(0);
            array.put(0);
            matchlist.put(array);
        }

        return matchlist;
    }

    public void insertTeam(Team team) {
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

        db.insert(TeamsContract.TABLE_NAME, null, values);
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

    public void insertTeamIntoMatchlist(int teamNumber, int row, int column) {
        try {
            matchlist.getJSONArray(row).put(column, teamNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        // TODO - write matchlist to disk
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

    // https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
    private void writeToFile(Context context, String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("CitrusException", "File write failed: " + e.toString());
        }
    }

    // https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
    private String readFromFile(Context context, String filename) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e(MainActivity.LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }

    private File[] matchlists(Context context) {
        File path = context.getFilesDir();
        // TODO - check for .matchlist file extention
        return path.listFiles();
    }
}
