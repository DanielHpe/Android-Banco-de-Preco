package ipead.com.br.newandroidbancodepreco;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.entity.Usuario;

public class PathActivity extends AppCompatActivity implements UsuarioDAO.ListUsuariosListener {

    private TextView currentPath;
    private TextView currentSdPath;
    private EditText novoPath;
    private Button salvarCaminho;
    private ImageView imageButton;
    private ImageView imageButtonSd;
    public DirectoryBrowser browser;
    private int REQUEST_PERMISSION_CODE = 1;
    private int PERMISSION_ALL = 1;
    private List<Usuario> usuarios;
    private UsuarioDAO dao;
    private ConfiguracaoDAO configuracaoDAO;
    private SweetAlertDialog progress;
    private String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        SharedPref.init(getApplicationContext());

        salvarCaminho = findViewById(R.id.btnCaminhoSistema);
        imageButton = findViewById(R.id.imageButton);
        imageButtonSd = findViewById(R.id.imageButtonSd);
        novoPath = findViewById(R.id.editCaminhoSistema);
        currentPath = findViewById(R.id.currentPath);
        currentSdPath = findViewById(R.id.currentSdPath);
        browser = new DirectoryBrowser(getApplicationContext());

        if(browser.isSdDirectoryExists()){
            novoPath.setHint("Sdcard disponível! Copie-o abaixo!");
            currentSdPath.setText(browser.sdCardPath() + "/");
        } else {
            currentSdPath.setText("Sdcard não encontrado!");
            currentSdPath.setTextColor(android.graphics.Color.rgb(255, 0, 0));
        }

        salvarCaminho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasPermissions(PathActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(PathActivity.this, PERMISSIONS, PERMISSION_ALL);
                }

                if(hasPermissions(PathActivity.this, PERMISSIONS)){
                    setPath();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novoPath.setText(currentPath.getText().toString().replace("/sistemas", ""));
                Toast.makeText(PathActivity.this, "Copiado para área de transferência!", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonSd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentSdPath.getText().toString().equals("Sdcard não encontrado!")){
                    novoPath.setText(currentSdPath.getText().toString().replace("/sistemas", ""));
                    Toast.makeText(PathActivity.this, "Copiado para área de transferência!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void syncUsuarios() {

        if(Conexao.verificaConexao(this)) {

            progress = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progress.setTitleText("Sincronizando...");
            progress.setCancelable(true);
            progress.show();

            configuracaoDAO.verifyTables();

            dao = new UsuarioDAO(this);
            dao.setListUsuariosListener(this);
            dao.getListUsuario();

        } else {

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Sem conexão!")
                    .setContentText("Ligue o Wifi ou os dados móveis!")
                    .setConfirmText("OK").show();
        }

    }

    public void setPath(){
        SharedPref.writeString("path", novoPath.getText().toString());
        Toast.makeText(getApplicationContext(), "Caminho alterado com sucesso!", Toast.LENGTH_SHORT).show();
        criarEstruturaPastas();
        configuracaoDAO = new ConfiguracaoDAO(this, browser.dirBancoDados() + ConfiguracaoDAO.DBNAME);
        syncUsuarios();
    }


    /**
     * Pastas aonde os arquivos serão armazenados
     *
     */
    private void criarEstruturaPastas() {

        DirectoryBrowser browser = new DirectoryBrowser(getApplicationContext());

        browser.chmod777(Environment.getExternalStorageDirectory().getAbsolutePath());
        browser.criarPasta(browser.dirSistemas());
        browser.criarPasta(browser.dir());
        browser.criarPasta(browser.dirTemp());
        browser.criarPasta(browser.dirError());
        browser.criarPasta(browser.dirF());
        browser.criarPasta(browser.dirAtrasado());
        browser.criarPasta(browser.dirBancoDados());

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setPath();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentPath.setText(browser.standardPath());
        novoPath.setText(browser.dirSistemas());

    }

    @Override
    public void listUsuariosReady(List<Usuario> list) {
        Dialog dialog;

        if(list != null) {
            usuarios = list;
            if(usuarios.size() > 0) {
                dao.deleteAllFromUsuarios();

                for (Usuario usuario: usuarios) {
                    dao.inserirUsuario(usuario);
                }

                if(ConfiguracaoDAO.hasConfig(PathActivity.this)) {
                    String login = configuracaoDAO.getLoginFromInformante();
                    String senha = dao.getSenhaUsuario(login);

                    configuracaoDAO.syncConfiguracao(login, senha);
                }

                dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso!")
                        .setContentText("Usuários sincronizados!")
                        .setConfirmText("OK");

            } else {

                dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Nenhum usuário encontrado!")
                        .setContentText("A requisição não trouxe nenhum usuário!")
                        .setConfirmText("OK");
            }

        } else {

            dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Falha ao buscar dados!")
                    .setContentText("Tente novamente mais tarde!")
                    .setConfirmText("OK");
        }

        configuracaoDAO.close();
        progress.dismissWithAnimation();
        dialog.show();

    }
}
