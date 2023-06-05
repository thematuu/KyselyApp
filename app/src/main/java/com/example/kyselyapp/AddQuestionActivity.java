package com.example.kyselyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText questionInput;
    private Button saveButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        // Get the table name from the intent
        String questionsTableName = getIntent().getStringExtra("questionsTableName");

        // Pass the table name when creating the DatabaseHelper instance
        databaseHelper = new DatabaseHelper(this, questionsTableName);

        questionInput = findViewById(R.id.questionInput);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionText = questionInput.getText().toString().trim();

                if (!questionText.isEmpty()) {
                    databaseHelper.addQuestion(questionText);
                    Toast.makeText(AddQuestionActivity.this, R.string.question_added_successfully, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set the result to RESULT_OK
                    finish();
                } else {
                    Toast.makeText(AddQuestionActivity.this, R.string.please_enter_a_question, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
