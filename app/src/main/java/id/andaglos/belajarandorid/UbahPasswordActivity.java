package id.andaglos.belajarandorid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionInterface;

import java.util.Objects;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahPasswordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,ErrorCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    // LogCat tag
    // LogCat tag
    private static final String TAG = ListJadwalActivity.class.getSimpleName();
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


    // declrasikan variable yang dibutuhkan
    private static final int REQUEST_PERMISSIONS = 20;

    EditText EdtUsernameBaru,EdtPasswordLama, EdtPasswordBaru, EdtKonfirmasiPassword;
    Button BtnUbahPassword; // variable btn login
    SharedPreferences sharedpreferences; // untuk menyimpan data username yang sedang login
    public static final String MyPREFERENCES = "login" ;
    public static final String username = "usernameKey";
    private ProgressDialog progress;// progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        EdtUsernameBaru = (EditText) findViewById(R.id.username_baru);
        EdtPasswordBaru = (EditText) findViewById(R.id.password_baru);
        EdtPasswordLama = (EditText) findViewById(R.id.password_lama);
        EdtKonfirmasiPassword = (EditText) findViewById(R.id.konfirmasi_password);
        BtnUbahPassword = (Button) findViewById(R.id.btn_ubah_password);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username_edit = shared.getString(username, "");


        EdtUsernameBaru.setText(username_edit);

        // Pertama kita perlu memeriksa ketersediaan play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }


        BtnUbahPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // jika vaidateform hasilnya true maka
                if (vaidate_form() == true){

                    // maka edit password akan dijalankan

                    SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                    String username_edit = shared.getString(username, "");

                    ProsesEditPassword(username_edit);
                }

            }
        });

    }

    private void ProsesEditPassword(String username_edit){
        /* progress */
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");// set message/pesan progress
        progress.show();// menampilkan progress

        CrudService crud = new CrudService();
        crud.UbahPassword(username_edit,EdtUsernameBaru.getText().toString(), EdtPasswordLama.getText().toString(), EdtPasswordBaru.getText().toString(), new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progress.dismiss();// progress ditutup

                String value = response.body().getValue();
                String message = response.body().getMessage();

                if (value.equals("1")){

                    //Edit Session login
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    // untuk menyimpan data username
                    editor.putString(username, EdtUsernameBaru.getText().toString());
                    editor.commit();

                    BerhasilUbahPassword();
                }else if(value.equals("2")){

                    Toast.makeText(UbahPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    // munculkan toast Terjadi kesalahan
                }else if(value.equals("0")){

                    Toast.makeText(UbahPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    // munculkan toast Terjadi kesalahan
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();// progress ditutup

                Toast.makeText(UbahPasswordActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_LONG).show();
                // munculkan toast Terjadi kesalahan
                t.printStackTrace();
            }
        });

    }
    private boolean vaidate_form(){
        // jika username belum di isi
        if (EdtUsernameBaru.getText().toString().equals("")){

            // maka muncul required
            EdtUsernameBaru.setError("Silahkan isi username baru anda!");
            EdtUsernameBaru.requestFocus();// focus ke input username

            return false;// return false
        }else if (EdtPasswordLama.getText().toString().equals("")){

            // maka muncul required
            EdtPasswordLama.setError("Silahkan isi Password Lama anda!");
            EdtPasswordLama.requestFocus();// focus ke input username

            return false;// return false
        }else if (EdtPasswordBaru.getText().toString().equals("")){

            // maka muncul required
            EdtPasswordBaru.setError("Silahkan isi Password Baru anda!");
            EdtPasswordBaru.requestFocus();// focus ke input username

            return false;// return false
        }else if (EdtKonfirmasiPassword.getText().toString().equals("")){

            // maka muncul required
            EdtKonfirmasiPassword.setError("Silahkan isi Konfirmasi Password anda!");
            EdtKonfirmasiPassword.requestFocus();// focus ke input username

            return false;// return false
        }else if (!Objects.equals(EdtPasswordBaru.getText().toString(), EdtKonfirmasiPassword.getText().toString())) {
            // maka muncul required
            EdtKonfirmasiPassword.setError("Password Baru dan Konfirmasi Password harus sama!");
            EdtKonfirmasiPassword.requestFocus();// focus ke input username
            return  false;// return true
        } else {


            return true;// return false
        }

    }

    private void BerhasilUbahPassword(){
        AlertDialog.Builder Alert = new AlertDialog.Builder(UbahPasswordActivity.this);
        // set title dialog
        Alert.setTitle("Username dan Password berhasil di ubah");


        // set pesan dialog
        Alert.setIcon(R.drawable.logofinish);
        Alert.setCancelable(false);
        Alert.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            // tombol Ya
            public void onClick(DialogInterface dialog, int id) {
                //jika tombol Ya di klik maka akan akan menjalankan proses batal absen
                // KEMBALI KE ACTIVITY ListJadwalActivity
                dialog.cancel();
                startActivity(new Intent(UbahPasswordActivity.this,ListJadwalActivity.class));

            }
        });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = Alert.create();

        // menampilkan alert dialog
        alertDialog.show();
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
            ListJadwalActivity List = new ListJadwalActivity();
            List.logout();
            finish();
            startActivity( new Intent(UbahPasswordActivity.this, LoginActivity.class));
        } else if (item.getItemId() ==  R.id.list_jadwal_dosen){

            startActivity( new Intent(UbahPasswordActivity.this, ListJadwalActivity.class));

        } else if (item.getItemId() ==  R.id.ubah_password){

            startActivity( new Intent(UbahPasswordActivity.this, UbahPasswordActivity.class));
        } else if (item.getItemId() ==  R.id.foto_profile){

            startActivity( new Intent(UbahPasswordActivity.this, Profile.class));
        }


        return super.onOptionsItemSelected(item);
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
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }



    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
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
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // untuk menampilkan otomatis data terbaru.
    @Override
    protected void onResume() {
        super.onResume();


        // Lanjutkan update lokasi secara berkala
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onShowRationalDialog(PermissionInterface permissionInterface, int requestCode) {

    }

    @Override
    public void onShowSettings(PermissionInterface permissionInterface, int requestCode) {

    }

    public void onBackPressed()
    {
        finish();
    }// jika user tekan tombol back

}
