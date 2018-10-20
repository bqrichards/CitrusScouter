package com.ftc5466.citrusscouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddTeamActivity extends AppCompatActivity {
    // Team Information
    EditText teamNameEditText, teamNumberEditText;

    // Autonomous
    CheckBox beginsLatchedCheckBox, claimsDepotCheckBox;
    CheckBox detectsGoldMineralCheckBox, parkInCraterCheckBox;

    // TeleOp
    EditText mineralsInDepotEditText, mineralsInLanderEditText;

    // End Game
    CheckBox endsLatchedCheckBox;
    RadioButton craterParkingNoRadioButton, craterParkingPartialRadioButton, craterParkingFullRadioButton;

    // Database
    CitrusDb db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
        db = new CitrusDb(this);

        teamNameEditText = findViewById(R.id.team_name_editText);
        teamNumberEditText = findViewById(R.id.team_number_editText);

        beginsLatchedCheckBox = findViewById(R.id.begins_latched_checkBox);
        claimsDepotCheckBox = findViewById(R.id.claim_depot_checkBox);
        detectsGoldMineralCheckBox = findViewById(R.id.detect_gold_mineral_checkBox);
        parkInCraterCheckBox = findViewById(R.id.park_in_crater_autonomous_checkBox);

        mineralsInDepotEditText = findViewById(R.id.minerals_in_depot_editText);
        mineralsInLanderEditText = findViewById(R.id.minerals_in_lander_editText);

        endsLatchedCheckBox = findViewById(R.id.ends_latched_checkBox);
        craterParkingNoRadioButton = findViewById(R.id.crater_parking_no_radioButton);
        craterParkingPartialRadioButton = findViewById(R.id.crater_parking_partial_radioButton);
        craterParkingFullRadioButton = findViewById(R.id.crater_parking_full_radioButton);
    }

    public void save(View view) {
        String teamName = teamNameEditText.getText().toString();

        if (teamName.isEmpty()) {
            Toast.makeText(this, "Team Name cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } if (teamNumberEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Team Number cannot be blank", Toast.LENGTH_LONG).show();
            return;
        }

        int teamNumber = Integer.valueOf(teamNumberEditText.getText().toString());

        boolean beginsLatched = beginsLatchedCheckBox.isChecked();
        boolean claimsDepot = claimsDepotCheckBox.isChecked();
        boolean detectsGoldMineral = detectsGoldMineralCheckBox.isChecked();
        boolean parkInCrater = parkInCraterCheckBox.isChecked();

        if (mineralsInDepotEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Minerals in Depot cannot be blank", Toast.LENGTH_LONG).show();
            return;
        } else if (mineralsInLanderEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Minerals in Lander cannot be blank", Toast.LENGTH_LONG).show();
            return;
        }

        int mineralsInDepot = Integer.valueOf(mineralsInDepotEditText.getText().toString());
        int mineralsInLander = Integer.valueOf(mineralsInLanderEditText.getText().toString());

        boolean endsLatched = endsLatchedCheckBox.isChecked();
        boolean craterParkingPartial = craterParkingPartialRadioButton.isChecked();
        boolean craterPartialFull = craterParkingFullRadioButton.isChecked();

        Team newTeam = new Team(teamName, teamNumber);
        newTeam.setBeginsLatched(beginsLatched);
        newTeam.setClaimsDepot(claimsDepot);
        newTeam.setDetectGoldMineral(detectsGoldMineral);
        newTeam.setParkInCraterAutonomous(parkInCrater);
        newTeam.setMineralsInDepot(mineralsInDepot);
        newTeam.setMineralsInLander(mineralsInLander);
        newTeam.setEndsLatched(endsLatched);
        newTeam.setPartialParkInCrater(craterParkingPartial);
        newTeam.setFullParkInCrater(craterPartialFull);

        db.insertTeam(newTeam);
        Toast.makeText(this, "Team saved!", Toast.LENGTH_SHORT).show();

        setResult(0);
        finish();
    }
}
