package com.example.kyselyapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ShowAnswersForQuestionActivity extends AppCompatActivity {

    private ListView answersListView;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers_for_question);

        int questionId = getIntent().getIntExtra("questionId", -1);
        String answersTableName = getIntent().getStringExtra("answersTableName");

        databaseHelper = new DatabaseHelper(this, answersTableName);

        List<String> answers = databaseHelper.getAnswersForQuestion(questionId, answersTableName);

        TextView questionTextView = findViewById(R.id.questionTextView);
        String questionText = databaseHelper.getQuestionText(questionId, "survey2_questions");
        questionTextView.setText(questionText);

        answersListView = findViewById(R.id.answersListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, answers);
        answersListView.setAdapter(adapter);
    }


}
