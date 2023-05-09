package com.example.kyselyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Survey2Activity extends AppCompatActivity {

    private List<Question> questions;
    private DatabaseHelper databaseHelper;
    private EditText answerEditText;
    private TextView questionTextView;
    private ProgressBar progressBar;
    private int currentQuestionIndex = 0;

    private List<String> answers;

    private void showCurrentQuestion() {
        getSupportActionBar().hide();
        questionTextView.setText(questions.get(currentQuestionIndex).getText());
        progressBar.setProgress((int) (((float) currentQuestionIndex / (questions.size() - 1)) * 100));

        answerEditText.setText(answers.get(currentQuestionIndex));

        Button nextButton = findViewById(R.id.nextButton);
        Button previousButton = findViewById(R.id.previousButton);

        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }
        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
    }



    private void updateButtonVisibility() {
        Button nextButton = findViewById(R.id.nextButton);
        Button previousButton = findViewById(R.id.previousButton);

        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }
        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey2);
        
        answerEditText = findViewById(R.id.answerEditText);
        questionTextView = findViewById(R.id.question_text_view);
        progressBar = findViewById(R.id.progress_bar);
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        answerEditText.requestFocus();

        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY2_QUESTIONS);
        questions = databaseHelper.getAllQuestions();

        // Initialize the answers list after initializing the questions list
        answers = new ArrayList<>(Collections.nCopies(questions.size(), ""));

        showCurrentQuestion();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    showCurrentQuestion();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentAnswer = answerEditText.getText().toString();

                if (currentAnswer.trim().isEmpty()) {
                    Toast.makeText(Survey2Activity.this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    answers.set(currentQuestionIndex, currentAnswer);

                    if (currentQuestionIndex < questions.size() - 1) {
                        currentQuestionIndex++;
                        showCurrentQuestion();
                    } else {
                        // Submit answers when on the last question
                        for (int i = 0; i < questions.size(); i++) {
                            int questionId = questions.get(i).getId(); // Get the question ID from the Question object
                            String answer = answers.get(i);
                            databaseHelper.saveAnswer(questionId, answer, DatabaseHelper.TABLE_SURVEY2_ANSWERS);
                        }
                        Intent intent = new Intent(Survey2Activity.this, ThankYouActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }
}