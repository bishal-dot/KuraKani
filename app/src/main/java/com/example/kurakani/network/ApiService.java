    package com.example.kurakani.network;

    import com.example.kurakani.model.ApiResponse;
    import com.example.kurakani.model.ChangePasswordRequest;
    import com.example.kurakani.model.DeletePhotoResponse;
    import com.example.kurakani.model.GetPhotosResponse;
    import com.example.kurakani.model.LoginResponse;
    import com.example.kurakani.model.Message;
    import com.example.kurakani.model.ProfileRequest;
    import com.example.kurakani.model.ProfileResponse;
    import com.example.kurakani.model.SignupRequest;
    import com.example.kurakani.model.SignupResponse;
    import com.example.kurakani.model.UploadPhotosResponse;
    import com.example.kurakani.model.SearchResponse;
    import com.example.kurakani.model.VerificationResponse;
    import com.example.kurakani.viewmodel.MatchesModel;
    import com.google.gson.JsonObject;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import okhttp3.MultipartBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.DELETE;
    import retrofit2.http.FormUrlEncoded;
    import retrofit2.http.Field;
    import retrofit2.http.GET;
    import retrofit2.http.Header;
    import retrofit2.http.Multipart;
    import retrofit2.http.POST;
    import retrofit2.http.Part;
    import retrofit2.http.Path;
    import retrofit2.http.Query;

    public interface ApiService {

        @FormUrlEncoded
        @POST("user/login")
        Call<LoginResponse> loginUser(
                @Field("email") String email,
                @Field("password") String password
        );

        @POST("user/register")
        Call<SignupResponse> registerUser(@Body SignupRequest request);

        //profile verification
        @Multipart
        @POST("profile/verify")
        Call<VerificationResponse> verifyGender(
                @Header("Authorization") String token,
                @Part MultipartBody.Part photo
        );

        // Matches your protected route: Route::post('user/completeProfile' ...)
        @POST("profile/create")
        Call<ProfileResponse> createProfile(
                @Header("Authorization") String token,
                @Body ProfileRequest request
        );

        @GET("user/profile")
        Call<ProfileResponse> getProfile();

        @GET("users/others")
        Call<List<ProfileResponse.User>> otherUsers();

        // Fetch full user details by ID
        @GET("users/{id}")
        Call<ProfileResponse.User> getUserProfile(@Path("id") int userId);

        @POST("profile/update")
        Call<JsonObject> updateProfile(@Body Map<String, Object> request);

        // Upload multiple photos
        @Multipart
        @POST("user/profilephoto")
        Call<JsonObject> uploadProfilePhoto(
                @Part MultipartBody.Part profile
        );


    @GET("photos")
    Call<GetPhotosResponse> getPhotos();

        // Upload profile photo
        @Multipart
        @POST("profile/photos")
        Call<UploadPhotosResponse> uploadPhotos(@Part List<MultipartBody.Part> photos);

        // Delete a photo
        @DELETE("profile/photos/{photoId}")
        Call<DeletePhotoResponse> deletePhoto(@Path("photoId") int photoId);

    @GET("search/users")
    Call<SearchResponse> searchUsers(
            @Query("search") String search
    );

    @GET("search/interests")
    Call<List<String>> getInterests();

    @POST("user/changepassword")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("send-match")
    @FormUrlEncoded
    Call<Void> sendNotification(
            @Field("user_id") int userId,
            @Field("title") String title,
            @Field("body") String body
    );
        // reset passowrd
        @FormUrlEncoded
        @POST("password/send-otp")
        Call<ApiResponse> sendOtp(@Field("email") String email);

        @FormUrlEncoded
        @POST("password/verify-otp")
        Call<ApiResponse> verifyOtp(@Field("email") String email, @Field("otp") String otp);

        @FormUrlEncoded
        @POST("password/reset")
        Call<ApiResponse> resetPassword(
                @Field("email") String email,
                @Field("otp") String otp,
                @Field("new_password") String newPassword,
                @Field("new_password_confirmation") String confirmPassword
        );

        // Match Notifications
//        @POST("send-match")
//        @FormUrlEncoded
//        Call<Void> sendNotification(
//                @Field("user_id") int userId,
//                @Field("title") String title,
//                @Field("body") String body
//        );

    @POST("update-fcm-token")
    Call<Void> updateFcmToken(@Body HashMap<String, String> body);

    @FormUrlEncoded
    @POST("match")
    Call<Void> sendMatch(
            @Header("Authorization") String authToken,
            @Field("user_id") int userId,
            @Field("matched_user_id") int matchedUserId
    );

    @GET("matches")
    Call<List<MatchesModel>> getMatches(
            @Query("user_id") int userId,
            @Query("status") String status
    );

    // ==========================
    // Chat / Message API Methods
    // ==========================

        // 1️⃣ Send message
        class SendMessageRequest {
            public int receiver_id;
            public String message;

            public SendMessageRequest(int receiver_id, String message) {
                this.receiver_id = receiver_id;
                this.message = message;
            }
        }

        @POST("messages/send")
        Call<Message> sendMessage(@Body SendMessageRequest request);

        // 2️⃣ Get messages with a user
        @GET("messages/{userId}")
        Call<List<Message>> getMessages(@Path("userId") int userId);

        // 3️⃣ Get all chat users
        @GET("messages")
        Call<List<ProfileResponse.User>> getChatUsers();

        // 4️⃣ Search users for dropdown
        @GET("messages/search")
        Call<List<ProfileResponse.User>> searchChatUsers(@Query("search") String searchQuery);

}
