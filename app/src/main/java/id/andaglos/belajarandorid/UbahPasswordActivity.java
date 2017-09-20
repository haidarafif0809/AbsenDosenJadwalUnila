package id.andaglos.belajarandorid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahPasswordActivity extends AppCompatActivity {

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
        }


        return super.onOptionsItemSelected(item);
    }


}
