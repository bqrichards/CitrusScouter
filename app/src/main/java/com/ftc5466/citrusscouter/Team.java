package com.ftc5466.citrusscouter;

import android.support.annotation.NonNull;

public class Team {
    // Team Information
    private String teamName;
    private int teamNumber;

    // Autonomous Data
    private boolean detectGoldMineral;

    // TeleOp Data

    // End Game Data

    public Team(String name, int number) {
        teamName = name;
        teamNumber = number;
    }

    public static Team testTeam() {
        Team t = new Team("Test Team", 0);
        t.setDetectGoldMineral(true);
        return t;
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

    @NonNull
    @Override
    public String toString() {
        return "Detects Gold Mineral: " + (detectGoldMineral ? "Yes" : "No");
    }
}
