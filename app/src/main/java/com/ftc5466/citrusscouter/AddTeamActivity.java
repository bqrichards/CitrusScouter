package com.ftc5466.citrusscouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddTeamActivity extends AppCompatActivity {
    // Team Information
    private EditText teamNameEditText, teamNumberEditText;

    // Autonomous
    private CheckBox beginsLatchedCheckBox, claimsDepotCheckBox;
    private CheckBox detectsGoldMineralCheckBox, parkInCraterCheckBox;

    // TeleOp
    private EditText mineralsInDepotEditText, mineralsInLanderEditText;

    // End Game
    private CheckBox endsLatchedCheckBox;
    private RadioButton craterParkingNoRadioButton, craterParkingPartialRadioButton, craterParkingFullRadioButton;

    private EditText notesEditText;

    // Other
    private Button autoFillButton;
    private Team possibleAutoFillTeam;
    private enum AutoFillEntryField {
        TEAM_NAME, TEAM_NUMBER
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        teamNameEditText = findViewById(R.id.team_name_editText);
        teamNumberEditText = findViewById(R.id.team_number_editText);

        teamNameEditText.addTextChangedListener(new AutoFillTextWatcher(AutoFillEntryField.TEAM_NAME));
        teamNumberEditText.addTextChangedListener(new AutoFillTextWatcher(AutoFillEntryField.TEAM_NUMBER));

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

        notesEditText = findViewById(R.id.notes_editText);
        autoFillButton = findViewById(R.id.auto_fill_button);
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

        int mineralsInDepot;
        if (mineralsInDepotEditText.getText() == null ||
                mineralsInDepotEditText.getText().toString().isEmpty()) {
            mineralsInDepot = 0;
        } else {
            try {
                mineralsInDepot = Integer.valueOf(mineralsInDepotEditText.getText().toString());
            } catch (NumberFormatException e) {
                mineralsInDepot = 0;
            }
        }

        int mineralsInLander;
        if (mineralsInLanderEditText.getText() == null ||
                mineralsInLanderEditText.getText().toString().isEmpty()) {
            mineralsInLander = 0;
        } else {
            try {
                mineralsInLander = Integer.valueOf(mineralsInLanderEditText.getText().toString());
            } catch (NumberFormatException e) {
                mineralsInLander = 0;
            }
        }

        boolean endsLatched = endsLatchedCheckBox.isChecked();
        boolean craterParkingPartial = craterParkingPartialRadioButton.isChecked();
        boolean craterPartialFull = craterParkingFullRadioButton.isChecked();

        String notes = "";
        try {
            notes = notesEditText.getText().toString();
        } catch (NullPointerException ignored) {}

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
        newTeam.setNotes(notes);

        CitrusDb.getInstance().insertTeam(newTeam);
        Toast.makeText(this, "Team saved!", Toast.LENGTH_SHORT).show();

        setResult(0);
        finish();
    }

    public void autoFill(View view) {
        if (possibleAutoFillTeam == null) { return; }

        teamNameEditText.setText(possibleAutoFillTeam.getTeamName());
        teamNumberEditText.setText(String.valueOf(possibleAutoFillTeam.getTeamNumber()));

        // Autonomous
        beginsLatchedCheckBox.setChecked(possibleAutoFillTeam.beginsLatched());
        claimsDepotCheckBox.setChecked(possibleAutoFillTeam.isClaimsDepot());
        detectsGoldMineralCheckBox.setChecked(possibleAutoFillTeam.canDetectGoldMineral());
        parkInCraterCheckBox.setChecked(possibleAutoFillTeam.isParkInCraterAutonomous());

        // TeleOp
        mineralsInDepotEditText.setText(String.valueOf(possibleAutoFillTeam.getMineralsInDepot()));
        mineralsInLanderEditText.setText(String.valueOf(possibleAutoFillTeam.getMineralsInLander()));

        // End Game
        endsLatchedCheckBox.setChecked(possibleAutoFillTeam.isEndsLatched());

        if (possibleAutoFillTeam.isPartialParkInCrater()) {
            craterParkingPartialRadioButton.toggle();
        } else if (possibleAutoFillTeam.isFullParkInCrater()) {
            craterParkingFullRadioButton.toggle();
        } else {
            craterParkingNoRadioButton.toggle();
        }

        notesEditText.setText(possibleAutoFillTeam.getNotes());
    }

    private class AutoFillTextWatcher implements TextWatcher {
        AutoFillEntryField entryField;

        public AutoFillTextWatcher(AutoFillEntryField entryField) {
            this.entryField = entryField;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            Team possibleTeam = null;

            if (entryField == AutoFillEntryField.TEAM_NUMBER) {
                try {
                    int teamNumber = Integer.parseInt(s.toString());
                    possibleTeam = CitrusDb.getInstance().getTeam(teamNumber);
                } catch (NumberFormatException ignored) {}
            } else if (entryField == AutoFillEntryField.TEAM_NAME) {
                possibleTeam = CitrusDb.getInstance().getTeam(s.toString());
            }

            if (possibleTeam == null) {
                if (autoFillButton.getVisibility() == View.VISIBLE) {
                    autoFillButton.setVisibility(View.GONE);
                    possibleAutoFillTeam = null;
                }
            } else {
                if (autoFillButton.getVisibility() == View.GONE) {
                    autoFillButton.setVisibility(View.VISIBLE);
                    possibleAutoFillTeam = possibleTeam;
                }
            }
        }
    }
}
