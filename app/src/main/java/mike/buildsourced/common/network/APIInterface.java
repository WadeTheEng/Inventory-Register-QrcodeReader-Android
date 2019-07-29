package mike.buildsourced.common.network;

import java.util.Map;

import mike.buildsourced.common.network.response.APIResponse;
import mike.buildsourced.common.network.response.LoginResponse;
import mike.buildsourced.common.network.response.TokenResponse;
import mike.buildsourced.common.network.response.UpdateSinceResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user1 on 6/23/2017.
 */

public interface APIInterface {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> apiLogin(@Header("authorization") String authorization, @FieldMap Map<String, String> names);

    @GET("assets/updated_since")
    Call<UpdateSinceResponse> apiUpdateSince(@Header("X-User-Api-Key") String apiKey, @Query("from") String fromDate);

    @GET("assets/token/{id}")
    Call<TokenResponse> apiToken(@Header("X-User-Api-Key") String apiKey, @Path("id") String tokenId);

    @FormUrlEncoded
    @POST("assets/{id}")
    Call<APIResponse> apiPostAsset(@Header("X-User-Api-Key") String apiKey, @Path("id") int assetId, @FieldMap Map<String, String> names);

    @Multipart
    @POST("assets/{id}/addPhoto")
    Call<APIResponse> apiUploadPhoto(@Header("X-User-Api-Key") String apiKey, @Path("id") int assetId,  @Part MultipartBody.Part file);
}
