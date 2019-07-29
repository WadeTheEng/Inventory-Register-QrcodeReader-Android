package mike.buildsourced.common.network;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mike.buildsourced.common.Constants;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.network.response.APIResponse;
import mike.buildsourced.common.network.response.ErrorResponse;
import mike.buildsourced.common.network.response.LoginResponse;
import mike.buildsourced.common.network.response.TokenResponse;
import mike.buildsourced.common.network.response.UpdateSinceResponse;
import mike.buildsourced.common.ContextSingleton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user1 on 6/23/2017.
 */

public class APIManager {

    private static Retrofit retrofit = null;
    private static APIInterface apiInterface = null;

    public static APIInterface getClient() {
        if (retrofit==null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(GlobInfo.URL_APIServer)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if(apiInterface == null){
            apiInterface = retrofit.create(APIInterface.class);
        }
        return apiInterface;
    }

    public static String getDeviceID(){
        Context _context = ContextSingleton.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId;
        if (telephonyManager.getDeviceId() != null)
            deviceId = telephonyManager.getDeviceId(); //*** use for mobiles
        else {
            deviceId = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    static String encodeBase64String(String s) {
        byte[] data = new byte[0];

        try {
            data = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            String base64Encoded = Base64.encodeToString(data, Base64.NO_WRAP);
            return base64Encoded;
        }
    }

    static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public static void onErrors(final ApiCallBack aCallBack,Response response){
        Converter<ResponseBody, APIResponse> converter = retrofit.responseBodyConverter(APIResponse.class, new Annotation[0]);
        try {
            if(response.message() != null){
                aCallBack.onFailure(response.message());
            }
            else{
                APIResponse errors = converter.convert(response.errorBody());
                aCallBack.onFailure(errors.errors.get(0).strErrorDetail);
            }
        } catch (Exception e) {
            aCallBack.onFailure("Unknown Error!");
        }

    }
    public static void reqLogin(String email,String passwd, final ApiCallBack aCallBack){
        final String deviceID = getDeviceID();
        //deviceID = deviceID.replacingOccurrences(of: "-", with: "")
        //deviceID = "2b6f0cc904d137be2e1730235f5664094b831186";//debug
        String _strEmailPass = email + ":" + passwd;
        //_strEmailPass = "mcook@buildsourced.com:marie123";//debug
        String _autho = "Basic " + encodeBase64String(_strEmailPass);

        Map<String,String> _param = new HashMap<String,String>();
        _param.put("udid",deviceID);

        Call<LoginResponse> _response = APIManager.getClient().apiLogin(_autho,_param);
        _response.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else {
                    LoginResponse _response = response.body();
                    String _combined = deviceID + "+" + _response.token;
                    String _shaValue = "";
                    try {
                        _shaValue = hash256(_combined);
                    } catch (NoSuchAlgorithmException exception) {
                        aCallBack.onFailure("Can't get the api key.");
                        return;
                    }
                    String _authKey = _shaValue + ":" + deviceID;
                    GlobInfo.setAuthKey(encodeBase64String(_authKey));
                    aCallBack.onSuccess(_response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void reqInitialUpdate(final ApiCallBack aCallBack){
        Call<UpdateSinceResponse> _response = APIManager.getClient().
                apiUpdateSince(GlobInfo.getAuthKey(), "2000-01-01 00:00:00");
        _response.enqueue(new Callback<UpdateSinceResponse>() {
            @Override
            public void onResponse(Call<UpdateSinceResponse> call, Response<UpdateSinceResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else
                    aCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<UpdateSinceResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void reqUpdatedSince(String fromDate, final ApiCallBack aCallBack){
        Call<UpdateSinceResponse> _response = APIManager.getClient().
                apiUpdateSince(GlobInfo.getAuthKey(), fromDate);
        _response.enqueue(new Callback<UpdateSinceResponse>() {
            @Override
            public void onResponse(Call<UpdateSinceResponse> call, Response<UpdateSinceResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else
                    aCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<UpdateSinceResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void reqGetAssetWithTokenId(String tokenId, final ApiCallBack aCallBack){
        Call<TokenResponse> _response = APIManager.getClient().
                apiToken(GlobInfo.getAuthKey(),tokenId);
        _response.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else
                    aCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void reqPostWithAssetsId(int assetId,String fieldName, String Value, final ApiCallBack aCallBack){
        String _osLevel = android.os.Build.VERSION.RELEASE;
        String _phone_type = Build.BRAND +" " + Build.MODEL;
        String _timestamp = Constants.dateString(new Date(System.currentTimeMillis()));
        Map<String,String> _param = new HashMap<String,String>();
        _param.put("field_name",fieldName);
        _param.put("value",Value);
        _param.put("os_level",_osLevel);
        _param.put("phone_type",_phone_type);
        _param.put("timestamp",_timestamp);
        Call<APIResponse> _response = APIManager.getClient().apiPostAsset(GlobInfo.getAuthKey(),assetId,_param);
        _response.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else
                    aCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void reqUploadPhoto(int assetId,String photoPath, final ApiCallBack aCallBack){
        File _filePhoto = new File(photoPath);
        RequestBody _requestFile = RequestBody.create(MediaType.parse("image/png"), _filePhoto);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", "file.png", _requestFile);
        Call<APIResponse> _response = APIManager.getClient().apiUploadPhoto(GlobInfo.getAuthKey(),assetId,body);
        _response.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(!response.isSuccessful()){
                    onErrors(aCallBack,response);
                }
                else
                    aCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                aCallBack.onFailure(t.getLocalizedMessage());
            }
        });
    }
}
