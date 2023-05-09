package com.example.kyselyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.TextView;

public class ShowAnswersActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private boolean showAbsoluteValues = false;
    private boolean isBarChart = false;

    // Add createBarChart() method to create a bar chart
    private BarChart createBarChart(Map<String, Integer> answerCounts) {
        BarChart barChart = new BarChart(this);

        List<BarEntry> entries = new ArrayList<>();
        List<String> answerOptions = new ArrayList<>();
        answerOptions.add("☹️");
        answerOptions.add("😔");
        answerOptions.add("😐");
        answerOptions.add("😊");
        answerOptions.add("😁");

        int index = 0;
        for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(getCustomColors());
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(answerOptions));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false); // Disable X-axis labels

        barChart.getAxisRight().setEnabled(false);
        barChart.setExtraOffsets(0, 10, 0, 5);

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        description.setEnabled(false);

        // Set a custom legend for the bar chart
        Legend legend = barChart.getLegend();
        legend.setTextSize(20f);
        legend.setWordWrapEnabled(true);
        LegendEntry[] legendEntries = new LegendEntry[answerOptions.size()];
        for (int i = 0; i < answerOptions.size(); i++) {
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.label = answerOptions.get(i);
            legendEntry.formColor = getCustomColors()[i];
            legendEntry.formSize = 20f;
            legendEntries[i] = legendEntry;
        }
        legend.setCustom(legendEntries);

        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);
        barChart.invalidate();

        return barChart;
    }



    private PieChart createPieChart(Map<String, Integer> answerCounts) {
        PieChart pieChart = new PieChart(this);

        List<PieEntry> entries = new ArrayList<>();
        List<String> answerOptions = new ArrayList<>();
        answerOptions.add("☹️");
        answerOptions.add("😔");
        answerOptions.add("😐");
        answerOptions.add("😊");
        answerOptions.add("😁");

        int index = 0;
        for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), answerOptions.get(index)));
            index++;
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(getCustomColors());
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(24f);

        PieData pieData = new PieData(pieDataSet);
        if (showAbsoluteValues) {
            pieChart.setUsePercentValues(false);
        } else {
            pieData.setValueFormatter(new PercentFormatter(pieChart));
            pieChart.setUsePercentValues(true);
        }

        pieChart.setEntryLabelTextSize(24f);

        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(24f);
        legend.setWordWrapEnabled(true);
        LegendEntry[] legendEntries = new LegendEntry[answerOptions.size()];
        for (int i = 0; i < answerOptions.size(); i++) {
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.label = answerOptions.get(i);
            legendEntry.formColor = getCustomColors()[i];
            legendEntry.formSize = 24f;
            legendEntries[i] = legendEntry;
        }
        legend.setCustom(legendEntries);

        pieChart.invalidate();

        return pieChart;
    }


    private void refreshChartDisplay() {
        LinearLayout showAnswersLayout = findViewById(R.id.show_answers_layout);
        showAnswersLayout.removeAllViews();
        List<Question> questions = databaseHelper.getAllQuestions();

        for (Question question : questions) {
            int questionId = databaseHelper.getQuestionId(question.getText());
            Map<String, Integer> answerCounts = databaseHelper.getAnswerCounts(DatabaseHelper.TABLE_SURVEY1_ANSWERS, questionId);
            Log.d("PieChartDebug", "Question ID: " + questionId);
            if (!answerCounts.isEmpty()) {
                // Inflate the pie_chart_layout
                LayoutInflater inflater = LayoutInflater.from(this);
                View chartLayout = inflater.inflate(R.layout.chart_layout, showAnswersLayout, false);

                if (isBarChart) {
                    // Create a bar chart
                    BarChart barChart = createBarChart(answerCounts);
                    ((FrameLayout) chartLayout.findViewById(R.id.chart_container)).addView(barChart);
                } else {
                    // Create a pie chart
                    PieChart pieChart = createPieChart(answerCounts);
                    ((FrameLayout) chartLayout.findViewById(R.id.chart_container)).addView(pieChart);
                }

                // Set the question text to the TextView
                TextView questionDescription = chartLayout.findViewById(R.id.question_description);
                questionDescription.setText(question.getText());

                // Add the chart to the main layout
                showAnswersLayout.addView(chartLayout);
            } else {
                // Create a TextView to display "No answers" message
                TextView noAnswersTextView = new TextView(this);
                noAnswersTextView.setText("No answers for \"" + question + "\"");
                noAnswersTextView.setTextSize(16);
                noAnswersTextView.setPadding(0, 20, 0, 20);

                // Add the TextView to the main layout
                showAnswersLayout.addView(noAnswersTextView);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers);

        Switch absoluteValueSwitch = findViewById(R.id.absolute_value_switch);
        absoluteValueSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAbsoluteValues = isChecked;
                refreshChartDisplay();
            }
        });

        Switch chartTypeSwitch = findViewById(R.id.chart_type_switch);
        chartTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBarChart = isChecked;
                if (isBarChart) {
                    absoluteValueSwitch.setVisibility(View.GONE);
                } else {
                    absoluteValueSwitch.setVisibility(View.VISIBLE);
                }
                refreshChartDisplay();
            }
        });


        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY1_QUESTIONS);
        refreshChartDisplay();
    }

    private int[] getCustomColors() {
        return new int[]{
                Color.rgb(255, 64,129), // Pink
                Color.rgb(255, 152, 0), // Orange
                Color.rgb(96, 125, 139), // Blue Grey
                Color.rgb(0, 188, 212), // Cyan
                Color.rgb(139, 195, 74) // Light Green
        };
    }
}
