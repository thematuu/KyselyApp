package com.example.kyselyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

public class EditQuestionActivity extends AppCompatActivity {

    private EditText questionInput;
    private Button updateButton;
    private DatabaseHelper databaseHelper;
    private String originalQuestion;
    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        String questionsTableName = getIntent().getStringExtra("questionsTableName");
        databaseHelper = new DatabaseHelper(this, questionsTableName);

        questionInput = findViewById(R.id.questionInput);
        updateButton = findViewById(R.id.updateButton);

        originalQuestion = getIntent().getStringExtra("question");
        questionId = getIntent().getIntExtra("questionId", -1);
        questionInput.setText(originalQuestion);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedQuestion = questionInput.getText().toString().trim();
                Log.d("EditQuestion", "Original question: '" + originalQuestion.trim() + "'");
                Log.d("EditQuestion", "Updated question: '" + updatedQuestion + "'");
                Log.d("EditQuestion", "Question ID: " + questionId);

                if (!updatedQuestion.isEmpty() && questionId != -1) {
                    if (!originalQuestion.trim().equals(updatedQuestion)) {
                        databaseHelper.updateQuestion(questionId, updatedQuestion);
                        Toast.makeText(EditQuestionActivity.this, R.string.question_updated_successfully, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(EditQuestionActivity.this, R.string.the_question_has_not_changed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("EditQuestion", "Issue detected");
                    Toast.makeText(EditQuestionActivity.this, R.string.please_enter_a_question, Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
