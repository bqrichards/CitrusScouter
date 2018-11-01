package com.ftc5466.citrusscouter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewMatchlistFragment extends Fragment {
    private EditText[][] editTexts;
    private GridLayout gridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_matchlist, container, false);
        setHasOptionsMenu(true);
        gridLayout = rootView.findViewById(R.id.view_matchlist_gridLayout);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_matchlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (id == R.id.action_new_matchlist) {

            builder.setTitle("New Matchlist")
                    .setMessage("Enter in the name for this matchlist")
                    .setView(R.layout.dialog_new_matchlist)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText matchlistFileNameEditText = ((AlertDialog) dialog).findViewById(R.id.matchlist_file_name);
                            EditText numberOfMatchesEditText = ((AlertDialog) dialog).findViewById(R.id.matchlist_number_of_matches);

                            String filename = matchlistFileNameEditText.getText().toString().trim();
                            if (filename.isEmpty()) {
                                Toast.makeText(getContext(), "Couldn't make matchlist. Filename cannot be empty.", Toast.LENGTH_LONG).show();
                                return;
                            } else if (filename.contains(" ")) {
                                Toast.makeText(getContext(), "Couldn't make matchlist. Filename cannot contain space.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            int numberOfMatches;
                            try {
                                numberOfMatches = Integer.parseInt(numberOfMatchesEditText.getText().toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Couldn't make matchlist. Invalid number of matches.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            CitrusDb.getInstance().newMatchlist(filename, numberOfMatches);
                            try {
                                fill();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        } else if (id == R.id.action_save_matchlist) {
            // TODO - save matchlist
            save();
            return true;
        } else if (id == R.id.action_load_matchlist) {
            // TODO - show dialog with saves
            return true;
        } else if (id == R.id.action_edit_matchlist) {
            // TODO - show edit screen
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fill() throws JSONException {
        int rows = CitrusDb.getInstance().matchlist.length();
        int columns = CitrusDb.getInstance().matchlist.getJSONArray(0).length();
        editTexts = new EditText[rows][columns];

        for (int i = 0; i < rows; i++) {
            TextView matchNumberTextView = new TextView(getContext());
            matchNumberTextView.setText(String.valueOf(i+1));
            GridLayout.LayoutParams textParam = new GridLayout.LayoutParams();
            textParam.width = GridLayout.LayoutParams.WRAP_CONTENT;
            textParam.height = GridLayout.LayoutParams.WRAP_CONTENT;
            textParam.rowSpec = GridLayout.spec(i+1);
            textParam.columnSpec = GridLayout.spec(0);
            matchNumberTextView.setLayoutParams(textParam);
            gridLayout.addView(matchNumberTextView);

            for (int j = 0; j < columns; j++) {
                EditText editText = new EditText(getContext());
                editText.setText("0");
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.rowSpec = GridLayout.spec(i+1);
                param.columnSpec = GridLayout.spec(j+1);
                editText.setLayoutParams(param);
                gridLayout.addView(editText);
                editTexts[i][j] = editText;
            }
        }
    }

    private void save() {
        Log.d(MainActivity.LOG_TAG, "Saving data!");
        for (int i = 0; i < editTexts.length; i++) {
            for (int j = 0; j < editTexts[i].length; j++) {
                int teamNumber;
                try {
                    teamNumber = Integer.parseInt(editTexts[i][j].getText().toString());
                } catch (NumberFormatException e) {
                    teamNumber = 0;
                    editTexts[i][j].setText("0");
                }

                CitrusDb.getInstance().insertTeamIntoMatchlist(teamNumber, i, j);
            }
        }

        CitrusDb.getInstance().save();
    }
}
