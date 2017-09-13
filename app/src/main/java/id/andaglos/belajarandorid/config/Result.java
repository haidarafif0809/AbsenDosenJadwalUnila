package id.andaglos.belajarandorid.config;

/**
 * Created by Andaglos on 25/08/17.
 */

public class Result{

    String tanggal;
    String waktu;
    String mata_kuliah;
    String nama_ruangan;
    String id_jadwal;
    String id_ruangan;
    String latitude;
    String longitude;
    String batas_jarak_absen;

    public String getTanggal(){

        return tanggal;
    }
    public String getWaktu(){

        return waktu;
    }
    public String getMataKuliah(){

        return mata_kuliah;
    }
    public String getNamaRuangan(){
        return nama_ruangan;

    }

    public String getIdJadwal(){
        return id_jadwal;
    }

    public String getIdRuangan(){
        return id_ruangan;
    }

    public String getLatitude(){
        return latitude;

    }

    public String getLongitude(){
        return longitude;
    }

    public String getBatasJarakAbsen(){
        return batas_jarak_absen;
    }

}
