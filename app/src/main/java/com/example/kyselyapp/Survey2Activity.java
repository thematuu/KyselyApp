package com.example.kyselyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Survey2Activity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private List<Question> questions;
    private DatabaseHelper databaseHelper;
    private List<EditText> answerEditTexts;
    private int currentQuestionIndex = 0;
    private void showCurrentQuestion() {
        Button nextButton = findViewById(R.id.nextButton);
        Button previousButton = findViewById(R.id.previousButton);

        for (int i = 0; i < questionContainer.getChildCount(); i++) {
            if (i == currentQuestionIndex) {
                questionContainer.getChildAt(i).setVisibility(View.VISIBLE);
                if (i == questions.size() - 1) {
                    nextButton.setText("Submit");
                } else {
                    nextButton.setText("Next");
                }
                if (i == 0) {
                    previousButton.setVisibility(View.GONE);
                } else {
                    previousButton.setVisibility(View.VISIBLE);
                }
            } else {
                questionContainer.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey2);

        questionContainer = findViewById(R.id.questionContainer);
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY2_QUESTIONS);
        questions = databaseHelper.getAllQuestions();
        answerEditTexts = new ArrayList<>();

        for (Question question : questions) {
            LinearLayout questionLayout = new LinearLayout(this);
            questionLayout.setOrientation(LinearLayout.VERTICAL);

            TextView questionTextView = new TextView(this);
            questionTextView.setText(question.getText());

            EditText answerEditText = new EditText(this);
            answerEditText.setHint("Write your answer here");

            questionLayout.addView(questionTextView);
            questionLayout.addView(answerEditText);

            questionContainer.addView(questionLayout);

            answerEditTexts.add(answerEditText);
        }

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
                String currentAnswer = answerEditTexts.get(currentQuestionIndex).getText().toString();

                if (currentAnswer.trim().isEmpty()) {
                    Toast.makeText(Survey2Activity.this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentQuestionIndex < questions.size() - 1) {
                        currentQuestionIndex++;
                        showCurrentQuestion();
                    } else {
                        // Submit answers when on the last question
                        for (int i = 0; i < questions.size(); i++) {
                            int questionId = questions.get(i).getId(); // Get the question ID from the Question object
                            String answer = answerEditTexts.get(i).getText().toString();
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
