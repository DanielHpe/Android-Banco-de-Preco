package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import ipead.com.br.newandroidbancodepreco.adapter.RecycleExcluirRotaAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

public class ExcluirRotaActivity extends AppCompatActivity {

    private List<Configuracao> listConfig;
    private ConfiguracaoDAO confDAO;
    public DirectoryBrowser browser;
    public String login;
    public String senha;
    private RecycleExcluirRotaAdapter adapter;
    private RecyclerView recycleList;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        Bundle b = getIntent().getExtras();

        browser = new DirectoryBrowser(getApplicationContext());
        SharedPref.init(ExcluirRotaActivity.this);

        login = b.getString("login");
        senha = b.getString("senha");

        recycleList = findViewById(R.id.recycleViewer);

        recycleList.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        load(path);

    }

    private void load(String path){

        confDAO = new ConfiguracaoDAO(getApplicationContext(), path);
        listConfig = confDAO.getListConfiguracaoLocal();

        if(listConfig == null || listConfig.size() == 0) {

            SharedPref.writeString("login", "");

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem rotas transferidas no dispositivo!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ExcluirRotaActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            adapter = new RecycleExcluirRotaAdapter(ExcluirRotaActivity.this, listConfig);
            recycleList.setAdapter(adapter);

        }

        confDAO.close();

    }

}
