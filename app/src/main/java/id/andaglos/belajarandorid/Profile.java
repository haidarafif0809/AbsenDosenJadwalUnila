package id.andaglos.belajarandorid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import id.andaglos.belajarandorid.config.Config;
import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Result;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.andaglos.belajarandorid.R.id.no_telp;


public class Profile extends AppCompatActivity{

    //variable untuk kebutuhan upload foto
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST = 1888;

    // ENCODE IMAGE
    private String encodedImage;

    private ImageView image;// image view
    TextView TextNik,TextNama, TextNo_telp;// NIK, NAMA, NO TELP
    private List<Result> results = new ArrayList<>();// result

    private ProgressDialog progress;// PROGRESS

    // untuk menyimpan data username yang sedang login
    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "login" ;
    public static final String username = "usernameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        image = (ImageView) findViewById(R.id.foto_profile);//ID FOTO PROFILE
        TextNama = (TextView) findViewById(R.id.nama);// ID NAMA
        TextNik = (TextView) findViewById(R.id.nik);// ID NIK
        TextNo_telp = (TextView) findViewById(no_telp);// ID NO TELP

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String user = shared.getString(username, "");

            // CEK FOTO PROFILE
        CekFotoProfile(user);

        // ONCLICK IMAGE
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // GANTI FOTO/ UPLOAD FOTO
                GantiFoto();
            }
        });
    }

        // CEK FOTO PROFILE
    public void CekFotoProfile(final String user){
        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        CrudService crud = new CrudService();
        crud.CekProfileDosen(user, new Callback <Value>() {
            @Override
            public void onResponse(Call <Value> call, Response <Value>response) {
                progress.dismiss();
                String value = response.body().getValue();// ambil vallue
                results = response.body().getResult();// AMBIL RESULT

                Result result = results.get(0);

                MenampilkanFoto(result,user,value);

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                Toast.makeText(Profile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();


            }
        });

    }

    public void MenampilkanFoto(Result result, String user, String value){

        TextNama.setText(result.getNamaDosen());
        TextNik.setText(user);
        TextNo_telp.setText(result.getNoTelp());

        if (value.equals("0")){// jika value sama degan nol
            // nol berarti foto masih kosong

            // menampilkan foto
            Picasso.with(this)
                    .load(R.mipmap.profile_circle)
                    .placeholder(R.mipmap.profile_circle)   // optional
                    .error(R.mipmap.profile_circle)
                    .transform(new CircleTransform())
                    .into(image);
        }else{

        // menampilkan foto
        Picasso.with(this)
                .load(Config.BASE_URL + result.getFotoProfile())
                .placeholder(R.mipmap.profile_circle)   // optional
                .error(R.mipmap.profile_circle)
                .transform(new CircleTransform())
                .into(image);
        }

    }


// METHODE UMTUK MEMBUAT AGAR FOTO PROFILE JADI CIRCLE
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }


    // ALERT DIALOG GANTI FOTO
    public void GantiFoto() {

        AlertDialog.Builder GantiFoto = new AlertDialog.Builder(Profile.this);
        // set title dialog



        // set pesan dialog
        GantiFoto.setIcon(R.drawable.logofinish);
        GantiFoto.setCancelable(false);
        GantiFoto.setPositiveButton("Kamera", new DialogInterface.OnClickListener() {
            // tombol Ya
            public void onClick(DialogInterface dialog, int id) {
                //jika tombol Ya di klik maka akan akan menjalankan proses AMBIL FOTO

                ambilFoto();

            }
        });
        GantiFoto.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
            // tombol Ya
            public void onClick(DialogInterface dialog, int id) {
                //jika tombol Ya di klik maka akan akan menjalankan proses batal absen
                // KEMBALI KE ACTIVITY ListJadwalActivity
                dialog.cancel();
            }
        });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = GantiFoto.create();

        // menampilkan alert dialog
        alertDialog.show();
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
            ProsesUpdateProfile();

        }else{// JIKA TIDAK JADI AMBIL FOTO
            // KEMBALI KE ACTIVITY ListJadwalActivity
            finish();
            startActivity(new Intent(Profile.this,ListJadwalActivity.class));
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

    private void ProsesUpdateProfile() {

        if (bitmap != null){


            SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            final String user = shared.getString(username, "");

            //membuat progress dialog
            progress = new ProgressDialog(this);
            progress.setCancelable(false);
            progress.setMessage("Loading ...");
            progress.show();

            CrudService crud = new CrudService();
            crud.UpdateProfile(encodedImage, user, new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {

                    String value = response.body().getValue();
                    String message = response.body().getMessage();
                    progress.dismiss();
                    if (value.equals("1")){
                        Berhasil(user);
                        // munculkan toast Terjadi kesalahan
                    }else{

                        Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                        // munculkan toast Terjadi kesalahan
                    }
                }



                @Override
                public void onFailure(Call call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(Profile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    private void Berhasil(String user) {

        CekFotoProfile(user);
        Toast.makeText(Profile.this, "Foto Berhasil Di Ubah", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // logut
        if (item.getItemId() ==  R.id.logout) {
            logout();
            finish();
            startActivity( new Intent(Profile.this, LoginActivity.class));
        } else if (item.getItemId() ==  R.id.list_jadwal_dosen){

            startActivity( new Intent(Profile.this, ListJadwalActivity.class));

        } else if (item.getItemId() ==  R.id.ubah_password){

            startActivity( new Intent(Profile.this, UbahPasswordActivity.class));
        } else if (item.getItemId() ==  R.id.foto_profile){

            startActivity( new Intent(Profile.this, Profile.class));
        }


        return super.onOptionsItemSelected(item);
    }

    // logout
    public void logout (){

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();// edit sessionlogin
        editor.clear();// bersihkan session login
        editor.commit();// simpan
    }

    public void onBackPressed()
    {
        finish();
    }// jika user tekan tombol back

}
