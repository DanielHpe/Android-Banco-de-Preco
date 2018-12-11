package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.HashMap;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.adapter.RecycleProdutoAdapter;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ProdutoDAO;

public class ProdutoActivity extends AppCompatActivity {

    private int idInformante;
    private int idGrupo;
    private int idPeriodoColeta;
    private int tipoInformante;
    private String filepath;
    private RecyclerView recycleList;
    private LinearLayoutManager manager;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        manager = new LinearLayoutManager(this);
        Bundle b = getIntent().getExtras();

        recycleList = findViewById(R.id.recycleViewer);
        recycleList.setHasFixedSize(true);
        recycleList.setLayoutManager(manager);

        idInformante = b.getInt("idInfo");
        idGrupo = Integer.parseInt(b.getString("idGrupo"));
        idPeriodoColeta = b.getInt("idPeriodo");
        tipoInformante = b.getInt("tipo");
        filepath = b.getString("filepath");

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        load();
    }

    private void load() {

        ProdutoDAO produtoDAO = new ProdutoDAO(filepath, getApplicationContext());
        List<HashMap<String, String>> produtos = produtoDAO.selectProduto(String.valueOf(idInformante), String.valueOf(idGrupo));

        if(produtos == null || produtos.size() == 0){

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem produtos no dispositivo!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ProdutoActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            RecycleProdutoAdapter produtoRecycleAdapter = new RecycleProdutoAdapter(ProdutoActivity.this, produtos,
                    idInformante, idGrupo, idPeriodoColeta, tipoInformante, filepath);
            recycleList.setAdapter(produtoRecycleAdapter);

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        int pos = SharedPref.readInt("posProduto", 0);
        manager.scrollToPositionWithOffset(pos, 12);

        SharedPref.writeBoolean("restart", true);

        load();

    }

}
