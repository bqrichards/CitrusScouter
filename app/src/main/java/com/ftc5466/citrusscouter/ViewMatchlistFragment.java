package com.ftc5466.citrusscouter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;

import java.io.File;

public class ViewMatchlistFragment extends Fragment {
    private String myTeamNumber;
    private EditText[][] editTexts;
    private GridLayout gridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_matchlist, container, false);
        setHasOptionsMenu(true);
        myTeamNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("MY_TEAM_NUMBER", null);
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

        if (id == R.id.action_new_matchlist) {
            showNewMatchlistAlert();
            return true;
        } else if (id == R.id.action_save_matchlist) {
            save();
            return true;
        } else if (id == R.id.action_load_matchlist) {
            File[] localFiles = CitrusDb.getInstance().matchlists(getContext());
            CharSequence[] filenames = new CharSequence[localFiles.length];
            for (int i = 0; i < localFiles.length; i++) {
                filenames[i] = localFiles[i].getName();
            }
            showLoadMatchlistAlert(filenames);
            return true;
        } else if (id == R.id.action_delete_matchlist) {
            showDeleteMatchlistAlert();
            return true;
        } else if (id == R.id.action_import_matchlist) {
            Intent i = new Intent(getContext(), ImportFromQRActivity.class);
            i.putExtra(ImportFromQRActivity.INTENT_PURPOSE_KEY, ImportFromQRActivity.INTENT_PURPOSE_MATCHLIST);
            startActivityForResult(i, MainActivity.IMPORT_FROM_QR_CODE_REQUEST);
            return true;
        } else if (id == R.id.action_export_matchlist) {
            String exported = CitrusDb.getInstance().getExportedMatchlist(getContext());
            if (exported == null || exported.isEmpty()) {
                Toast.makeText(getContext(), "Cannot export empty matchlist", Toast.LENGTH_SHORT).show();
                return true;
            }

            QRCodeWriter writer = new QRCodeWriter();
            try {
                int width = 550, height = 550;
                BitMatrix matrix = writer.encode(exported, BarcodeFormat.QR_CODE, width, height);
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(bitmap);
                new AlertDialog.Builder(getContext())
                        .setTitle("Exported Matchlist")
                        .setView(imageView)
                        .show();
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == R.id.action_set_my_team_number) {
            showSetMyTeamNumberAlert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.IMPORT_FROM_QR_CODE_REQUEST) {
            if (CitrusDb.getInstance().matchlist == null) {
                return;
            }

            String msg = "Matchlist " + CitrusDb.getInstance().matchlistFilename + " imported!" +
                    " Please load matchlist for imported data!";
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    private void showNewMatchlistAlert() {
        View newMatchlistView = View.inflate(getContext(), R.layout.dialog_new_matchlist, null);

        // Add text change listener onto EditText
        final EditText matchlistNameEditText = newMatchlistView.findViewById(R.id.matchlist_file_name);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String reservedChars = "|\\?*<\":>+[]/'";
                for (int i = start; i < end; i++) {
                    for (char c : reservedChars.toCharArray()) {
                        if (source.charAt(i) == c) {
                            Toast.makeText(getContext(), "Invalid character entered", Toast.LENGTH_SHORT).show();
                            return "";
                        }
                    }
                }

                return null;
            }
        };

        matchlistNameEditText.setFilters(new InputFilter[] {filter});

        new AlertDialog.Builder(getContext())
                .setTitle("New Matchlist")
                .setMessage("Enter in the name for this matchlist")
                .setView(newMatchlistView)
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

    private void showDeleteMatchlistAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle("*Delete Matchlist*")
                .setMessage("Please don't do this ;-; I have a family.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CitrusDb.getInstance().deleteMatchlist(getContext());
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

    private void showSetMyTeamNumberAlert() {
        final EditText editText = new EditText(getContext());
        editText.setHint("Team Number");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(getContext())
                .setTitle("Set my Team Number")
                .setMessage("Please enter your team number below. This allows the matchlist to highlight matches you're in.")
                .setView(editText)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Integer.parseInt(editText.getText().toString());
                            myTeamNumber = editText.getText().toString();
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("MY_TEAM_NUMBER", myTeamNumber).apply();
                            Toast.makeText(getContext(), "Please reload matchlist to highlight", Toast.LENGTH_LONG).show();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed; Not a valid number", Toast.LENGTH_LONG).show();
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

        if (CitrusDb.getInstance().matchlist == null) {
            return;
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
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                String teamNumber = CitrusDb.getInstance().matchlist.getJSONArray(i).getString(j);
                editText.setText(teamNumber);
                if (myTeamNumber != null && myTeamNumber.equals(teamNumber)) {
                    matchNumberTextView.setBackgroundColor(Color.YELLOW);
                }
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.rowSpec = GridLayout.spec(i+1);
                param.columnSpec = GridLayout.spec(j+1);
                param.setGravity(1);
                editText.setLayoutParams(param);
                gridLayout.addView(editText);
                editTexts[i][j] = editText;
            }
        }
    }

    private void save() {
        if (editTexts == null) {
            return;
        }

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
