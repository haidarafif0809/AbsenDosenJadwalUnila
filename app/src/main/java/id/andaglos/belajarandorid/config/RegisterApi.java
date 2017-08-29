package id.andaglos.belajarandorid.config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Andaglos on 25/08/17.
 */

public interface RegisterApi {

    @FormUrlEncoded
    @POST("tambah_siswa.php")
    Call<Value> daftarMahasiswa(@Field("nama_awal") String nama_awal,
                                @Field("nama_akhir") String nama_akhir);

    @FormUrlEncoded
    @POST("login_dosen_android")
    Call<Value>prosesLogin(@Field("username") String username,
                                @Field("password") String password);


}


