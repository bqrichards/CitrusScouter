package com.ftc5466.citrusscouter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class StatisticsActivity extends Activity {
    private int maxScore;
    private CustomDataPoint[] dataPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        GraphView graphView = findViewById(R.id.graph);
        grabTeamScores();
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>((DataPoint[]) dataPoints);
        graphView.addSeries(series);
        graphView.setTitle("Top 5 Teams");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Score");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Teams");
        graphView.getGridLabelRenderer().setLabelFormatter(new SlightlyCustomLabelFormatter());
        graphView.getGridLabelRenderer().setNumHorizontalLabels(30); // Arbitrarily high number

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);
        series.setValuesOnTopSize(45);
        series.setSpacing(35);
        series.setDataWidth(1);

        final Random rnd = new Random();
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            }
        });

        // set manual X bounds
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(dataPoints.length);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(maxScore+5);
    }

    private void grabTeamScores() {
        LinkedList<Team> teams = CitrusDb.getInstance().getTeams();
        if (teams == null || teams.isEmpty()) {
            dataPoints = new CustomDataPoint[0];
            return;
        }

        // Sort teams by score
        Collections.sort(teams, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                return Double.compare(o2.getTotalScore(), o1.getTotalScore());
            }
        });

        int numOfElements = Math.min(5, teams.size()); // Limit to top 5

        dataPoints = new CustomDataPoint[numOfElements];
        for (int i = 0; i < numOfElements; i++) {
            int score = teams.get(i).getTotalScore();
            if (score > maxScore) {
                maxScore = score;
            }
            dataPoints[i] = new CustomDataPoint(i, score, teams.get(i).getTeamNumber());
        }
    }

    private class CustomDataPoint extends DataPoint {
        int teamNumber;

        public CustomDataPoint(double x, double y, int teamNumber) {
            super(x, y);
            this.teamNumber = teamNumber;
        }
    }

    private class SlightlyCustomLabelFormatter extends DefaultLabelFormatter {
        @Override
        public String formatLabel(double value, boolean isValueX) {
            String fromSuper = super.formatLabel(value, isValueX);

            if (!isValueX) {
                return fromSuper;
            }

            // If value is a whole number and in our array, use the team number from our array
            // instead of the actual value
            if (value == (int)value && value < dataPoints.length) {
                MainActivity.log(fromSuper + " is whole and we haven't looked at it yet");
                return String.valueOf(dataPoints[(int)value].teamNumber);
            } else {
                return "";
            }
        }
    }
}
