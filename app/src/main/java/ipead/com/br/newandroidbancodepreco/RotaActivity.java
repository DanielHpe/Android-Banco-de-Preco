package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.adapter.RecycleRotaAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

public class RotaActivity extends AppCompatActivity {

    public DirectoryBrowser browser;
    public String login;
    public String senha;
    private int idUsuario;
    private String mainPath;
    private LinearLayoutManager manager;
    private RecycleRotaAdapter recycleRotaAdapter;
    private RecyclerView recycleList;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        SharedPref.init(getApplicationContext());
        browser = new DirectoryBrowser(getApplicationContext());

        Bundle b = getIntent().getExtras();
        idUsuario = getIntent().getExtras().getInt("idUsuario");
        login = b.getString("login");
        senha = b.getString("senha");

        recycleList = findViewById(R.id.recycleViewer);
        recycleList.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        onLoad();
    }

    /**
     * Metodo para reuso no carregamento primario
     */
    private void onLoad(){

        ConfiguracaoDAO confDAO = new ConfiguracaoDAO(getApplicationContext(), mainPath);
        List<Configuracao> listConfig = confDAO.getListConfiguracaoLocal();

        if(listConfig == null || listConfig.size() == 0) {

            SharedPref.writeString("login", "");

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem rotas no dispositivo!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    RotaActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            recycleRotaAdapter = new RecycleRotaAdapter(RotaActivity.this, listConfig, login, senha, mainPath);
            recycleList.setAdapter(recycleRotaAdapter);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        int pos = SharedPref.readInt("posRota", 0);

        manager.scrollToPositionWithOffset(pos, 12);
        SharedPref.writeBoolean("restart", true);

        onLoad();
    }
}
