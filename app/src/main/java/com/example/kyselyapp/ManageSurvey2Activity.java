package com.example.kyselyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ManageSurvey2Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionAdapter questionAdapter;
    private DatabaseHelper databaseHelper;

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_survey2); // Update the layout file name

        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY2_QUESTIONS); // Update the table reference

        questions = databaseHelper.getAllQuestions();

        recyclerView = findViewById(R.id.recyclerView);
        questionAdapter = new QuestionAdapter(questions);
        recyclerView.setAdapter(questionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addQuestionButton = findViewById(R.id.addQuestionButton);
        Button editQuestionButton = findViewById(R.id.editQuestionButton);
        Button deleteQuestionButton = findViewById(R.id.deleteQuestionButton);
        Button exportButton = findViewById(R.id.exportButton);

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageSurvey2Activity.this, AddQuestionActivity.class);
                intent.putExtra("questionsTableName", DatabaseHelper.TABLE_SURVEY2_QUESTIONS);
                startActivityForResult(intent, 1); // Add request code

            }
        });


        editQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedPosition = questionAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    Intent intent = new Intent(ManageSurvey2Activity.this, EditQuestionActivity.class);
                    intent.putExtra("question", questions.get(selectedPosition).getText());
                    intent.putExtra("questionId", questions.get(selectedPosition).getId()); // Updated this line
                    intent.putExtra("questionsTableName", DatabaseHelper.TABLE_SURVEY2_QUESTIONS);
                    startActivityForResult(intent, 2); // Updated this line
                } else {
                    Toast.makeText(ManageSurvey2Activity.this, "Please select a question to edit", Toast.LENGTH_SHORT).show();
                }
            }
        });




        deleteQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedPosition = questionAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ManageSurvey2Activity.this);
                    builder.setTitle("Delete Question")
                            .setMessage("Are you sure you want to delete this question and its answers?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseHelper.deleteQuestion(questions.get(selectedPosition).getText());
                                    questions.remove(selectedPosition);
                                    questionAdapter.notifyItemRemoved(selectedPosition);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    androidx.appcompat.app.AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(ManageSurvey2Activity.this, "Please select a question to delete", Toast.LENGTH_SHORT).show();
                }


            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportAnswersToCSV(DatabaseHelper.TABLE_SURVEY2_ANSWERS, "Survey2_Answers.csv"); // Update the table reference and file name
            }
        });

        Button deleteSurvey2AnswersButton = findViewById(R.id.delete_survey2_answers_button);
        deleteSurvey2AnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageSurvey2Activity.this);
                builder.setTitle("Delete All Answers")
                        .setMessage("Are you sure you want to delete all answers for this survey?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseHelper databaseHelper = new DatabaseHelper(ManageSurvey2Activity.this, DatabaseHelper.TABLE_SURVEY2_QUESTIONS);
                                databaseHelper.deleteAllAnswers(DatabaseHelper.TABLE_SURVEY2_ANSWERS);
                                // Refresh the activity if needed
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        Button showAnswersButton = findViewById(R.id.showAnswersButton);
        showAnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedPosition = questionAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    Intent intent = new Intent(ManageSurvey2Activity.this, ShowAnswersForQuestionActivity.class);
                    intent.putExtra("questionId", questions.get(selectedPosition).getId());
                    intent.putExtra("answersTableName", DatabaseHelper.TABLE_SURVEY2_ANSWERS);
                    startActivity(intent);
                } else {
                    Toast.makeText(ManageSurvey2Activity.this, "Please select a question to see answers", Toast.LENGTH_SHORT).show();
                }
            }
        });

                }


    private void exportAnswersToCSV(String tableName, String fileName) {
        DatabaseHelper dbHelper = new DatabaseHelper(this, tableName);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        if (cursor != null) {
            try {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, fileName);
                int fileCounter = 1;

                while (file.exists()) {
                    file = new File(path, fileName.replace(".csv", "_" + fileCounter + ".csv"));
                    fileCounter++;
                }

                FileWriter fileWriter = new FileWriter(file);

                // Write column names
                fileWriter.write("ID,Question ID,Question,Answer\n");

                // Write data
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex("id");
                    int questionIdIndex = cursor.getColumnIndex("question_id");
                    int answerIndex = cursor.getColumnIndex("answer");

                    if (idIndex != -1 && questionIdIndex != -1 && answerIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        int questionId = cursor.getInt(questionIdIndex);
                        String answer = cursor.getString(answerIndex);

                        // Get the question text based on the questionId
                        String questionText = dbHelper.getQuestionText(questionId, DatabaseHelper.TABLE_SURVEY1_QUESTIONS);

                        fileWriter.write(id + "," + questionId + "," + questionText + "," + answer + "\n");
                    }
                }

                fileWriter.close();
                cursor.close();
                Toast.makeText(this, "Answers exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error exporting answers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No answers to export", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            refreshQuestions();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshQuestions();
    }

    private void refreshQuestions() {
        questions = databaseHelper.getAllQuestions();
        questionAdapter.updateQuestions(questions);
    }

}