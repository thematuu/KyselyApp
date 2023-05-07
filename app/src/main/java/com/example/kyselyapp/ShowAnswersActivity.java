package com.example.kyselyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.TextView;

public class ShowAnswersActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers);

        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY1_QUESTIONS);
        List<Question> questions = databaseHelper.getAllQuestions();
        LinearLayout showAnswersLayout = findViewById(R.id.show_answers_layout);

        for (Question question : questions) {
            int questionId = databaseHelper.getQuestionId(question.getText());
            Map<String, Integer> answerCounts = databaseHelper.getAnswerCounts(DatabaseHelper.TABLE_SURVEY1_ANSWERS, questionId);
            Log.d("PieChartDebug", "Question ID: " + questionId);
            if (!answerCounts.isEmpty()) {
                // Inflate the pie_chart_layout
                LayoutInflater inflater = LayoutInflater.from(this);
                View pieChartLayout = inflater.inflate(R.layout.pie_chart_layout, showAnswersLayout, false);

                // Create and configure the PieChart
                PieChart pieChart = pieChartLayout.findViewById(R.id.pie_chart);
                List<PieEntry> entries = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
                    entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }

                PieDataSet pieDataSet = new PieDataSet(entries, "");
                pieDataSet.setColors(getCustomColors());
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setExtraOffsets(5, 10, 5, 5);

                Description description = new Description();
                description.setText(question.getText());
                pieChart.setDescription(description);

                // Add the pie chart to the main layout
                showAnswersLayout.addView(pieChartLayout);
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
