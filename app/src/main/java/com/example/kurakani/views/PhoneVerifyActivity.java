package com.example.kurakani.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class PhoneVerifyActivity extends AppCompatActivity {

    private TextInputEditText otpEditText;
    private MaterialButton btnVerifyOtp;
    private TextView tvResendOtp, tvTimer;
    private CountDownTimer countDownTimer;
    private boolean canResend = false;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        email = getIntent().getStringExtra("email");

        otpEditText = findViewById(R.id.otpEditText);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvTimer = findViewById(R.id.tvTimer);

        startOtpTimer();

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.length() != 6) {
                otpEditText.setError("Enter 6-digit OTP");
                return;
            }
            verifyOtp(this, email, otp);
        });

        tvResendOtp.setOnClickListener(v -> {
            if (canResend) resendOtp(this, email);
        });
    }

    private void startOtpTimer() {
        tvTimer.setVisibility(TextView.VISIBLE);
        canResend = false;

        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setVisibility(TextView.GONE);
                canResend = true;
            }
        }.start();
    }

    private void resendOtp(Context context, String email) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        apiService.sendOtp(email).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Toast.makeText(PhoneVerifyActivity.this, "OTP sent again", Toast.LENGTH_SHORT).show();
                startOtpTimer();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(PhoneVerifyActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtp(Context context, String email, String otp) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        apiService.verifyOtp(email, otp).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null && !response.body().isError()) {
                    // OTP is valid, pass it to ResetPasswordActivity
                    startActivity(new Intent(PhoneVerifyActivity.this, ResetPasswordActivity.class)
                            .putExtra("email", email)
                            .putExtra("otp", otp));
                    finish();
                } else {
                    Toast.makeText(PhoneVerifyActivity.this,
                            response.body() != null ? response.body().getMessage() : "Invalid OTP",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(PhoneVerifyActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
