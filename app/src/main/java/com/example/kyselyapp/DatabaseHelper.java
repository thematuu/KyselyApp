package com.example.kyselyapp;

import static android.provider.Settings.System.getString;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "survey_app.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SURVEY1_QUESTIONS = "survey1_questions";
    public static final String TABLE_SURVEY2_QUESTIONS = "survey2_questions";
    public static final String TABLE_SURVEY1_ANSWERS = "survey1_answers";
    public static final String TABLE_SURVEY2_ANSWERS = "survey2_answers";

    // Add the column name for the question field
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ID = "id";
    // Add a member variable for the questions table name

    // Add a constant for the admin table
    public static final String TABLE_ADMIN_CREDENTIALS = "admin_credentials";

    // Add column names for the admin credentials
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    private String questionsTableName;

    private CryptoHelper cryptoHelper;

    public DatabaseHelper(Context context, String questionsTableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.questionsTableName = questionsTableName;
        this.cryptoHelper = new CryptoHelper(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSurvey1QuestionsTable = "CREATE TABLE " + TABLE_SURVEY1_QUESTIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT)";
        String createSurvey2QuestionsTable = "CREATE TABLE " + TABLE_SURVEY2_QUESTIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT)";
        String createSurvey1AnswersTable = "CREATE TABLE " + TABLE_SURVEY1_ANSWERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question_id INTEGER, answer TEXT)";
        String createSurvey2AnswersTable = "CREATE TABLE " + TABLE_SURVEY2_ANSWERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question_id INTEGER, answer TEXT)";

        db.execSQL(createSurvey1QuestionsTable);
        db.execSQL(createSurvey2QuestionsTable);
        db.execSQL(createSurvey1AnswersTable);
        db.execSQL(createSurvey2AnswersTable);

        // Create the admin_credentials table
        String createAdminCredentialsTable = "CREATE TABLE " + TABLE_ADMIN_CREDENTIALS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)";
        db.execSQL(createAdminCredentialsTable);

        // Insert the default admin credentials
        ContentValues adminCredentials = new ContentValues();
        String encryptedUsername = cryptoHelper.encrypt("admin");
        String encryptedPassword = cryptoHelper.encrypt("password");
        adminCredentials.put(KEY_USERNAME, encryptedUsername);
        adminCredentials.put(KEY_PASSWORD, encryptedPassword);
        db.insert(TABLE_ADMIN_CREDENTIALS, null, adminCredentials);

        // Insert example questions for survey 1 and survey 2
        String[] survey1ExampleQuestions = {
                "Tämä on esimerkki kysymys?",
                "Tämä on esimerkki kysymys2?",
                "Tämä on esimerkki kysymys3?"
        };
        String[] survey2ExampleQuestions = {
                "Tämä on esimerkki kysymys?",
                "Tämä on esimerkki kysymys2?",
                "Tämä on esimerkki kysymys3?"
        };
        insertExampleQuestions(db, TABLE_SURVEY1_QUESTIONS, survey1ExampleQuestions);
        insertExampleQuestions(db, TABLE_SURVEY2_QUESTIONS, survey2ExampleQuestions);
    }

    private void insertExampleQuestions(SQLiteDatabase db, String tableName, String[] questions) {
        for (String question : questions) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_QUESTION, question);
            db.insert(tableName, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement onUpgrade method to handle database schema changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY1_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY2_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY1_ANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY2_ANSWERS);
        onCreate(db);
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(questionsTableName, new String[]{KEY_ID, KEY_QUESTION}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int questionIndex = cursor.getColumnIndex(KEY_QUESTION);
                if (idIndex != -1 && questionIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String question = cursor.getString(questionIndex);
                    questions.add(new Question(id, question));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questions;
    }

    public long addQuestion(String question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_QUESTION, question);
        long newRowId = db.insert(questionsTableName, null, contentValues);
        db.close();
        return newRowId;
    }

    public boolean updateQuestion(int id, String updatedQuestion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_QUESTION, updatedQuestion);
        int rowsAffected = db.update(questionsTableName, contentValues, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public void deleteQuestion(String question) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(questionsTableName, KEY_QUESTION + "=?", new String[]{question});
    }
    public void deleteAllAnswers(String answersTableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(answersTableName, null, null);
        db.close();
    }

    public void saveAnswer(int questionId, String answer, String answersTableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("question_id", questionId);
        contentValues.put("answer", answer);
        db.insert(answersTableName, null, contentValues);
        db.close();
    }

    public int getQuestionId(String questionText) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + questionsTableName + " WHERE question=?";
        Cursor cursor = db.rawQuery(query, new String[]{questionText});

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");

            if (idIndex != -1) {
                int questionId = cursor.getInt(idIndex);
                cursor.close();
                return questionId;
            }
        }
        return -1;
    }

    public Map<String, Integer> getAnswerCounts(String answersTableName, int questionId) {
        Map<String, Integer> answerCounts = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(answersTableName, new String[]{"answer"}, "question_id=?", new String[]{String.valueOf(questionId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int columnIndex = cursor.getColumnIndex("answer");
                if (columnIndex != -1) {
                    String answer = cursor.getString(columnIndex);
                    answerCounts.put(answer, answerCounts.getOrDefault(answer, 0) + 1);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return answerCounts;
    }

    public List<String> getAnswersForQuestion(int questionId, String answersTableName) {
        List<String> answers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "question_id=?";
        String[] selectionArgs = {String.valueOf(questionId)};

        Cursor cursor = db.query(answersTableName, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int answerColumnIndex = cursor.getColumnIndex("answer");
                if (answerColumnIndex >= 0) {
                    String answer = cursor.getString(answerColumnIndex);
                    answers.add(answer);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return answers;
    }

    // Add a method to update the admin credentials
    public void updateAdminCredentials(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, cryptoHelper.encrypt(username));
        contentValues.put(KEY_PASSWORD, cryptoHelper.encrypt(password));
        db.update(TABLE_ADMIN_CREDENTIALS, contentValues, "id=?", new String[]{"1"});
        db.close();
    }

    // Add a method to get the admin credentials
    public Map<String, String> getAdminCredentials() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ADMIN_CREDENTIALS, new String[]{KEY_USERNAME, KEY_PASSWORD}, "id=?", new String[]{"1"}, null, null, null);
        Map<String, String> adminCredentials = new HashMap<>();

        if (cursor.moveToFirst()) {
            int usernameColumnIndex = cursor.getColumnIndex(KEY_USERNAME);
            int passwordColumnIndex = cursor.getColumnIndex(KEY_PASSWORD);

            if (usernameColumnIndex >= 0 && passwordColumnIndex >= 0) {
                String encryptedUsername = cursor.getString(usernameColumnIndex);
                String encryptedPassword = cursor.getString(passwordColumnIndex);
                adminCredentials.put(KEY_USERNAME, cryptoHelper.decrypt(encryptedUsername));
                adminCredentials.put(KEY_PASSWORD, cryptoHelper.decrypt(encryptedPassword));
            }
        }

        cursor.close();
        return adminCredentials;
    }

    public String getQuestionText(int questionId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"id", "question"}, "id=?", new String[]{String.valueOf(questionId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int questionColumnIndex = cursor.getColumnIndex("question");
            if (questionColumnIndex != -1) {
                String questionText = cursor.getString(questionColumnIndex);
                cursor.close();
                return questionText;
            }
        }
        return null;
    }


    public void exportAllDataToCSV(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] tableNames = new String[]{TABLE_SURVEY1_QUESTIONS, TABLE_SURVEY2_QUESTIONS, TABLE_SURVEY1_ANSWERS, TABLE_SURVEY2_ANSWERS};

        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            int fileNumber = 0;
            File csvFile;
            do {
                csvFile = new File(downloadsDir, "database_export" + (fileNumber > 0 ? "(" + fileNumber + ")" : "") + ".csv");
                fileNumber++;
            } while (csvFile.exists());
            FileWriter fileWriter = new FileWriter(csvFile);

            for (String tableName : tableNames) {
                Cursor cursor = db.query(tableName, null, null, null, null, null, null);

                // write table name
                fileWriter.append(tableName);
                fileWriter.append("\n");

                // write column names
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    fileWriter.append(columnNames[i]);
                    if(i < columnNames.length - 1){
                        fileWriter.append(",");
                    }
                }
                fileWriter.append("\n");

                // write rows
                while (cursor.moveToNext()) {
                    int numColumns = cursor.getColumnCount();
                    for (int i = 0; i < numColumns; i++) {
                        fileWriter.append(cursor.getString(i));
                        if(i < numColumns - 1){
                            fileWriter.append(",");
                        }
                    }
                    fileWriter.append("\n");
                }
                cursor.close();
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(context, context.getString(R.string.backup_data_saved) + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.backup_data_save_failed), Toast.LENGTH_SHORT).show();
        }
    }





    public void importAllDataFromCSV(Context context, Uri csvFileUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (csvFileUri == null) {
            Log.d("Import", "No file selected");
            Toast.makeText(context, context.getString(R.string.no_file_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(csvFileUri);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            db.beginTransaction();

            String tableName = "";
            String[] columnNames = null;
            while ((line = buffer.readLine()) != null) {
                // If line has one field, it's a table name
                if (line.split(",", -1).length == 1) {
                    tableName = line;
                    // Delete all rows from the current table before importing new data
                    db.delete(tableName, null, null);
                    // The next line will be column names
                    if((line = buffer.readLine()) != null) {
                        columnNames = line.split(",", -1);
                    }
                } else {
                    // If line has more than one field, it's data row
                    String[] str = line.split(",", -1);
                    ContentValues contentValues = new ContentValues();

                    for (int i = 0; i < str.length; i++) {
                        contentValues.put(columnNames[i], str[i]);
                    }

                    db.insert(tableName, null, contentValues);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            buffer.close();
            Toast.makeText(context, context.getString(R.string.data_imported_successfully), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.data_import_failed), Toast.LENGTH_SHORT).show();
        }
    }








}
