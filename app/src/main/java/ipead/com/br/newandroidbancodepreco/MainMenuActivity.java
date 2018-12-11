package ipead.com.br.newandroidbancodepreco;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.adapter.MenuAdapter;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;

public class MainMenuActivity extends AppCompatActivity {

    List<MenuAdapter.CustomMenuItem> menuItems;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_menu);

        b = getIntent().getExtras();

        initMenu();

        MenuAdapter adapter = new MenuAdapter(this, menuItems);

        ListView  gridView = findViewById(R.id.gridMenu);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DirectoryBrowser browser = new DirectoryBrowser(getApplicationContext());
                browser.criarPasta(browser.dirTemp());
                File[] filesTemp = new File(browser.dirTemp()).listFiles();

                 if (filesTemp.length > 1) {
                     if( menuItems.get(i).title.equals("Rotas p/ Download") ||
                             menuItems.get(i).title.equals("Rotas Locais")
                             || menuItems.get(i).title.equals("Coleta")){

                         showDialog();
                     } else {
                         startActivity(menuItems.get(i).intent);
                     }
                 } else {
                     startActivity(menuItems.get(i).intent);
                 }
            }
        });
    }

    private void initMenu() {

        menuItems = new ArrayList<>();

        String idGrupo = getGrupoFromUsuario();

        switch (idGrupo){
            case "1":
                menuItems.add(new MenuAdapter.CustomMenuItem("Coleta", R.drawable.ic_assignment_36dp, new Intent(MainMenuActivity.this, RotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas p/ Download", R.drawable.ic_assignment_returned_36dp, new Intent(MainMenuActivity.this, DownloadRotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas Locais", R.drawable.ic_call_split_black_36dp, new Intent(MainMenuActivity.this, SwipeActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Gerente", R.drawable.ic_assignment_ind_36dp, new Intent(MainMenuActivity.this, AdminMenuActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Administrador", R.drawable.ic_account_circle_black_36dp, new Intent(MainMenuActivity.this, ManagerActivity.class)));
                menuItems.add(new MenuAdapter.CustomMenuItem("Informações", R.drawable.ic_help_36dp, new Intent(MainMenuActivity.this, InfoActivity.class)));
                break;
            case "2":
                menuItems.add(new MenuAdapter.CustomMenuItem("Coleta", R.drawable.ic_assignment_36dp, new Intent(MainMenuActivity.this, RotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas p/ Download", R.drawable.ic_assignment_returned_36dp, new Intent(MainMenuActivity.this, DownloadRotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas Locais", R.drawable.ic_call_split_black_36dp, new Intent(MainMenuActivity.this, SwipeActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Gerente", R.drawable.ic_assignment_ind_36dp, new Intent(MainMenuActivity.this, AdminMenuActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Informações", R.drawable.ic_help_36dp, new Intent(MainMenuActivity.this, InfoActivity.class)));
                break;
            case "3":
                break;
            case "4":
                menuItems.add(new MenuAdapter.CustomMenuItem("Coleta", R.drawable.ic_assignment_36dp, new Intent(MainMenuActivity.this, RotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas p/ Download", R.drawable.ic_assignment_returned_36dp, new Intent(MainMenuActivity.this, DownloadRotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas Locais", R.drawable.ic_call_split_black_36dp, new Intent(MainMenuActivity.this, SwipeActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Gerente", R.drawable.ic_assignment_ind_36dp, new Intent(MainMenuActivity.this, AdminMenuActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Informações", R.drawable.ic_help_36dp, new Intent(MainMenuActivity.this, InfoActivity.class)));
                break;
            case "5":
                menuItems.add(new MenuAdapter.CustomMenuItem("Coleta", R.drawable.ic_assignment_36dp, new Intent(MainMenuActivity.this, RotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas p/ Download", R.drawable.ic_assignment_returned_36dp, new Intent(MainMenuActivity.this, DownloadRotaActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Rotas Locais", R.drawable.ic_call_split_black_36dp, new Intent(MainMenuActivity.this, SwipeActivity.class)
                        .putExtra("login", b.getString("login")).putExtra("senha", b.getString("senha"))));
                menuItems.add(new MenuAdapter.CustomMenuItem("Informações", R.drawable.ic_help_36dp, new Intent(MainMenuActivity.this, InfoActivity.class)));
                break;
            default:
                break;

        }

    }

    public String getGrupoFromUsuario(){
        UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
        String idGrupoUsuario = usuarioDAO.getIdGrupoUsuario(b.getString("login"));
        return idGrupoUsuario;
    }

    public void showDialog(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Aviso")
                .setContentText("Rota em transferência para o dispostivo! Aguarde!")
                .show();
    }

    @Override
    public void onBackPressed() {

        new MaterialDialog.Builder(MainMenuActivity.this)
                .title("Aviso")
                .content("Deseja realmente sair?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                        Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
