package com.ftc5466.citrusscouter;

import android.provider.BaseColumns;

public class CitrusDb {
    private static final CitrusDb INSTANCE = new CitrusDb();

    private CitrusDb() {}

    public static CitrusDb getInstance() {
        return INSTANCE;
    }

    private static class Contract implements BaseColumns {
        public static final String TABLE_NAME = "entry";
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
    }
}
