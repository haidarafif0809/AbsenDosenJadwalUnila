package id.andaglos.belajarandorid.config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @FormUrlEncoded
    @POST("list_jadwal_dosen")
    Call<Value>listJadwal(@Field("username") String username );

    @FormUrlEncoded
    @POST("search_jadwal_dosen")
    Call<Value>searchJadwal(@Field("search") String search,
                            @Field("username") String username);


    @FormUrlEncoded
    @POST("batal_jadwal_dosen")
    Call<Value>batalJadwalDosen(@Field("id_jadwal") String id_jadwal);

    @FormUrlEncoded
    @POST("presensi_dosen")
    Call<Value>presensiDosen(@Field("id_jadwal") String id_jadwal,
                             @Field("username") String username,
                             @Field("id_ruangan") String id_ruangan,
                             @Field("image") String image,
                             @Field("latitude_sekarang") String latitude_sekarang,
                             @Field("longitude_sekarang") String longitude_sekarang,
                             @Field("jarak_ke_lokasi_absen") String jarak_ke_lokasi_absen,
                             @Field("waktu_jadwal") String waktu_jadwal,
                             @Field("tanggal") String tanggal);

    @FormUrlEncoded
    @POST("ubah_password_dosen")
    Call<Value>UbahPassword(@Field("username") String username,
                            @Field("username_baru") String username_baru,
                            @Field("password_lama") String password_lama,
                            @Field("password_baru") String password_baru);


    @FormUrlEncoded
    @POST("cek_profile_dosen")
    Call<Value>CekProfileDosen(@Field("user") String user);

    @FormUrlEncoded
    @POST("update_profile_dosen")
    Call<Value>UpdateProfile(@Field("image") String image,
                             @Field("user") String user);


    @GET("versi-absen-dosen")
    Call<Value> CekVersiAplikasi();

}


