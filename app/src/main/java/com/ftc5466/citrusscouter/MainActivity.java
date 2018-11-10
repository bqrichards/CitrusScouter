package com.ftc5466.citrusscouter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "LE_EPIC_LOGGING_TAG";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private final ViewTeamsFragment teamsFragment = new ViewTeamsFragment();
    private final String TEAMS_TITLE = "Teams";

    private final ViewMatchlistFragment matchlistFragment = new ViewMatchlistFragment();
    private final String MATCHLIST_TITLE = "Matchlist";

    private final int CHANGE_TEAM_INFO_REQUEST = 0;
    private final int TAB_START_INDEX = 1; // Used for debugging, swiping to new tab every time is annoying

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(TEAMS_TITLE));
        tabLayout.addTab(tabLayout.newTab().setText(MATCHLIST_TITLE));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == null) {
                    throw new IllegalArgumentException("Tab doesn't have a title!");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddTeamActivity.class);
                startActivityForResult(i, CHANGE_TEAM_INFO_REQUEST);
            }
        });

        mViewPager.setCurrentItem(TAB_START_INDEX, true);

        CitrusDb.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_TEAM_INFO_REQUEST) {
            // Tell view fragment to refresh
            teamsFragment.refresh();
        }
    }

    public static void log(Object o) {
        Log.e(LOG_TAG, o.toString());
    }

    public void editChild(View view) {
        teamsFragment.editChild(view);
    }

    public void deleteChild(View view) {
        teamsFragment.deleteChild(view);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return teamsFragment;
                case 1:
                    return matchlistFragment;
                default:
                    throw new IllegalArgumentException("Trying to get tab that doesn't exist");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
