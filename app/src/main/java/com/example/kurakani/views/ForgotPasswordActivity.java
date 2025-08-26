package com.example.kurakani.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.model.ApiResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailField;
    private TextView goBack;
    private MaterialButton resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = findViewById(R.id.emailField);
        resetBtn = findViewById(R.id.resetbtn);
        goBack = findViewById(R.id.goback);

        goBack.setOnClickListener(v -> {
            finish();
        });

        resetBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Valid email required");
                return;
            }
            sendResetOtp(this, email);
        });
    }

    private void sendResetOtp(Context context, String email) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        apiService.sendOtp(email).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (!response.body().isError()) { // <-- use isError() instead of isSuccess()
                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, PhoneVerifyActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Response is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
