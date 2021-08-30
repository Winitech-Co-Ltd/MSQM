package kr.go.sqsm.peru.network;

import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.KeyData;
import kr.go.sqsm.peru.data.servicedata.LoginServiceData;
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
     * PERU Service
     * @param mJsonObject JSON
     */
    @Headers({"Content-Type: application/json", "Connection: close"})
    @POST("servicecall.do")
    Call<KeyData> key_Service(@Body RequestBody mJsonObject);

    @Headers({"Content-Type: application/json", "Connection: close"})
    @POST("servicecall.do")
    Call<LoginServiceData> login_Service(@Body RequestBody mJsonObject);

    @Headers({"Content-Type: application/json", "Connection: close"})
    @POST("servicecall.do")
    Call<LoginServiceData> login2_Service(@Header("peruuname") String key, @Body RequestBody mJsonObject);

    @Headers({"Content-Type: application/json", "Connection: close"})
    @POST("servicecall.do")
    Call<RetrofitData> private_Service(@Header("peruuname") String key, @Body RequestBody mJsonObject);

}
