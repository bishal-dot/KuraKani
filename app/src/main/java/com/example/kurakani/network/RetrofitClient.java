package com.example.kurakani.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.10.6:8000/api/";
    public static int CURRENT_USER_ID = 1;

    private static Retrofit retrofitNoUserId = null;
    private static Retrofit retrofitWithUserId = null;

    private static OkHttpClient buildClient(Context context, boolean includeUserId) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        // Auth token from SharedPreferences + X-User-Id
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            SharedPreferences prefs = context.getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);

            Request.Builder builder = original.newBuilder()
                    .method(original.method(), original.body());

            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }

            // Include X-User-Id for API calls that require it
            if (includeUserId) {
                builder.header("X-User-Id", String.valueOf(CURRENT_USER_ID));
            }

            return chain.proceed(builder.build());
        });

        return httpClient.build();
    }

    private static Gson getLenientGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    public static Retrofit getClient(Context context) {
        if (retrofitNoUserId == null) {
            retrofitNoUserId = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(buildClient(context, false))
                    .addConverterFactory(GsonConverterFactory.create(getLenientGson()))
                    .build();
        }
        return retrofitNoUserId;
    }

    public static Retrofit getClientWithUserId(Context context) {
        if (retrofitWithUserId == null) {
            retrofitWithUserId = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(buildClient(context, true))
                    .addConverterFactory(GsonConverterFactory.create(getLenientGson()))
                    .build();
        }
        return retrofitWithUserId;
    }

    public static void resetClient() {
        retrofitNoUserId = null;
        retrofitWithUserId = null;
    }
}
