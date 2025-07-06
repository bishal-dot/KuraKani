package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.Controlller.SignUpController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    TextView btnGoBack;
    TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    MaterialButton btnSignUp;
    TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    CheckBox chkTerms;
    SignUpController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        controller = new SignUpController(this);

//        goBack.setOnClickListener(v -> {
//            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//            finish();
//        });

        findViewById();
        registerEvents();

//        signUp.setOnClickListener(v -> {
//            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        });
    }

    private void findViewById(){
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnGoBack = findViewById(R.id.goback);
        btnSignUp = findViewById(R.id.btnSignup);
        chkTerms = findViewById(R.id.chkTerms);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
    }

    private void registerEvents(){
        btnGoBack.setOnClickListener(v -> finish());

        btnSignUp.setOnClickListener(v -> {
            controller.validateFields();
        });
    }

    public String getName(){
        return etName.getText().toString();
    }
    public String getEmail() {
        return etEmail.getText().toString();
    }
    public String getPassword(){
        return etPassword.getText().toString();
    }
    public String getConfirmPassword(){
        return etConfirmPassword.getText().toString();
    }
    public void setNameError(String message) {
        tilName.setError(message);
    }
    public void setEmailError(String message) {
        tilEmail.setError(message);
    }
    public void setPasswordError(String message) {
        tilPassword.setError(message);
    }
    public void setConfirmPasswordError(String message){
        tilConfirmPassword.setError(message);
    }
    public  boolean isTermsChecked(){
        return chkTerms.isChecked();
    }

    public void showError(String message){
        Snackbar.make(btnSignUp.getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    public void showProfileSetup(){
        startActivity(new Intent(SignupActivity.this, com.example.kurakani.views.ProfileSetupActivity.class));
        finish();
    }
}
