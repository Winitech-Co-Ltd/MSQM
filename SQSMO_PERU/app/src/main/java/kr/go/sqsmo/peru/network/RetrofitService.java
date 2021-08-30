package kr.go.sqsmo.peru.network;

import kr.go.sqsmo.peru.data.KeyData;
import kr.go.sqsmo.peru.data.LoginServiceData;
import kr.go.sqsmo.peru.data.RetrofitData;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Retrofit Service Interface
 */
public interface RetrofitService {
    /**
     * Definición de Service
     */
    @Headers({"Content-Type: application/json","Connection: close"})
    @POST("servicecall.do")
    Call<KeyData> key_Service(@Body RequestBody mJsonObject);

    @Headers({"Content-Type: application/json","Connection: close"})
    @POST("servicecall.do")
    Call<LoginServiceData> login_Service(@Body RequestBody mJsonObject);

    @Headers({"Content-Type: application/json","Connection: close"})
    @POST("servicecall.do")
    Call<RetrofitData> peruo_Service(@Header("peruoname") String username, @Body RequestBody mJsonObject);

}
