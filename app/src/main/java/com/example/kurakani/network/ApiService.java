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
    import com.google.gson.JsonObject;

    import java.util.List;
    import java.util.Map;

    import okhttp3.MultipartBody;
    import okhttp3.RequestBody;
    import okhttp3.ResponseBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.DELETE;
    import retrofit2.http.FormUrlEncoded;
    import retrofit2.http.Field;
    import retrofit2.http.GET;
    import retrofit2.http.Header;
    import retrofit2.http.Headers;
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

        @Multipart
        @POST("verifygender/temp")
        Call<VerificationResponse> verifyGenderTemp(
                @Part MultipartBody.Part photo,
                @Part("user_gender") RequestBody userGender,
                @Part("temp_token") RequestBody tempToken
        );

        // Matches your protected route: Route::post('user/completeProfile' ...)
        @POST("profile/temp")
        Call<ResponseBody> completeProfileTemp(
                @Header("Authorization") String token,
                @Body ProfileRequest request
        );

        @POST("profile/finalize")
        Call<ProfileResponse> finalizeProfile(
                @Header("Authorization") String tempToken
        );

        @GET("user/profile")
        Call<ResponseBody> getProfile();

        @GET("users/others")
        Call<List<ProfileResponse.User>> otherUsers();

        // Fetch full user details by ID
        @GET("users/{id}")
        Call<ProfileResponse.User> getUserProfile(@Path("id") int userId);

        @POST("profile/update")
        Call<JsonObject> updateProfile(@Body Map<String, Object> request);

        // Upload multiple photos
        @Multipart
        @POST("user/profile/photos")
        Call<UploadPhotosResponse> uploadPhotos(
                @Part List<MultipartBody.Part> photos
        );

        // Get all photos
        @GET("photos")
        Call<GetPhotosResponse> getPhotos();

        // Delete a photo
        @DELETE("profile/photos/{photoId}")
        Call<DeletePhotoResponse> deletePhoto(@Path("photoId") int photoId);

        @GET("search/users")
        Call<SearchResponse> searchUsers(
                @Query("search") String search,
                @Query("interests") String interests
        );

        @GET("search/interests")
        Call<List<String>> getInterests();

        // Sending and Receiving messages
        // GET messages/{otherUserId}
        @GET("messages/{otherUserId}")
        Call<List<Message>> getMessages(@Path("otherUserId") int otherUserId);

        // POST messages/{otherUserId}
        @FormUrlEncoded
        @POST("messages/{otherUserId}")
        Call<Message> sendMessage(
                @Path("otherUserId") int otherUserId,
                @Field("message") String message
        );

        //change password
        @POST("user/changepassword")
        Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);

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
    }
