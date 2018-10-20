package com.ftc5466.citrusscouter;

import android.database.Cursor;
import android.support.annotation.NonNull;

public class Team {
    // Team Information
    private String teamName;
    private int teamNumber;

    // Autonomous Data
    private boolean beginsLatched;
    private boolean claimsDepot;
    private boolean detectGoldMineral;
    private boolean parkInCraterAutonomous;

    // TeleOp Data
    private int mineralsInDepot;
    private int mineralsInLander;

    // End Game Data
    private boolean endsLatched;
    private boolean partialParkInCrater;
    private boolean fullParkInCrater;

    public Team(String name, int number) {
        teamName = name;
        teamNumber = number;
        beginsLatched = false;
        claimsDepot = false;
        detectGoldMineral = false;
        parkInCraterAutonomous = false;

        mineralsInDepot = 0;
        mineralsInLander = 0;

        endsLatched = false;
        partialParkInCrater = false;
        fullParkInCrater = false;
    }

    /**
     * Create a Team object from a database {@link Cursor}
     * @param cursor the cursor from the database
     */
    public Team(Cursor cursor) {
        teamName = cursor.getString(cursor.getColumnIndex(TeamsContract.COLUMN_TEAM_NAME));
        teamNumber = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_TEAM_NUMBER));

        beginsLatched = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_BEGINS_LATCHED)) == 1;
        claimsDepot = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_CLAIMS_DEPOT)) == 1;
        detectGoldMineral = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_DETECTS_GOLD_MINERAL)) == 1;
        parkInCraterAutonomous = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_PARKS_IN_CRATER_AUTONOMOUS)) == 1;

        mineralsInDepot = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_MINERALS_IN_DEPOT));
        mineralsInLander = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_MINERALS_IN_LANDER));

        endsLatched = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_ENDS_LATCHED)) == 1;
        partialParkInCrater = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_PARTIAL_PARK)) == 1;
        fullParkInCrater = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_FULL_PARK)) == 1;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public boolean canDetectGoldMineral() {
        return detectGoldMineral;
    }

    public void setDetectGoldMineral(boolean detectGoldMineral) {
        this.detectGoldMineral = detectGoldMineral;
    }

    public boolean beginsLatched() {
        return beginsLatched;
    }

    public void setBeginsLatched(boolean beginsLatched) {
        this.beginsLatched = beginsLatched;
    }

    public boolean isClaimsDepot() {
        return claimsDepot;
    }

    public void setClaimsDepot(boolean claimsDepot) {
        this.claimsDepot = claimsDepot;
    }

    public boolean isParkInCraterAutonomous() {
        return parkInCraterAutonomous;
    }

    public void setParkInCraterAutonomous(boolean parkInCraterAutonomous) {
        this.parkInCraterAutonomous = parkInCraterAutonomous;
    }

    public int getMineralsInDepot() {
        return mineralsInDepot;
    }

    public void setMineralsInDepot(int mineralsInDepot) {
        this.mineralsInDepot = mineralsInDepot;
    }

    public int getMineralsInLander() {
        return mineralsInLander;
    }

    public void setMineralsInLander(int mineralsInLander) {
        this.mineralsInLander = mineralsInLander;
    }

    public boolean isEndsLatched() {
        return endsLatched;
    }

    public void setEndsLatched(boolean endsLatched) {
        this.endsLatched = endsLatched;
    }

    public boolean isPartialParkInCrater() {
        return partialParkInCrater;
    }

    public void setPartialParkInCrater(boolean partialParkInCrater) {
        this.partialParkInCrater = partialParkInCrater;
    }

    public boolean isFullParkInCrater() {
        return fullParkInCrater;
    }

    public void setFullParkInCrater(boolean fullParkInCrater) {
        this.fullParkInCrater = fullParkInCrater;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Autonomous\n");
        sb.append("Begins Latched: %s\n");
        sb.append("Claims Depot: %s\n");
        sb.append("Detects Gold Mineral: %s\n");
        sb.append("Parks in Crater: %s\n");
        sb.append("\nTeleOp\n");
        sb.append("Minerals in Depot: %d\n");
        sb.append("Minerals in Lander: %d\n");
        sb.append("\nEnd Game\n");
        sb.append("Ends Game Latched: %s\n");
        sb.append("Partial Park In Crater: %s\n");
        sb.append("Full Park In Crater: %s\n");

        String s = String.format(sb.toString(),
                beginsLatched, claimsDepot, detectGoldMineral, parkInCraterAutonomous,
                mineralsInDepot, mineralsInLander, endsLatched, partialParkInCrater, fullParkInCrater);

        s = s.replaceAll("false", "No");
        s = s.replaceAll("true", "Yes");
        return s;
    }
}
