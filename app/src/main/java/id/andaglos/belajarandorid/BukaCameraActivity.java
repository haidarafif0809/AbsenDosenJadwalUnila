package id.andaglos.belajarandorid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BukaCameraActivity extends AppCompatActivity {

    private ProgressDialog progress;// PROGRESS

    //variable untuk kebutuhan upload foto
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST = 1888;

    // untuk menyimpan data username yang sedang login
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "login" ;
    public static final String userlogin = "usernameKey";

    // ENCODE IMAGE
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buka_camera);
        // LANGSUNG DIARAHKAN KE PROSES MENGAKTIFKAN KAMERA
        ambilFoto();

    }

    //PROSES UNTUK MENGAKTIFKAN KAMERA
    private void ambilFoto() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // PROSES SETELAH AMBIL FOTO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // JIKA FOTO SUDAH DIAMBIL
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

            // UBAH FOTO MENJADI STRING
            getStringImage(bitmap);
            // PROSES ABSEN MASUK
            prosesAbsenMasuk();

        }else{// JIKA TIDAK JADI AMBIL FOTO
            // KEMBALI KE ACTIVITY ListJadwalActivity
            finish();
            startActivity(new Intent(BukaCameraActivity.this,ListJadwalActivity.class));
        }
    }

    // PROSES UNTUK UBAH FOTO MENJADI STRING
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    // PROSES ABSEN MASUK
    private void prosesAbsenMasuk(){

        // getIntent() adalah metode dari aktivitas awal/ SEBELUMNYA (ListJadwalActivity)
        Intent myIntent = getIntent(); //
        String id_jadwal = myIntent.getStringExtra("id_jadwal"); // UNTUK MENDAPATKAN KEMBALI NILAI ID JADWAL
        String id_ruangan = myIntent.getStringExtra("id_ruangan"); // UNTUK MENDAPATKAN KEMBALI NILAI ID RUANGAN

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        /// session login
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username = (shared.getString(userlogin, ""));

        // JIKA BITMAP TIDAK SAMA DENGAN NULL
        if (bitmap != null){

            // MAKA JALANKAN PROSES ABSEN
            AbsenMasuk(id_jadwal, username, id_ruangan);// PASSING ID JADWAL DAN USERNAME
        }

    }

    // PROSES ABSEN MASUK
    private void AbsenMasuk(String id_jadwal, String username, String id_ruangan){

        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        // BUAT OBJEK BARU UNTUK CRUD SERVICE
        CrudService crud = new CrudService();
        // CRUD PRESENSI DOSEN=
        crud.presensiDosen(id_jadwal, username,id_ruangan, encodedImage, new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();// DAPATKAN  VALUE
                String message = response.body().getMessage();// DAPATKAN  MESSAGE
                progress.dismiss();// CLOSE PROGRESS

                // JIKA VALUE BERNILAI 1
                if (value.equals("1")){
                    // MAKA ABSEN BERHASIL
                    BerhasilAbsen();

                }else{

                    // MAKA ABSEN GAGAL
                    GagalAbsen();

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {// JIKA TERJADI THROWBLE

                progress.dismiss();// progress ditutup

                Toast.makeText(BukaCameraActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_LONG).show();
                // munculkan toast Terjadi kesalahan
                t.printStackTrace();

            }
        });


    }

    private void BerhasilAbsen(){

        AlertDialog.Builder AbsenBerhasil = new AlertDialog.Builder(BukaCameraActivity.this);
        // set title dialog
        AbsenBerhasil.setTitle("Absen Berhasil!");


        // set pesan dialog
        AbsenBerhasil.setIcon(R.drawable.logofinish);
        AbsenBerhasil.setCancelable(false);
        AbsenBerhasil.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            // tombol Ya
            public void onClick(DialogInterface dialog, int id) {
                //jika tombol Ya di klik maka akan akan menjalankan proses batal absen
                // KEMBALI KE ACTIVITY ListJadwalActivity
                startActivity(new Intent(BukaCameraActivity.this, ListJadwalActivity.class));
            }
        });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = AbsenBerhasil.create();

        // menampilkan alert dialog
        alertDialog.show();



    }

    private void GagalAbsen(){

        AlertDialog.Builder AbsenGagal = new AlertDialog.Builder(BukaCameraActivity.this);
        // set title dialog
        AbsenGagal.setTitle("Anda sudah absen di jadwal ini!");


        // set pesan dialog
        AbsenGagal.setIcon(R.drawable.logofinish);
        AbsenGagal.setCancelable(false);
        AbsenGagal.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            // tombol Ya
            public void onClick(DialogInterface dialog, int id) {
                //jika tombol Ya di klik maka akan akan menjalankan proses batal absen
                // KEMBALI KE ACTIVITY ListJadwalActivity
                startActivity(new Intent(BukaCameraActivity.this, ListJadwalActivity.class));
            }
        });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = AbsenGagal.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }// jika user tekan tombol back
}
