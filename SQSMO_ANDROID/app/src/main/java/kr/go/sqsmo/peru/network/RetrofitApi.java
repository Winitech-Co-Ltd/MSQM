package kr.go.sqsmo.peru.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.go.sqsmo.peru.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit Create
 */
public class RetrofitApi {
    private HttpLoggingInterceptor interceptor =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);


    private OkHttpClient client =
            new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(false)
                    .cache(null)
                    .build();

    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

    public RetrofitService getService() {
        return retrofit.create(RetrofitService.class);
    }

}
