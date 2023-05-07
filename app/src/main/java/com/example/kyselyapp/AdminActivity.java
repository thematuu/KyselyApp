package com.example.kyselyapp;

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

                if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                    DatabaseHelper dbHelper = new DatabaseHelper(AdminActivity.this, null);
                    dbHelper.updateAdminCredentials(newUsername, newPassword);
                    Toast.makeText(AdminActivity.this, "Credentials updated", Toast.LENGTH_SHORT).show();

                    // Clear the input fields after updating the credentials
                    changeUsernameEditText.setText("");
                    changePasswordEditText.setText("");
                } else {
                    Toast.makeText(AdminActivity.this, "Both fields must be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
