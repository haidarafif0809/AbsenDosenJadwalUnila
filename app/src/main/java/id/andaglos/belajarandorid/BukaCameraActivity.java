package id.andaglos.belajarandorid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.round;


public class BukaCameraActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
        // LogCat tag
    // LogCat tag
    private static final String TAG = BukaCameraActivity.class.getSimpleName();
    public double latitude_sekarang,longitude_sekarang;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    public float jarak_ke_lokasi_absen;

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



        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        ambilFoto();

    }


    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            latitude_sekarang =  mLastLocation.getLatitude();
            longitude_sekarang = mLastLocation.getLongitude();


            hitung_jarak_absen();

        } else {

        }
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the devicedfhdfhknvk
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }


    private void hitung_jarak_absen(){

        // getIntent() adalah metode dari aktivitas awal/ SEBELUMNYA (ListJadwalActivity)
        Intent myIntent = getIntent(); //
        String latitude_ruangan = myIntent.getStringExtra("latitude");
        String longitude_ruangan = myIntent.getStringExtra("longitude");


        Location loc1 = new Location("");
        loc1.setLatitude(latitude_sekarang);
        loc1.setLongitude(longitude_sekarang);

        Location loc2 = new Location("");
        loc2.setLatitude(Double.valueOf(latitude_ruangan));
        loc2.setLongitude(Double.valueOf(longitude_ruangan));

        jarak_ke_lokasi_absen = loc1.distanceTo(loc2);
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
        String id_jadwal = myIntent.getStringExtra("id_jadwal");
        // UNTUK MENDAPATKAN KEMBALI NILAI ID JADWAL
        String id_ruangan = myIntent.getStringExtra("id_ruangan");
        // UNTUK MENDAPATKAN KEMBALI NILAI ID RUANGAN
        String batas_jarak_absen = myIntent.getStringExtra("batas_jarak_absen");
        // UNTUK MENDAPATKAN KEMBALI BATAS JAARAK ABSEN
        String waktu_jadwal = myIntent.getStringExtra("waktu_jadwal");
        // UNTUK MENDAPATKAN KEMBALI NILAI ID RUANGAN
        String tanggal = myIntent.getStringExtra("tanggal");
        // UNTUK MENDAPATKAN KEMBALI BATAS JAARAK ABSEN


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        /// session login
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username = (shared.getString(userlogin, ""));

        displayLocation();


          if (jarak_ke_lokasi_absen <= Float.valueOf(batas_jarak_absen) ){
              if (latitude_sekarang != 0 && longitude_sekarang != 0){

                  // JIKA BITMAP TIDAK SAMA DENGAN NULL
                  if (bitmap != null){

                      // MAKA JALANKAN PROSES ABSEN
                      AbsenMasuk(id_jadwal, username, id_ruangan, jarak_ke_lokasi_absen, waktu_jadwal, tanggal);// PASSING ID JADWAL DAN USERNAME
                  }

              }
          }else{

              LokasiTerlaluJauh(jarak_ke_lokasi_absen, batas_jarak_absen);

          }



    }

    // PROSES ABSEN MASUK
    private void AbsenMasuk(String id_jadwal, String username, String id_ruangan, Float jarak_ke_lokasi_absen, String waktu_jadwal, String tanggal){

        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        // BUAT OBJEK BARU UNTUK CRUD SERVICE
        CrudService crud = new CrudService();
        // CRUD PRESENSI DOSEN=
        crud.presensiDosen(id_jadwal, username,id_ruangan, encodedImage,
                String.valueOf(latitude_sekarang), String.valueOf(longitude_sekarang),
                String.valueOf(round(jarak_ke_lokasi_absen)),waktu_jadwal,tanggal,new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();// DAPATKAN  VALUE
                String message = response.body().getMessage();// DAPATKAN  MESSAGE
                progress.dismiss();// CLOSE PROGRESS

                // JIKA VALUE BERNILAI 1
                if (value.equals("1")){
                    // MAKA ABSEN BERHASIL
                    BerhasilAbsen();


                }else if (value.equals("2")){

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

    private void LokasiTerlaluJauh(Float jarak_ke_lokasi_absen, String batas_jarak_absen){


        Intent myIntent = getIntent(); //
        String nama_ruangan = myIntent.getStringExtra("nama_ruangan");

        AlertDialog.Builder AbsenGagal = new AlertDialog.Builder(BukaCameraActivity.this);
        LayoutInflater inflater = BukaCameraActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialog_alert, null);
        AbsenGagal.setView(dialogView);
        // set title dialog
        AbsenGagal.setTitle("Ruangan " + nama_ruangan +" Terlalu Jauh");

        TextView jarak_ruangan = (TextView) dialogView.findViewById(R.id.JarakKeRuangan);
        TextView batas_jarak = (TextView) dialogView.findViewById(R.id.BatasJarakAbsen);

        jarak_ruangan.setText(String.valueOf(round(jarak_ke_lokasi_absen)) + " m");
        batas_jarak.setText(batas_jarak_absen + " m");

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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
        @Override
        public void onConnected(Bundle arg0) {

            // Once connected with google api, get the location
            displayLocation();
        }

        @Override
        public void onConnectionSuspended(int arg0) {
            mGoogleApiClient.connect();
        }


        /**
         * Google api callback methods
         */
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                    + result.getErrorCode());
        }
}


