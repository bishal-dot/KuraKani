    package com.example.kurakani.network;

    import com.example.kurakani.model.DeletePhotoResponse;
    import com.example.kurakani.model.GetPhotosResponse;
    import com.example.kurakani.model.LoginResponse;
    import com.example.kurakani.model.ProfileRequest;
    import com.example.kurakani.model.ProfileResponse;
    import com.example.kurakani.model.SignupRequest;
    import com.example.kurakani.model.SignupResponse;
    import com.example.kurakani.model.UploadPhotosResponse;
    import com.example.kurakani.model.VerificationResponse;

    import java.util.List;

    import okhttp3.MultipartBody;
    import okhttp3.RequestBody;
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
        Call<VerificationResponse> verifyGender(
                String authToken, @Part MultipartBody.Part photo,
                @Part("user_gender") RequestBody userGender
        );

        // Matches your protected route: Route::post('user/completeProfile' ...)
        @POST("user/complete/profile")
        Call<ProfileResponse> completeProfile(
                @Body ProfileRequest profileRequest);

        @GET("user/profile")
        Call<ProfileResponse> getProfile();

        @POST("profile/update")
        Call<ProfileResponse> updateProfile(@Body ProfileRequest request);

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
    }
