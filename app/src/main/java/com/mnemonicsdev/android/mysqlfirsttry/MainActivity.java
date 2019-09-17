package com.mnemonicsdev.android.mysqlfirsttry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;

    private Button insertUser;
    private Button getUserBySurname;
    private ProgressBar connectionProgress;
    private TextView receivedDataText;
    private EditText userName;
    private EditText userSurname;
    private EditText userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        observe();

    }

    private void observe() {
        viewModel.getIsDataBaseConnected().observe(this, isConnected -> {
            if (isConnected) {
                connectionProgress.setVisibility(View.GONE);
                insertUser.setEnabled(true);
                getUserBySurname.setEnabled(true);
                userName.setEnabled(true);
                userSurname.setEnabled(true);
                userAge.setEnabled(true);
            } else {
                connectionProgress.setVisibility(View.VISIBLE);
                insertUser.setEnabled(false);
                getUserBySurname.setEnabled(false);
                userName.setEnabled(false);
                userSurname.setEnabled(false);
                userAge.setEnabled(false);
            }
        });
        viewModel.getReceivedUser().observe(this, receivedUser -> {
            connectionProgress.setVisibility(View.GONE);
            receivedDataText.setText(receivedUser.toString());
        });
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        insertUser = findViewById(R.id.insert_user_button);
        getUserBySurname = findViewById(R.id.get_user_button);
        connectionProgress = findViewById(R.id.connection_progress);
        //connectionProgress.setVisibility(View.VISIBLE);
        receivedDataText = findViewById(R.id.received_data_text);

        userName = findViewById(R.id.user_name_edit);
        userSurname = findViewById(R.id.user_surname_edit);
        userAge = findViewById(R.id.user_age_edit);
    }

    public void InsertUserOnClick(View view) {

        String name = userName.getText().toString();
        String surName = userSurname.getText().toString();
        int age = Integer.valueOf(userAge.getText().toString());

        if (name.isEmpty() | surName.isEmpty() | userAge.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.InsertUserToDataBase(new User(name, surName, age));
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
    }

    public void GetUserBySurname(View view) {
        String surname = userSurname.getText().toString();
        if (surname.isEmpty()) Toast.makeText(this, "Fill Surname", Toast.LENGTH_SHORT).show();
        else {
            connectionProgress.setVisibility(View.VISIBLE);
            viewModel.GetUserFromDataBaseBySurname(surname);
        }
    }
}
