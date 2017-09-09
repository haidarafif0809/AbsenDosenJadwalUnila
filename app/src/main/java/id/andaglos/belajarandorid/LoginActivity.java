package id.andaglos.belajarandorid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.andaglos.belajarandorid.R.id.btn_login;

/**
 * Created by Andaglos on 29/08/17.
 */

public class LoginActivity extends AppCompatActivity {

    // declarasi kan variable yang dibutuhkan
    EditText edtUsername, edtPassword;// variable Edtusername, edtPassword
    Button btnLogin; // variable btn login
    SharedPreferences sharedpreferences; // untuk menyimpan data username yang sedang login
    public static final String MyPREFERENCES = "login" ;
    public static final String username = "usernameKey";
    private ProgressDialog progress;// progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);// set layout ke activity login

        // inisiasi variable
        edtUsername = (EditText) findViewById(R.id.input_username);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(btn_login);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        /// session login
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String login_username = (shared.getString(username, ""));

        // jika user sudah login
        if (login_username != ""){

            // maka akan muncul toast bahwa user login sebagai "username"
            Toast.makeText(LoginActivity.this, "Login as "+login_username, Toast.LENGTH_SHORT).show();
            finish();// keluar aplikasi/ activity ini ditutup(login activity)
            //dan activty nya akan langsung diarahkan ke list jadwal activity
            startActivity(new Intent(LoginActivity.this,ListJadwalActivity.class));
        }

        // valdate form login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // jika vaidateform hasilnya true maka
                if (vaidate_form() == true){
                    // maka proses login akan dijalankan
                    prosesLogin();
                }


            }
        });
    }

    // proses login
    private void prosesLogin() {

        // prgress
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");// set message/pesan progress
        progress.show();// menampilkan progress


        CrudService crud = new CrudService();// membuat object baru CrudService
        // arahkan ke proses login yang ada di crud service, untuk dikirim username dan passwordnya
        crud.prosesLogin(edtUsername.getText().toString(), edtPassword.getText().toString(), new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();// mendapatkan nilai value
                String message = response.body().getMessage();// mendapatkan hasil message

                if (value.equals("1")) {// jika value = 1
                    // user berhasil login

                    progress.dismiss();// progress di tutup
                    session_login();// simpan session login
                    finish();// tutup activity login
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    //munculkan toast message nya
                    startActivity(new Intent(LoginActivity.this,ListJadwalActivity.class));
                    // pindah activity ke list jadwal activity

                } else if (value.equals("2")){// jika value 2
                    // user gagal login , karena user bukan dosen

                    progress.dismiss();// progress ditutup
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    // munculkan toast message nya
                    edtUsername.setText("");// kosongkan input username
                    edtPassword.setText("");// kosongkan input password

                }else{// dan jika value 3
                    // user gagal login, karena user nya salah, atau passwordnya salah

                    progress.dismiss();// progress ditutup
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    // munculkan toast message nya
                    edtUsername.setText("");// kosongkan input username
                    edtPassword.setText("");// kosongkan input password
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {// jika terjadi throwble/ masalah

                progress.dismiss();// progress ditutup
                edtUsername.setText("");// kosongkan input username
                edtPassword.setText("");// kosongkan input password
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_LONG).show();
                // munculkan toast Terjadi kesalahan
                t.printStackTrace();
            }
        });

    }

    // session login
    private void session_login (){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        // untuk menyimpan data username
        editor.putString(username, edtUsername.getText().toString());
        editor.commit();

    }

    //validate form
    private boolean vaidate_form(){

        // jika username belum di isi
        if (edtUsername.getText().toString().equals("")){

            // maka muncul required
            edtUsername.setError("Silahkan isi username anda!");
            edtUsername.requestFocus();// focus ke input username

            return false;// return false
        }else{

            return  true;// return true
        }

    }

}