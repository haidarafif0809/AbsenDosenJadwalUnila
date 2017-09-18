package id.andaglos.belajarandorid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import java.util.ArrayList;
import java.util.List;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Result;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListJadwalActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, PermissionCallback, ErrorCallback {

    // declrasikan variable yang dibutuhkan
    private static final int REQUEST_PERMISSIONS = 20;

    private List<Result> results = new ArrayList<>();// result
    private RecyclerViewAdapter viewAdapter;// viewAdapter
    private ImageView imageView;// image view
    private TextView jadwal_kosong; // text view jadwal kosong

    SearchView search;// search/ pencarian
    RecyclerView recyclerView;// recyclerView
    ProgressBar progressBar;// progresabar

    // untuk menyimpan data username yang sedang login
    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "login" ;
    public static final String username = "usernameKey";
    public static final String loginusername = "usernameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_jadwal);

        // inisiasi variable
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        search = (SearchView) findViewById(R.id.search);
        imageView = (ImageView) findViewById(R.id.ImageKosong);
        jadwal_kosong = (TextView) findViewById(R.id.jadwal_kosong);

        viewAdapter = new RecyclerViewAdapter(this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String login_username = shared.getString(username, "");

        // passing varibel username
        loadDataJadwal(login_username);

         reqPermission();
    }

    // proses menampilkan list jadwal dosen
    public void loadDataJadwal(String username) {

        imageView.setVisibility(View.GONE);// hidden Image View
        jadwal_kosong.setVisibility(View.GONE);// hidden Text Jadwal Kosong

        CrudService crud = new CrudService();
        crud.listJadwal(username,new Callback <Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();// ambil vallue

                progressBar.setVisibility(View.GONE);// hidden progressbar

                if (value.equals("0")) {// jika value bernilai nol,
                    // maka
                    imageView.setVisibility(View.VISIBLE);// Show Image View
                    jadwal_kosong.setVisibility(View.VISIBLE);// Hidden Text Jadwal Kosong

                }else{// jika tidak
                    // tampilkan lsit jadwal
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(ListJadwalActivity.this, results);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {// jika terjadi throwble

                progressBar.setVisibility(View.GONE);// hidden progressbar
                Toast.makeText(ListJadwalActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();
                // toast terjadi KEsalahan
                t.printStackTrace();

            }
        });
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // fitur search jadwal/ pencarian jadwa;
    @Override
    public boolean onQueryTextChange(String newText) {// keyup

        recyclerView.setVisibility(View.GONE);// hidden recycle view
        progressBar.setVisibility(View.VISIBLE);// munculkan progressbar

        // untuk menda[takn session login
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username = (shared.getString(loginusername, ""));

        CrudService crud = new CrudService();
        crud.searchJadwal(newText,username, new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE); //hidden progressBar
                recyclerView.setVisibility(View.VISIBLE);// tampilkan recycle view

                // jika value bernilai 1
                if (value.equals("1")) {
                    // maka akan tampil jadwal yang dicari
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(ListJadwalActivity.this, results);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                progressBar.setVisibility(View.GONE); //hidden progressBar
                Toast.makeText(ListJadwalActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                t.printStackTrace();

            }

        });

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Cari Jadwal...");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // logut
        if (item.getItemId() ==  R.id.logout) {
            logout();
            finish();
            startActivity( new Intent(ListJadwalActivity.this, LoginActivity.class));
        } else if (item.getItemId() ==  R.id.list_jadwal_dosen){

            startActivity( new Intent(ListJadwalActivity.this, ListJadwalActivity.class));

        } else if (item.getItemId() ==  R.id.ubah_password){

            startActivity( new Intent(ListJadwalActivity.this, UbahPasswordActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }// jika user tekan tombol back

    // logout
    public void logout (){

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();// edit sessionlogin
        editor.clear();// bersihkan session login
        editor.commit();// simpan
    }

// untuk menampilkan otomatis data terbaru.
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String login_username = (shared.getString(username, ""));

        loadDataJadwal(login_username);
    }

    public void reqPermission() {
        new AskPermission.Builder(this).setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .setCallback(this)
                .setErrorCallback(this)
                .request(REQUEST_PERMISSIONS);
    }


    @Override
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
        reqPermission();
    }
}
