package com.example.kyselyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button manageSurvey1Button = findViewById(R.id.manageSurvey1Button);
        Button manageSurvey2Button = findViewById(R.id.manageSurvey2Button);
        Button logoutButton = findViewById(R.id.logoutButton);
        EditText changeUsernameEditText = findViewById(R.id.changeUsernameEditText);
        EditText changePasswordEditText = findViewById(R.id.changePasswordEditText);
        Button changeCredentialsButton = findViewById(R.id.changeCredentialsButton);

        // Set the password field to hidden
        changePasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        manageSurvey1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ManageSurvey1Activity.class);
                startActivity(intent);
            }
        });

        manageSurvey2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ManageSurvey2Activity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        changeCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = changeUsernameEditText.getText().toString();
                String newPassword = changePasswordEditText.getText().toString();
                EditText retypePasswordEditText = findViewById(R.id.retypePasswordEditText);
                String retypedPassword = retypePasswordEditText.getText().toString();

                if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                    if (newPassword.equals(retypedPassword)) {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle(R.string.confirmation)
                                .setMessage(R.string.Are_you_sure_you_want_to_change_the_credentials)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseHelper dbHelper = new DatabaseHelper(AdminActivity.this, null);
                                        dbHelper.updateAdminCredentials(newUsername, newPassword);

                                        new AlertDialog.Builder(AdminActivity.this)
                                                .setTitle(R.string.success)
                                                .setMessage(R.string.credentials_updated)
                                                .setPositiveButton(android.R.string.ok, null)
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .show();

                                        // Clear the input fields after updating the credentials
                                        changeUsernameEditText.setText("");
                                        changePasswordEditText.setText("");
                                        retypePasswordEditText.setText("");
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle(R.string.error)
                                .setMessage(R.string.passwords_do_not_match)
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } else {
                    new AlertDialog.Builder(AdminActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.all_fields_must_be_filled)
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });



    }
}
