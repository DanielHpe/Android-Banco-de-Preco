package ipead.com.br.newandroidbancodepreco;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConexaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.HostDAO;
import ipead.com.br.newandroidbancodepreco.entity.Host;

public class InfoActivity extends AppCompatActivity implements ConexaoDAO.StatusConexaoListener {

    public static final String VERSION = "1.1.8";
    private TextView versao;
    private TextView conStatus;
    private TextView host;
    private Button btnAtualizar;
    private ConexaoDAO daoCon;
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        SharedPref.init(getApplicationContext());

        versao = findViewById(R.id.lblInfoVersaoValue);
        conStatus = findViewById(R.id.lblInfoConStatusValue);
        host = findViewById(R.id.lblInfoHostValue);
        btnAtualizar = findViewById(R.id.btnAtualizarInfo);

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoad();
            }
        });

        onLoad();
    }

    private void onLoad() {

        daoCon = new ConexaoDAO(this, this);
        HostDAO hostDAO = new HostDAO(InfoActivity.this);

        List<Host> hosts = hostDAO.selectHosts();

        String nomeElemento = SharedPref.readString("nomeHost", "Produção");

        for(int i = 0; i < hosts.size(); i++){
            if(hosts.get(i).getNomeHost().equals(nomeElemento)){
                host.setText(nomeElemento);
            }
        }

        versao.setText(VERSION);

        if(Conexao.verificaConexao(this)) {
            daoCon.getStatusConexao();
        } else {
            conStatus.setText("Sem Internet");
            conStatus.setTextColor(android.graphics.Color.rgb(100, 100, 100));
        }
    }

    @Override
    public void statusConexaoReady(Conexao con) {
       // Log.i("Status: ", con.getStatusConexao());
        this.conexao = con;
        String status;
        Log.i("Status: ", conexao.getStatusConexao());

        if (conexao != null) {
            status = conexao.getStatusConexao();
        } else {
            status = "Offline";
        }

        if(status != null && status.equalsIgnoreCase("Online"))
            conStatus.setTextColor(android.graphics.Color.rgb(21, 160, 51));
        else
            conStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));

        conStatus.setText(status);
    }
}
