package com.ftc5466.citrusscouter;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Refresh the View Team List
     */
    public void refresh() {
        adapter.refresh();
    }

    private class ViewTeamDataAdapter extends BaseExpandableListAdapter {
        private ArrayList<Team> groups;

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
            mainView.setText((String)getGroup(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_child_item, parent, false);
            }

            TextView mainView = convertView.findViewById(R.id.list_child_item_text);
            mainView.setText((getChild(groupPosition, childPosition)).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
