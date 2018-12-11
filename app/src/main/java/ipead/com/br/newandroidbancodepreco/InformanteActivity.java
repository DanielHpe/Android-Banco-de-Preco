package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import ipead.com.br.newandroidbancodepreco.adapter.RecycleInformanteAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import ipead.com.br.newandroidbancodepreco.entity.Informante;

public class InformanteActivity extends AppCompatActivity {

    private int idRota;
    private int idPeriodoColeta;
    private String mainPath;
    private String login;
    private String senha;
    private RecycleInformanteAdapter recycleInformanteAdapter;
    private RecyclerView recycleList;
    private List<Informante> listInfo;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        Bundle b = getIntent().getExtras();
        SharedPref.init(getApplicationContext());

        idRota = b.getInt("idRota");
        idPeriodoColeta = b.getInt("idPeriodo");

        login = b.getString("login");
        senha = b.getString("senha");
        mainPath = b.getString("mainPath");

        Log.d("MAIN PATH", mainPath);

        recycleList = findViewById(R.id.recycleViewer);
        recycleList.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        InformanteDAO informanteDAO = new InformanteDAO(getApplicationContext(), null, mainPath);

        listInfo = informanteDAO.getListInformanteByRota(String.valueOf(idRota),
                String.valueOf(idPeriodoColeta));

        load();
    }

    public void load(){

        if(listInfo == null || listInfo.size() == 0) {

            excluirRota();

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem informantes no dispositivo!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    InformanteActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            recycleInformanteAdapter = new RecycleInformanteAdapter(InformanteActivity.this,
                    listInfo, idRota, idPeriodoColeta, login, senha, mainPath);
            recycleList.setAdapter(recycleInformanteAdapter);

        }

    }

    private void excluirRota(){

        InformanteDAO informanteDAO = new InformanteDAO(InformanteActivity.this, null, mainPath);
        int numberInfoTransf = informanteDAO.numberInformanteTransferido(String.valueOf(idRota),
                String.valueOf(idPeriodoColeta));
        int numberInfoTotal = informanteDAO.getListInformanteByRotaTransferidos(String.valueOf(idRota),
                String.valueOf(idPeriodoColeta)).size();
        Log.d("Transferido|Total", String.valueOf(numberInfoTransf) + ", " +  String.valueOf(numberInfoTotal));

        if(numberInfoTransf == numberInfoTotal){
            ConfiguracaoDAO configuracaoDAO = new ConfiguracaoDAO(InformanteActivity.this, mainPath);
            configuracaoDAO.excluirRota(idRota, idPeriodoColeta);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        int pos = SharedPref.readInt("pos", 0);

        manager.scrollToPositionWithOffset(pos, 12);
        SharedPref.writeBoolean("restart", true);

        load();

    }
}
