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

    EditText edtUsername, edtPassword;
    Button btnLogin;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "login" ;
    public static final String username = "usernameKey";
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.input_username);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(btn_login);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String login_username = (shared.getString(username, ""));

        if (login_username != ""){

            Toast.makeText(LoginActivity.this, "Login as "+login_username, Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginActivity.this,ListJadwalActivity.class));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vaidate_form() == true){
                    prosesLogin();
                }


            }
        });
    }

    private void prosesLogin() {

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();


        CrudService crud = new CrudService();
        crud.prosesLogin(edtUsername.getText().toString(), edtPassword.getText().toString(), new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();
                String message = response.body().getMessage();

                if (value.equals("1")) {

                    progress.dismiss();
                    session_login();
                    finish();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,ListJadwalActivity.class));




                } else if (value.equals("2")){

                    progress.dismiss();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();


                }else{

                    progress.dismiss();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void session_login (){

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(username, edtUsername.getText().toString());
        editor.commit();

    }

    private boolean vaidate_form(){

        if (edtUsername.getText().toString().equals("")){

            edtUsername.setError("Silahkan isi username anda!");
            edtUsername.requestFocus();

            return false;
        }else{

            return  true;
        }

    }

}