package com.example.kurakani.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.kurakani.R;
import com.example.kurakani.model.ApiResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText newPassword, confirmPassword;
    private MaterialButton resetButton;
    private TextView backButton, strengthText;
    private View[] strengthBars;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        resetButton = findViewById(R.id.reset_button);
        backButton = findViewById(R.id.back_button);
        strengthText = findViewById(R.id.strength_text);

        strengthBars = new View[] {
                findViewById(R.id.strength_indicator_1),
                findViewById(R.id.strength_indicator_2),
                findViewById(R.id.strength_indicator_3),
                findViewById(R.id.strength_indicator_4)
        };

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        resetButton.setOnClickListener(v -> resetPassword(this));

        backButton.setOnClickListener(v -> finish()); // Back button
    }

    private void updatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()].*")) strength++;

        for (int i = 0; i < strengthBars.length; i++) {
            strengthBars[i].setBackgroundColor(i < strength ?
                    ContextCompat.getColor(this, R.color.password_strong) :
                    ContextCompat.getColor(this, R.color.gray));
        }

        String[] strengthLabels = {"Weak", "Fair", "Good", "Strong"};
        strengthText.setText(strength > 0 ? strengthLabels[strength - 1] : "Too short");
    }

    private void resetPassword(Context context) {
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPass)) {
            newPassword.setError("Enter new password");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        apiService.resetPassword(email, otp, newPass, confirmPass).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null && !response.body().isError()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (response.errorBody() != null) {
                    Toast.makeText(ResetPasswordActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
