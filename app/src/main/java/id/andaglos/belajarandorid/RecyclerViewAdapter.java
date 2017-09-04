package id.andaglos.belajarandorid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.andaglos.belajarandorid.config.Result;

/**
 * Created by Andaglos on 29/08/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private Context context;
    private List<Result> results;


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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTanggal, txtWaktu, txtMataKuliah,txtNamaRuangan;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTanggal = itemView.findViewById(R.id.textTanggal);
            txtWaktu = itemView.findViewById(R.id.textWaktu);
            txtMataKuliah = itemView.findViewById(R.id.textMataKuliah);
            txtNamaRuangan = itemView.findViewById(R.id.textNamaRuangan);

        }
    }

}
