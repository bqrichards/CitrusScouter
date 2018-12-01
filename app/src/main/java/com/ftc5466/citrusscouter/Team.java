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
    private String preferredAutoStart;

    // TeleOp Data
    private int mineralsInDepot;
    private int mineralsInLander;

    // End Game Data
    private boolean endsLatched;
    private boolean partialParkInCrater;
    private boolean fullParkInCrater;

    private String notes;

    public Team() {

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
        preferredAutoStart = cursor.getString(cursor.getColumnIndex(TeamsContract.COLUMN_PREFERRED_AUTO_START));

        mineralsInDepot = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_MINERALS_IN_DEPOT));
        mineralsInLander = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_MINERALS_IN_LANDER));

        endsLatched = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_ENDS_LATCHED)) == 1;
        partialParkInCrater = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_PARTIAL_PARK)) == 1;
        fullParkInCrater = cursor.getInt(cursor.getColumnIndex(TeamsContract.COLUMN_FULL_PARK)) == 1;

        notes = cursor.getString(cursor.getColumnIndex(TeamsContract.COLUMN_NOTES));
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

    public String getPreferredAutoStart() {
        return preferredAutoStart;
    }

    public void setPreferredAutoStart(String preferredAutoStart) {
        this.preferredAutoStart = preferredAutoStart;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getExported() {
        StringBuilder sb = new StringBuilder();
        sb.append(teamName).append(">")
                .append(teamNumber).append(">")
                .append(beginsLatched).append(">")
                .append(claimsDepot).append(">")
                .append(detectGoldMineral).append(">")
                .append(parkInCraterAutonomous).append(">")
                .append(preferredAutoStart).append(">")
                .append(mineralsInDepot).append(">")
                .append(mineralsInLander).append(">")
                .append(endsLatched).append(">")
                .append(partialParkInCrater).append(">")
                .append(fullParkInCrater).append(">")
                .append(notes).append(">");

        String s = sb.toString();
        s = s.replace("true", "t");
        s = s.replace("false", "f");
        s = s.replace("Crater", "c");
        s = s.replace("Either", "e");
        s = s.replace("Depot", "d");
        s = s.replace("Neither", "n");

        return s;
    }

    @NonNull
    @Override
    public String toString() {
        String sb = "";
        sb += "Autonomous\n";
        sb += "Begins Latched: %s\n";
        sb += "Claims Depot: %s\n";
        sb += "Detects Gold Mineral: %s\n";
        sb += "Parks in Crater: %s\n";
        sb += "Preferred Auto Start: %s\n";
        sb += "\nTeleOp\n";
        sb += "Minerals in Depot: %d\n";
        sb += "Minerals in Lander: %d\n";
        sb += "\nEnd Game\n";
        sb += "Ends Game Latched: %s\n";
        sb += "Partial Park In Crater: %s\n";
        sb += "Full Park In Crater: %s\n";
        sb += "Notes: %s";

        String s = String.format(sb,
                beginsLatched, claimsDepot, detectGoldMineral, parkInCraterAutonomous, preferredAutoStart,
                mineralsInDepot, mineralsInLander, endsLatched, partialParkInCrater, fullParkInCrater, notes.isEmpty() ? "None" : notes);

        s = s.replaceAll("false", "No");
        s = s.replaceAll("true", "Yes");
        return s;
    }
}
