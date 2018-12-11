package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.io.File;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.entity.Usuario;
import ipead.com.br.newandroidbancodepreco.service.Alarm;

public class MainActivity extends AppCompatActivity implements UsuarioDAO.ListUsuariosListener {

    private TextView txtUser;
    private TextView currentUser;
    private EditText txtLogin;
    private EditText txtSenha;
    private Button btnLogin;
    private ImageButton imgBtnInfo;
    private ImageButton imgBtnSync;
    private ConfiguracaoDAO configuracaoDAO;
    private List<Usuario> usuarios;
    private SweetAlertDialog progress;
    private UsuarioDAO dao;
    public DirectoryBrowser browser;
    private File[] filesBancoDados;
    private File[] filesSistema;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLogin = findViewById(R.id.txtLogin);
        txtSenha = findViewById(R.id.txtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        imgBtnSync = findViewById(R.id.imgBtnSync);
        imgBtnInfo = findViewById(R.id.imgBtnInfo);
        txtUser = findViewById(R.id.txtUser);
        currentUser = findViewById(R.id.currentUser);
        browser = new DirectoryBrowser(getApplicationContext());
        SharedPref.init(getApplicationContext());

        imgBtnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filesSistema != null){
                    onResume();
                    syncUsuario();
                } else {
                    showWarning("Aviso", "Defina as configurações de armazenamento");
                }
            }
        });

        imgBtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(browser.exists(filesBancoDados, ConfiguracaoDAO.DBNAME)){
                    if(dao.checkIfExistsDataInTable()){
                        logarUsuario();
                    } else {
                        logarAdmin();
                    }
                } else {
                    logarAdmin();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        filesBancoDados = new File(browser.dirBancoDados()).listFiles();
        filesSistema = new File(browser.dirSistemas()).listFiles();

        if(browser.exists(filesBancoDados, ConfiguracaoDAO.DBNAME)){
            configuracaoDAO = new ConfiguracaoDAO(this, path);
            dao = new UsuarioDAO(getApplicationContext());
            if(configuracaoDAO.checkIfExistsDataInTable()){
                if(!verificarIfExistsColetor()){
                    txtUser.setText("");
                    currentUser.setText("");
                } else {
                    if (dao.checkIfExistsDataInTable()) {
                        verificarColetorAtual();
                    }
                }
            }
        }

        //Bloqueia acesso aos finais de semana
        if(Alarm.isWeekend()) {
            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Acesso Negado!")
                    .setContentText("Acesso permitido somente em dias de semana!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    MainActivity.this.finish();
                }
            });

            dialog.show();
        }

    }

    private void verificarColetorAtual() {

        String loginAtual = SharedPref.readString("login", "");
        String nomeUsuarioAtual;

        if(loginAtual.equals("")){
            txtUser.setText("");
            nomeUsuarioAtual = "";
        } else {
            if(dao.isGerenteOrAdmin(loginAtual)){
                txtUser.setText("");
                nomeUsuarioAtual = "";
            } else {
                txtUser.setText("Rota em andamento para: ");
                nomeUsuarioAtual = dao.getNomeUsuario(loginAtual);
            }
        }

        currentUser.setText(nomeUsuarioAtual);
        currentUser.setTextColor(android.graphics.Color.rgb(21, 160, 51));
    }

    /**
     * Método que sincroniza os usuários
     * Ou seja, é feita a conexão com o servidor para ver se a lista de usuários foi atualizada
     *
     */
    private void syncUsuario() {

        if(Conexao.verificaConexao(this)) {
            progress = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progress.setTitleText("Sincronizando...");
            progress.setCancelable(true);
            progress.show();
            imgBtnSync.setEnabled(false);
            addHandler();

            configuracaoDAO.verifyTables();

            dao.setListUsuariosListener(this);
            dao.getListUsuario();
        } else {
            showWarning("Sem conexão!", "Ligue o Wifi ou os dados móveis!");
        }

    }

    private void logarAdmin() {

        String login = txtLogin.getText().toString().trim();
        String senha = txtSenha.getText().toString().trim();

        if(login.equals("admin") && senha.equals("1p3ad")){
            Intent i = new Intent(MainActivity.this, ManagerActivity.class);
            i.putExtra("login", login);
            i.putExtra("senha", senha);
            startActivity(i);
            txtLogin.requestFocus();
        } else {
            txtSenha.setText("");
            showWarning("Atenção", "Sincronize usuários antes de logar!");
        }
    }


    public void addHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                imgBtnSync.setEnabled(true);
            }
        }, 1000);
    }

    //Método para logar o usuário no sistema
    public void logarUsuario(){

        String login = txtLogin.getText().toString().trim();
        String senha = txtSenha.getText().toString().trim();

        if (login.equals("")) {
            txtLogin.setError("Campo Obrigatório!");
            txtLogin.requestFocus();
        } else if(senha.equals("")){
            txtSenha.setError("Campo Obrigatório!");
            txtSenha.requestFocus();
        } else {
            try{
                String retorno = dao.isLogin(login, senha);

                if (retorno.equals("true")) {
                    Intent i = new Intent(MainActivity.this, MainMenuActivity.class);
                    i.putExtra("login", login);
                    i.putExtra("senha", senha);
                    startActivity(i);
                    txtLogin.requestFocus();
                } else {
                    if(retorno.equals("usr false")){
                        showError("Erro","Login ou senha inválidos" );
                        txtSenha.setText("");
                    } else if(dao.getIdGrupoUsuario(login).equals("1") || dao.getIdGrupoUsuario(login).equals("2")
                            || dao.getIdGrupoUsuario(login).equals("4")){
                        if(dao.validarLogin(login, senha)){
                            Intent i = new Intent(MainActivity.this, MainMenuActivity.class);
                            i.putExtra("login", login);
                            i.putExtra("senha", senha);
                            startActivity(i);
                            txtLogin.requestFocus();
                        } else {
                            showError("Erro","Login ou senha inválidos" );
                            txtSenha.setText("");
                        }
                    } else {
                        if(dao.validarLogin(login, senha)){
                            String nome = retorno.replace("false", "");
                            showWarning("Aviso", "Somente o usuario " + nome + "" +
                                    " pode entrar até o término da rota");
                            txtSenha.setText("");
                        } else {
                            showError("Erro","Login ou senha inválidos" );
                            txtSenha.setText("");
                        }
                    }
                }
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        txtLogin.setText("");
        txtSenha.setText("");

        if(browser.exists(filesBancoDados, ConfiguracaoDAO.DBNAME)){
            dao = new UsuarioDAO(getApplicationContext());
            configuracaoDAO = new ConfiguracaoDAO(this, path);
            if(configuracaoDAO.checkIfExistsDataInTable()){
                if(verificarIfExistsColetor()){

                }
            } else {
                txtUser.setText("");
                currentUser.setText("");
            }

            if (dao.checkIfExistsDataInTable()) {
                verificarColetorAtual();
            }
        }

    }

    private boolean verificarIfExistsColetor() {

        if(!configuracaoDAO.hasConfig(MainActivity.this)){
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //Prepara a lista de usuários para a sincronização (Atualização)
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

                if(ConfiguracaoDAO.hasConfig(MainActivity.this)) {
                    String login = configuracaoDAO.getLoginFromInformante();
                    String senha = dao.getSenhaUsuario(login);
                    configuracaoDAO.syncConfiguracao(login, senha);
                }

                dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso")
                        .setContentText("Usuários sincronizados!")
                        .setConfirmText("OK");

                txtLogin.requestFocus();

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

        progress.dismissWithAnimation();
        dialog.show();
    }


    private void showWarning(String title, String mensagem){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(mensagem)
                .setConfirmText("OK")
                .show();

    }

    private void showError(String title, String mensagem){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(mensagem)
                .setConfirmText("OK")
                .show();

    }

}
