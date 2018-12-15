package com.ftc5466.citrusscouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

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
            loadMatchlist(context, matchlistFilename);
        }
    }

    public static void init(Context context) {
        db = new CitrusDb(context);
    }

    public static CitrusDb getInstance() {
        return db;
    }

    public void newMatchlist(Context context, String filename, int matches) {
        matchlistFilename = filename + ".matchlist";
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("MATCHLIST_FILENAME", matchlistFilename).apply();
        matchlist = new JSONArray();

        for (int i = 0; i < matches; i++) {
            JSONArray array = new JSONArray(); // Create an array of 4 placeholders
            array.put(0);
            array.put(0);
            array.put(0);
            array.put(0);
            matchlist.put(array);
        }

        save(context);
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
        values.put(TeamsContract.COLUMN_PREFERRED_AUTO_START, team.getPreferredAutoStart());
        // TeleOp
        values.put(TeamsContract.COLUMN_MINERALS_IN_DEPOT, team.getMineralsInDepot());
        values.put(TeamsContract.COLUMN_MINERALS_IN_LANDER, team.getMineralsInLander());
        // End Game
        values.put(TeamsContract.COLUMN_ENDS_LATCHED, team.isEndsLatched());
        values.put(TeamsContract.COLUMN_PARTIAL_PARK, team.isPartialParkInCrater());
        values.put(TeamsContract.COLUMN_FULL_PARK, team.isFullParkInCrater());
        values.put(TeamsContract.COLUMN_NOTES, team.getNotes());

        db.replace(TeamsContract.TABLE_NAME, null, values);
    }

    public LinkedList<Team> getTeams() {
        SQLiteDatabase db = getReadableDatabase();

        String sortOrder = TeamsContract.COLUMN_TEAM_NUMBER + " ASC";
        Cursor cursor = db.query(TeamsContract.TABLE_NAME, null, null, null, null, null, sortOrder);
        LinkedList<Team> results = new LinkedList<>();
        while (cursor.moveToNext()) {
            results.add(new Team(cursor));
        }

        cursor.close();

        return results;
    }

    public Team getTeam(int teamNumber) {
        Team returnTeam = null;

        String selection = TeamsContract.COLUMN_TEAM_NUMBER + "=?";
        String[] selectionArgs = {String.valueOf(teamNumber)};

        Cursor cursor = getReadableDatabase().query(TeamsContract.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            returnTeam = new Team(cursor);
        }

        cursor.close();
        return returnTeam;
    }

    public Team getTeam(String teamName) {
        Team returnTeam = null;

        String selection = TeamsContract.COLUMN_TEAM_NAME + "=?";
        String[] selectionArgs = {String.valueOf(teamName)};

        Cursor cursor = getReadableDatabase().query(TeamsContract.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            returnTeam = new Team(cursor);
        }

        cursor.close();
        return returnTeam;
    }

    public void deleteTeam(String teamNumber) {
        getWritableDatabase().delete(TeamsContract.TABLE_NAME, TeamsContract.COLUMN_TEAM_NUMBER + "=?", new String[]{teamNumber});
    }

    public void insertTeamIntoMatchlist(int teamNumber, int row, int column) {
        try {
            matchlist.getJSONArray(row).put(column, teamNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean loadMatchlist(Context context, String filename) {
        // Try to load matchlist
        String contents = readFromFile(context, filename);
        if (contents == null) {
            MainActivity.log("Matchlist " + filename + " not found. :(");
            return false;
        } else {
            MainActivity.log("Found matchlist " + filename + "! Loading...");
            try {
                matchlist = new JSONArray(contents);
                matchlistFilename = filename;
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("MATCHLIST_FILENAME", matchlistFilename).apply();
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                MainActivity.log(e.getMessage());
                return false;
            }
        }
    }

    public void deleteMatchlist(Context context) {
        context.deleteFile(matchlistFilename);
        matchlist = null;
        Toast.makeText(context, "Deleted " + matchlistFilename, Toast.LENGTH_LONG).show();
        matchlistFilename = null;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("MATCHLIST_FILENAME", matchlistFilename).apply();
    }

    public void save(Context context) {
        writeToFile(context, matchlistFilename, matchlist.toString());
        Toast.makeText(context, "Saved " + matchlistFilename, Toast.LENGTH_SHORT).show();
    }

    public void importFromString(String databaseText) {
        String[] teamStrings = databaseText.split("\\|");
        if (teamStrings.length == 0) {
            return;
        }

        for (int i = 0; i < teamStrings.length; i++) {
            Team newTeam = new Team(teamStrings[i]);
            insertTeam(newTeam);
        }
    }

    public String getExported() {
        StringBuilder sb = new StringBuilder();
        LinkedList<Team> teams = getTeams();
        for (int i = 0; i < teams.size(); i++) {
            sb.append(teams.get(i).getExported());
            if (i != teams.size() - 1) {
                sb.append("|");
            }
        }

        return sb.toString();
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
        String ret = null;

        try {
            InputStream inputStream = context.openFileInput(filename);
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public File[] matchlists(Context context) {
        File path = context.getFilesDir();
        MatchlistFileFilter filter = new MatchlistFileFilter();
        return path.listFiles(filter);
    }

    private class MatchlistFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getAbsolutePath().endsWith(".matchlist");
        }
    }
}
