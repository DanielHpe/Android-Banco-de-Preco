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
import ipead.com.br.newandroidbancodepreco.adapter.RecycleInformantesFechadosAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.entity.Informante;

public class ReabrirActivity extends AppCompatActivity {

    private int idUsuario;
    private RecyclerView recycleList;
    private DirectoryBrowser browser;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        Bundle b = getIntent().getExtras();
        idUsuario = b.getInt("idUsuario");

        browser = new DirectoryBrowser(getApplicationContext());
        manager = new LinearLayoutManager(this);

        recycleList =  findViewById(R.id.recycleViewer);
        recycleList.setHasFixedSize(true);
        recycleList.setLayoutManager(manager);

        String mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        InformanteDAO informanteDAO = new InformanteDAO(getApplicationContext(), null, mainPath);
        List<Informante> listInformantes = informanteDAO.reabrirInformante();

        load(listInformantes);

    }

    private void load(List<Informante> listInformantes){

        if(listInformantes == null || listInformantes.size() == 0) {

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem informantes fechados ou transferidos!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ReabrirActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            RecycleInformantesFechadosAdapter recycleInformantesFechadosAdapter =
                    new RecycleInformantesFechadosAdapter(ReabrirActivity.this, listInformantes, idUsuario);
            recycleList.setAdapter(recycleInformantesFechadosAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(),
                    manager.getOrientation());
            recycleList.addItemDecoration(dividerItemDecoration);

        }

    }
}
