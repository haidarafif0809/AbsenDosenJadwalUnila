package id.andaglos.belajarandorid.config;

import java.util.concurrent.TimeUnit;

import id.andaglos.belajarandorid.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andaglos on 25/08/17.
 */

public class CrudService {

    private RegisterApi registerApi;

    public CrudService() {

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder();
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        registerApi = retrofit.create(RegisterApi.class);

    }

    public void daftarMahasiswa(String nama_awal, String nama_akhir, Callback callback){
        registerApi.daftarMahasiswa(nama_awal , nama_akhir).enqueue(callback);
    }

    public void prosesLogin(String username, String password, Callback callback){
        registerApi.prosesLogin(username , password).enqueue(callback);
    }


}
