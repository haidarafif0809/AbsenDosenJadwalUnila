package id.andaglos.belajarandorid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.andaglos.belajarandorid.config.CrudService;
import id.andaglos.belajarandorid.config.Result;
import id.andaglos.belajarandorid.config.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static id.andaglos.belajarandorid.R.id.textTanggal;

/**
 * Created by Andaglos on 29/08/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ProgressDialog progress;
    private Context context;
    private List<Result> results;
    RecyclerView recyclerView;
    ProgressBar progressBar;



    public RecyclerViewAdapter(Context context, List<Result> results){
        this.context = context;
        this.results = results;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        Result result = results.get(position);
        holder.txtTanggal.setText(result.getTanggal());
        holder.txtWaktu.setText(result.getWaktu());
        holder.txtMataKuliah.setText(result.getMataKuliah());
        holder.txtNamaRuangan.setText(result.getNamaRuangan());
        holder.txtIdJadwal.setText(result.getIdJadwal());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_jadwal, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtTanggal, txtWaktu, txtMataKuliah,txtNamaRuangan, txtIdJadwal;

        SharedPreferences sharedpreferences;
        public static final String MyPREFERENCES = "login" ;



        public String id_jadwal;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txtTanggal = itemView.findViewById(textTanggal);
            txtWaktu = itemView.findViewById(R.id.textWaktu);
            txtMataKuliah = itemView.findViewById(R.id.textMataKuliah);
            txtNamaRuangan = itemView.findViewById(R.id.textNamaRuangan);
            txtIdJadwal = itemView.findViewById(R.id.textIdJadwal);
            txtIdJadwal.setVisibility(View.GONE);// hidden id jadwal

            SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String username = shared.getString("usernameKey", "");
        }

        @Override
        public void onClick(View view) {

            id_jadwal = txtIdJadwal.getText().toString();

            showDialog(id_jadwal);

        }
    }

    // tampilkan AlertDiaqlog
    private void showDialog(final String id_jadwal){


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title dialog
        alertDialogBuilder.setTitle("Konfirmasi Jadwal");


        // set pesan dialog
        alertDialogBuilder
                .setIcon(R.drawable.schedule_background)
                .setCancelable(false)
                .setPositiveButton("Absen", new DialogInterface.OnClickListener(){
                    // tombol absen
                    public void onClick(DialogInterface dialog, int id){
                        //jika tombol diklik maka akan keluar dari aplikasi
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    // tombol batal
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, maka proses pembatalan jadwal akan dijalankan

                        BatalJadwalDosen(id_jadwal);

                    }
                })
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener(){
                    // tombol tutup
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void BatalJadwalDosen(String id_jadwal){

        //membuat progress dialog
        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        CrudService crud = new CrudService();
        crud.batalJadwalDosen(id_jadwal, new Callback<Value>(){

            @Override
            public void onResponse(Call<Value> call, Response<Value> response){

                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();

                if (value.equals("1")){

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context , ListJadwalActivity.class));
                }


            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }


        });


    }

}
