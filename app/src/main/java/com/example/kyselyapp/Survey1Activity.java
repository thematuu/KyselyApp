package com.example.kyselyapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class Survey1Activity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private DatabaseHelper databaseHelper;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] emojiCodes = {0x1F641, 0x1F614, 0x1F610, 0x1F60A, 0x1F601};
    private TextToSpeech tts;

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
        setContentView(R.layout.activity_survey1);

        questionContainer = findViewById(R.id.questionContainer);
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        databaseHelper = new DatabaseHelper(this, DatabaseHelper.TABLE_SURVEY1_QUESTIONS);
        questions = databaseHelper.getAllQuestions();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // TTS is ready to use.
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        }, "com.google.android.tts");


        for (Question question : questions) {
            LinearLayout questionLayout = new LinearLayout(this);
            questionLayout.setOrientation(LinearLayout.VERTICAL);

            TextView questionTextView = new TextView(this);
            questionTextView.setText(question.getText());
            questionTextView.setTextSize(18);

            RadioGroup emojiRadioGroup = new RadioGroup(this);
            emojiRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < emojiCodes.length; i++) {
                RadioButton emojiRadioButton = new RadioButton(this);
                emojiRadioButton.setText(String.valueOf(Character.toChars(emojiCodes[i])));
                emojiRadioGroup.addView(emojiRadioButton);
            }

            Button speakButton = new Button(this);
            speakButton.setText("Speak");
            speakButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tts.speak(question.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });

            emojiRadioGroup.setId(question.getId());
            questionLayout.addView(questionTextView);
            questionLayout.addView(emojiRadioGroup);
            questionLayout.addView(speakButton);
            questionContainer.addView(questionLayout);
        }    showCurrentQuestion();

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
                RadioGroup currentRadioGroup = (RadioGroup) questionContainer.getChildAt(currentQuestionIndex).findViewById(questions.get(currentQuestionIndex).getId());
                if (currentRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Survey1Activity.this, "Please select answer before proceeding.", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentQuestionIndex < questions.size() - 1) {
                        currentQuestionIndex++;
                        showCurrentQuestion();
                    } else {
                        // Submit answers when on the last question
                        for (int i = 0; i < questionContainer.getChildCount(); i++) {
                            RadioGroup emojiRadioGroup = questionContainer.getChildAt(i).findViewById(questions.get(i).getId());
                            int questionId = emojiRadioGroup.getId(); // Fetch the correct question ID
                            int checkedRadioButtonId = emojiRadioGroup.getCheckedRadioButtonId();
                            if (checkedRadioButtonId != -1) {
                                RadioButton checkedRadioButton = emojiRadioGroup.findViewById(checkedRadioButtonId);
                                String answer = checkedRadioButton.getText().toString();
                                databaseHelper.saveAnswer(questionId, answer, DatabaseHelper.TABLE_SURVEY1_ANSWERS);
                            }
                            Intent intent = new Intent(Survey1Activity.this, ThankYouActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
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
