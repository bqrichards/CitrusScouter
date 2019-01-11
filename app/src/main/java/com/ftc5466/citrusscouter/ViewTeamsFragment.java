package com.ftc5466.citrusscouter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.LinkedList;

import static com.ftc5466.citrusscouter.MainActivity.CHANGE_TEAM_INFO_REQUEST;

public class ViewTeamsFragment extends Fragment {
    private ViewTeamDataAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_teams, container, false);

        adapter = new ViewTeamDataAdapter();
        ExpandableListView listView = rootView.findViewById(R.id.view_teams_expandable_list_view);
        listView.setAdapter(adapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view_teams, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_team) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText teamNumberEntryEditText = new EditText(getContext());
            teamNumberEntryEditText.setHint(R.string.team_number);
            teamNumberEntryEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle("Delete Team")
                    .setMessage("Enter the team number to completely disintegrate it.")
                    .setView(teamNumberEntryEditText)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try { // Make sure text entered is actually a number
                                Integer.parseInt(teamNumberEntryEditText.getText().toString());
                            } catch (NumberFormatException e) {
                                return;
                            }

                            CitrusDb.getInstance().deleteTeam(teamNumberEntryEditText.getText().toString());
                            refresh();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        } else if (id == R.id.action_export_to_qr) {
            String exported = CitrusDb.getInstance().getExportedTeams();
            if (exported.isEmpty()) {
                Toast.makeText(getContext(), "Cannot export empty database", Toast.LENGTH_SHORT).show();
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
                        .setTitle("Exported Teams Database")
                        .setView(imageView)
                        .show();
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == R.id.action_show_statistics) {
            Intent i = new Intent(getContext(), StatisticsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_import_from_qr) {
            Intent i = new Intent(getContext(), ImportFromQRActivity.class);
            i.putExtra(ImportFromQRActivity.INTENT_PURPOSE_KEY, ImportFromQRActivity.INTENT_PURPOSE_TEAMS);
            startActivityForResult(i, MainActivity.IMPORT_FROM_QR_CODE_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.IMPORT_FROM_QR_CODE_REQUEST) {
            refresh();
        }
    }

    /**
     * Refresh the View Team List
     */
    public void refresh() {
        adapter.refresh();
    }

    private class ViewTeamDataAdapter extends BaseExpandableListAdapter {
        private LinkedList<Team> groups;

        public ViewTeamDataAdapter() {
            groups = CitrusDb.getInstance().getTeams();
        }

        public void refresh() {
            groups = CitrusDb.getInstance().getTeams();
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition).getTeamNumber() + " - " + groups.get(groupPosition).getTeamName();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }

            TextView mainView = convertView.findViewById(android.R.id.text1);
            mainView.setText((String) getGroup(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_child_item, parent, false);
            }

            TextView mainView = convertView.findViewById(R.id.list_child_item_text);
            mainView.setText((getChild(groupPosition, childPosition)).toString());

            final String teamNumber = ((String) getGroup(groupPosition)).split(" -")[0];
            Button editButton = convertView.findViewById(R.id.list_child_edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), AddTeamActivity.class);
                    i.putExtra(AddTeamActivity.PRESET_TEAM_NUMBER_INTENT_KEY, teamNumber);
                    if (getActivity() != null) {
                        getActivity().startActivityForResult(i, CHANGE_TEAM_INFO_REQUEST);
                    }
                }
            });

            Button deleteButton = convertView.findViewById(R.id.list_child_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Team " + teamNumber + "?")
                            .setMessage("Are you sure you want to delete this team?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CitrusDb.getInstance().deleteTeam(teamNumber);
                                    refresh();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
