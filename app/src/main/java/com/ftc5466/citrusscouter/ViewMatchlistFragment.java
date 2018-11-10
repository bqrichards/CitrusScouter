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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ViewMatchlistFragment extends Fragment {
    private EditText[][] editTexts;
    private GridLayout gridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_matchlist, container, false);
        setHasOptionsMenu(true);
        gridLayout = rootView.findViewById(R.id.view_matchlist_gridLayout);
        try {
            if (CitrusDb.getInstance().matchlist != null) {
                fill();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            showNewMatchlistAlert();
            return true;
        } else if (id == R.id.action_save_matchlist) {
            save();
            return true;
        } else if (id == R.id.action_load_matchlist) {
            // TODO - show dialog with saves
            File[] localFiles = CitrusDb.getInstance().matchlists(getContext());
            ArrayList<String> filenames = new ArrayList<>();
            for (File f : localFiles) {
                String filename = f.getName();
                String ext = filename.substring(filename.lastIndexOf("."));
                if (ext.equals(".matchlist")) {
                    filenames.add(filename);
                }
            }
            showLoadMatchlistAlert(filenames.toArray(new CharSequence[]{}));
            return true;
        } else if (id == R.id.action_edit_matchlist) {
            // TODO - show edit screen
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNewMatchlistAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle("New Matchlist")
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

                        CitrusDb.getInstance().newMatchlist(getContext(), filename, numberOfMatches);
                        try {
                            fill();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void showLoadMatchlistAlert(CharSequence[] options) {
        new AlertDialog.Builder(getContext())
                .setTitle("Load Matchlist")
                .setSingleChoiceItems(options, -1, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedIndex = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (checkedIndex == -1) {
                            return;
                        }

                        String checkedFilename = (String)((AlertDialog)dialog).getListView().getItemAtPosition(checkedIndex);
                        boolean matchlistLoaded = CitrusDb.getInstance().loadMatchlist(getContext(), checkedFilename);
                        if (matchlistLoaded) {
                            try {
                                fill();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void fill() throws JSONException {
        if (editTexts != null) { // Delete previous EditTexts
            for (int i = 0; i < editTexts.length; i++) {
                gridLayout.removeView(gridLayout.findViewWithTag(i+1));
                for (EditText column : editTexts[i]) {
                    gridLayout.removeView(column);
                }
            }
        }

        int rows = CitrusDb.getInstance().matchlist.length();
        int columns = CitrusDb.getInstance().matchlist.getJSONArray(0).length();
        editTexts = new EditText[rows][columns];

        for (int i = 0; i < rows; i++) {
            TextView matchNumberTextView = new TextView(getContext());
            matchNumberTextView.setText(String.valueOf(i+1));
            matchNumberTextView.setTag(i+1);
            GridLayout.LayoutParams textParam = new GridLayout.LayoutParams();
            textParam.width = GridLayout.LayoutParams.WRAP_CONTENT;
            textParam.height = GridLayout.LayoutParams.WRAP_CONTENT;
            textParam.rowSpec = GridLayout.spec(i+1);
            textParam.columnSpec = GridLayout.spec(0);
            matchNumberTextView.setLayoutParams(textParam);
            gridLayout.addView(matchNumberTextView);

            for (int j = 0; j < columns; j++) {
                EditText editText = new EditText(getContext());
                String teamNumber = CitrusDb.getInstance().matchlist.getJSONArray(i).getString(j);
                editText.setText(teamNumber);
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

        CitrusDb.getInstance().save(getContext());
    }
}
