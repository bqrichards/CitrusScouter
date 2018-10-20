package com.ftc5466.citrusscouter;

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
