package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.HashMap;
import java.util.List;

import ipead.com.br.newandroidbancodepreco.adapter.RecycleGrupoAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.GPSTracker;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.GrupoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Informante;

public class GrupoActivity extends AppCompatActivity {

    private int idRota;
    private int idPeriodoColeta;
    private int idInformante;
    private int tipoInformante;
    private int position;
    private List<HashMap<String, String>> grupos;
    private String filepath = null;
    private String mainPath;
    public DirectoryBrowser browser;
    private RecycleGrupoAdapter recycleGrupoAdapter;
    private RecyclerView recycleList;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;
    private InformanteDAO informanteDAO;
    private List<Informante> listInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        browser = new DirectoryBrowser(getApplicationContext());

        recycleList = findViewById(R.id.recycleViewer);

        recycleList.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        Bundle b = getIntent().getExtras();

        idRota = b.getInt("idRota");
        idPeriodoColeta = b.getInt("idPeriodo");
        idInformante = b.getInt("idInfo");
        position = b.getInt("position");
        mainPath = b.getString("mainPath");

        Log.d("TAGS INFOS", idRota + ", " + idPeriodoColeta + ", " + idInformante + ", " + position);
        Log.d("MAIN PATH GRUPO", mainPath);

        filepath = browser.dir() + MultiBD.BD_INFORMANTE  + "" + idPeriodoColeta + "-" + idRota + "-" + idInformante;

        mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        informanteDAO = new InformanteDAO(getApplicationContext(), null, mainPath);

        listInfo = informanteDAO.getLocationsByInformante(String.valueOf(idRota),
                String.valueOf(idPeriodoColeta), String.valueOf(idInformante));

        conferirDistanciaInformante();

        load();

    }


    private void load() {

        GrupoDAO grupoDAO = new GrupoDAO(filepath);

        grupos = grupoDAO.selectGrupo(String.valueOf(idInformante));

        InformanteDAO infDAO = new InformanteDAO(getApplicationContext(), null, mainPath);
        tipoInformante = infDAO.getTipoInformante(String.valueOf(idInformante));

        if(grupos == null || grupos.size() == 0){

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem grupos no dispositivo!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    GrupoActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            recycleGrupoAdapter = new RecycleGrupoAdapter(GrupoActivity.this, grupos, idPeriodoColeta,
                    idInformante, tipoInformante, filepath);
            recycleList.setAdapter(recycleGrupoAdapter);

        }

    }

    private void conferirDistanciaInformante() {

        boolean statusGps = SharedPref.readBoolean("statusGps", false);
        int valueSeek = SharedPref.readInt("valueSeek", 0) * 200;

        GPSTracker gps = new GPSTracker(GrupoActivity.this);

        double distancia = 0.0;
        double latUser = gps.getLatitude();
        double longUser = gps.getLongitude();
        double latInformante = listInfo.get(0).getLatitude();
        double longInformante = listInfo.get(0).getLongitude();

        Log.d("VALORES LAT LONG ", String.valueOf(latUser) + ", " + String.valueOf(longUser) + ", " +
                String.valueOf(latInformante) + ", " + String.valueOf(longInformante));

        if(latInformante != 0.0 && longInformante != 0.0){
            distancia = distance(latUser, longUser, latInformante, longInformante);
        }

        Log.d("DISTANCIA ", String.valueOf(distancia));

        float valueSeekFloat = valueSeek / 1000.0f;

        Log.d("VALUE SEEK FLOAT", String.valueOf(valueSeekFloat));

        if(!checkPesquisaLocal()){
            if(distancia != 0.0){
                if(distancia > valueSeekFloat){
                    if(statusGps){
                        notifyUsuario();
                    }
                }
            }
        }

    }

    private boolean checkPesquisaLocal(){

        // IPEAD Coordinates;
        double latIpead = -19.867089;
        double longIpead = -43.962714;

        GPSTracker gps = new GPSTracker(GrupoActivity.this);

        double distancia;
        double latUser = gps.getLatitude();
        double longUser = gps.getLongitude();

        distancia = distance(latUser, longUser, latIpead, longIpead);

        // Check if is 200 meters radius from IPEAD
        if(distancia < 0.2){
            return true;
        } else {
            return false;
        }

    }

    private void notifyUsuario() {

        new MaterialDialog.Builder(GrupoActivity.this)
                .title("Aviso")
                .content("Você está longe de " + listInfo.get(0).getDescricao() + ". Deseja continuar ?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();

    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; //Earth Radius

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        int pos = SharedPref.readInt("positionGrupo", 0);

        manager.scrollToPositionWithOffset(pos, 12);
        SharedPref.writeBoolean("restart", true);

        load();

    }


}
