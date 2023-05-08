package com.example.kyselyapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class Survey1Activity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup radioGroup;
    private Button previousButton;
    private Button nextButton;
    private Button ttsButton;
    private ProgressBar progressBar;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private TextToSpeech tts;
    private String[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey1);

        questionTextView = findViewById(R.id.question_text_view);
        radioGroup = findViewById(R.id.radio_group);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        ttsButton = findViewById(R.id.tts_button);
        progressBar = findViewById(R.id.progress_bar);

        DatabaseHelper databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY1_QUESTIONS);
        questions = databaseHelper.getAllQuestions();
        if (questions != null) {
            answers = new String[questions.size()];
            progressBar.setMax(questions.size() - 1);
        } else {
            Toast.makeText(this, "Error loading questions", Toast.LENGTH_SHORT).show();
            finish();
        }


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Language not supported
                    } else {
                        // TTS is ready to use
                    }
                } else {
                    // Initialization failed
                }
            }
        }, "com.google.android.tts");

        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(questionTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

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
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == -1) {
                    // No answer selected
                } else {
                    // Save the answer for the current question to the answers array
                    RadioButton checkedRadioButton = findViewById(checkedRadioButtonId);
                    String answer = checkedRadioButton.getText().toString();
                    answers[currentQuestionIndex] = answer;

                    if (currentQuestionIndex < questions.size() - 1) {
                        currentQuestionIndex++;
                        showCurrentQuestion();
                    } else {
                        // Save all answers to the database and proceed to ThankYouActivity
                        for (int i = 0; i < questions.size(); i++) {
                            databaseHelper.saveAnswer(questions.get(i).getId(), answers[i], DatabaseHelper.TABLE_SURVEY1_ANSWERS);
                        }
                        Intent intent = new Intent(Survey1Activity.this, ThankYouActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        showCurrentQuestion();
    }

    private void showCurrentQuestion() {
        Question question = questions.get(currentQuestionIndex);
        questionTextView.setText(question.getText());

        // Clear the checked status of all radio buttons
        radioGroup.clearCheck();

        // Update the checked status of the radio button if the answer was previously selected
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(answers[currentQuestionIndex])) {
                radioButton.setChecked(true);
                break;
            }
        }

        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }

        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }

        progressBar.setProgress(currentQuestionIndex);




    }




    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
