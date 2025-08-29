package com.example.kurakani.network;

import com.example.kurakani.model.ApiResponse;
import com.example.kurakani.model.ChangePasswordRequest;
import com.example.kurakani.model.DeletePhotoResponse;
import com.example.kurakani.model.GetPhotosResponse;
import com.example.kurakani.model.LoginResponse;
import com.example.kurakani.model.ProfileRequest;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.model.SignupRequest;
import com.example.kurakani.model.SignupResponse;
import com.example.kurakani.model.UploadPhotosResponse;
import com.example.kurakani.model.SearchResponse;
import com.example.kurakani.model.User;
import com.example.kurakani.model.VerificationResponse;
import com.example.kurakani.viewmodel.MatchesModel;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
    @POST("verifygender")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<VerificationResponse> verifyGender(
            String authToken, @Part MultipartBody.Part photo,
            @Part("user_gender") RequestBody userGender
    );

    @POST("user/complete/profile")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<ProfileResponse.User> completeProfile(@Body ProfileRequest profileRequest);

    // ✅ Updated: Return ProfileResponse instead of JsonObject
    @GET("user/profile")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<ProfileResponse> getProfile();

    @GET("users/others")
    Call<List<User>> otherUsers();

    @GET("users/{id}")
    Call<User> getUserProfile(@Path("id") int userId);

    @POST("profile/update")
    Call<JsonObject> updateProfile(@Body Map<String, Object> request);

    @Multipart
    @POST("user/profile/photos")
    Call<UploadPhotosResponse> uploadPhotos(
            @Part List<MultipartBody.Part> photos
    );

    @GET("photos")
    Call<GetPhotosResponse> getPhotos();

    @DELETE("profile/photos/{photoId}")
    Call<DeletePhotoResponse> deletePhoto(@Path("photoId") int photoId);

    @GET("search/users")
    Call<SearchResponse> searchUsers(
            @Query("search") String search,
            @Query("interests") String interests
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

    @GET("messages")
    Call<List<User>> getChatUsers();

    @GET("messages/{userId}")
    Call<List<com.example.kurakani.model.Message>> getMessages(@Path("userId") int userId);

    @POST("messages/send")
    Call<com.example.kurakani.model.Message> sendMessage(@Body SendMessageRequest request);

    public static class SendMessageRequest {
        private int receiver_id;
        private String message;   // optional for text
        private String imageUrl;  // optional for image

        // Constructor for text-only messages
        public SendMessageRequest(int receiver_id, String message) {
            this.receiver_id = receiver_id;
            this.message = message;
            this.imageUrl = null;
        }

        // Constructor for image-only messages
        public SendMessageRequest(int receiver_id, String imageUrl, boolean isImage) {
            this.receiver_id = receiver_id;
            this.message = null;
            this.imageUrl = imageUrl;
        }

        // Constructor for both text and image messages
        public SendMessageRequest(int receiver_id, String message, String imageUrl) {
            this.receiver_id = receiver_id;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        // Getters (optional, if Retrofit/Gson needs them)
        public int getReceiver_id() { return receiver_id; }
        public String getMessage() { return message; }
        public String getImageUrl() { return imageUrl; }
    }


    //    Search users in chat
    @GET("messages/search")
    Call<List<User>> searchUsers(
            @Query("search") String searchQuery
    );

}
