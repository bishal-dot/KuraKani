package com.example.kurakani.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kurakani.R;
import com.example.kurakani.model.ApiResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.model.ChangePasswordRequest;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private MaterialButton updateButton, backButton;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        currentPasswordInput = findViewById(R.id.current_password);
        newPasswordInput = findViewById(R.id.new_password);
        confirmPasswordInput = findViewById(R.id.confirm_password);
        updateButton = findViewById(R.id.update_button);
        backButton = findViewById(R.id.back_button);

        api = RetrofitClient.getInstance(this).getApi();

        // Password strength listener
        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        updateButton.setOnClickListener(v -> handleChangePassword());
        backButton.setOnClickListener(v -> finish());
    }

    private void updatePasswordStrength(String password) {
        View strengthLayout = findViewById(R.id.password_strength);
        TextView strengthTextView = findViewById(R.id.strength_text);

        strengthLayout.setVisibility(password.isEmpty() ? View.GONE : View.VISIBLE);
        strengthTextView.setVisibility(password.isEmpty() ? View.GONE : View.VISIBLE);

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-zA-Z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/].*")) strength++;

        int[] bars = {R.id.strength_indicator_1, R.id.strength_indicator_2, R.id.strength_indicator_3, R.id.strength_indicator_4};
        int colorWeak = getResources().getColor(R.color.password_weak);
        int colorMedium = getResources().getColor(R.color.password_medium);
        int colorStrong = getResources().getColor(R.color.password_strong);
        int colorDefault = getResources().getColor(R.color.bg_password_strength_weak);

        for (int i = 0; i < bars.length; i++) {
            View bar = findViewById(bars[i]);
            if (i < strength) {
                if (strength <= 2) bar.setBackgroundColor(colorWeak);
                else if (strength == 3) bar.setBackgroundColor(colorMedium);
                else bar.setBackgroundColor(colorStrong);
            } else {
                bar.setBackgroundColor(colorDefault);
            }
        }

        String strengthText;
        if (strength <= 1) strengthText = "Very Weak";
        else if (strength == 2) strengthText = "Weak";
        else if (strength == 3) strengthText = "Medium";
        else strengthText = "Strong";

        strengthTextView.setText(strengthText);
    }

    private void handleChangePassword() {
        String current = currentPasswordInput.getText().toString().trim();
        String newPass = newPasswordInput.getText().toString().trim();
        String confirm = confirmPasswordInput.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(current, newPass, confirm);
        updateButton.setEnabled(false);

        api.changePassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                updateButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().error) {
                        Toast.makeText(ChangePasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                updateButton.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
