package ipead.com.br.newandroidbancodepreco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;

public class AdminMenuActivity extends AppCompatActivity {

    public String login;
    public String senha;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Bundle b = getIntent().getExtras();

        login = b.getString("login");
        senha = b.getString("senha");
        UsuarioDAO dao = new UsuarioDAO(AdminMenuActivity.this);
        idUsuario = Integer.parseInt(dao.getIdUsuario(login));

        Button btnReabrirExcluir = findViewById(R.id.btnReabrirExcluirInformante);
        Button btnExcluir = findViewById(R.id.btnExcluirRota);

        btnReabrirExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenuActivity.this, ReabrirActivity.class);
                i.putExtra("idUsuario", idUsuario);
                startActivity(i);
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AdminMenuActivity.this, ExcluirRotaActivity.class);
                i.putExtra("login", login);
                i.putExtra("senha", senha);
                i.putExtra("idUsuario", idUsuario);
                startActivity(i);

            }
        });

    }
}
