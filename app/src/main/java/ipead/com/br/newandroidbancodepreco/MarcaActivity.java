package ipead.com.br.newandroidbancodepreco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.adapter.RecycleMarcaAdapter;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.MarcaDAO;

public class MarcaActivity extends AppCompatActivity {

    private int idInformante;
    private int idGrupo;
    private int idPeriodoColeta;
    private int tipoInformante;
    private int idProduto;
    private String filepath;
    private RecyclerView recycleList;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        Bundle b = getIntent().getExtras();

        recycleList = findViewById(R.id.recycleViewer);
        recycleList.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        idInformante = b.getInt("idInfo");
        idGrupo = b.getInt("idGrupo");
        idProduto = Integer.parseInt(b.getString("idProduto"));
        idPeriodoColeta = b.getInt("idPeriodo");
        tipoInformante = b.getInt("tipo");
        filepath = b.getString("filepath");
        SharedPref.init(MarcaActivity.this);

        dividerItemDecoration = new DividerItemDecoration(recycleList.getContext(), manager.getOrientation());
        recycleList.removeItemDecoration(dividerItemDecoration);
        recycleList.addItemDecoration(dividerItemDecoration);

        load();

    }

    public void load(){

        MarcaDAO marcaDAO = new MarcaDAO(filepath);
        List<HashMap<String, String>> marcas = marcaDAO.selectMarcas(String.valueOf(idInformante), String.valueOf(idProduto));

        if(marcas == null || marcas.size() == 0){

            Toast.makeText(getApplicationContext(), "Sem Marcas!", Toast.LENGTH_SHORT).show();

        } else {

            RecycleMarcaAdapter marcaRecycleAdapter = new RecycleMarcaAdapter(MarcaActivity.this, marcas,
                    idInformante, idGrupo, idProduto, idPeriodoColeta, tipoInformante, filepath);
            recycleList.setAdapter(marcaRecycleAdapter);

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        manager = new LinearLayoutManager(this);
        recycleList.setLayoutManager(manager);

        int pos = SharedPref.readInt("posMarca", 0);

        manager.scrollToPositionWithOffset(pos, 12);
        SharedPref.writeBoolean("restart", true);

        load();
    }
}
