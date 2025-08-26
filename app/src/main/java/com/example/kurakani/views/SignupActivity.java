package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.Controlller.SignUpController;
import com.example.kurakani.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private CheckBox chkTerms;
    private MaterialButton btnSignup;
    private SignUpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        chkTerms = findViewById(R.id.chkTerms);
        btnSignup = findViewById(R.id.btnSignup);

        controller = new SignUpController(this);

        btnSignup.setOnClickListener(v -> controller.validateFields());

        findViewById(R.id.goback).setOnClickListener(v -> finish());
    }

    public String getName() { return etName.getText().toString().trim(); }
    public String getEmail() { return etEmail.getText().toString().trim(); }
    public String getPassword() { return etPassword.getText().toString(); }
    public String getConfirmPassword() { return etConfirmPassword.getText().toString(); }
    public boolean isTermsChecked() { return chkTerms.isChecked(); }

    public void showMessage(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    public void showError(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    public void setNameError(String msg) { etName.setError(msg); }
    public void setEmailError(String msg) { etEmail.setError(msg); }
    public void setPasswordError(String msg) { etPassword.setError(msg); }
    public void setConfirmPasswordError(String msg) { etConfirmPassword.setError(msg); }

    public void clearErrors() {
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    public void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
