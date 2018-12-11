package ipead.com.br.newandroidbancodepreco;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.HostDAO;
import ipead.com.br.newandroidbancodepreco.entity.Host;

public class HostActivity extends AppCompatActivity {

    private FloatingActionButton fabButton;
    private List<Host> hosts;
    private String hostName;
    private int cont = 0;
    public RadioGroup radioGroup;
    public RadioButton radioButton;
    private HostDAO hostDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_host_view);

        SharedPref.init(getApplicationContext());
        hostName = SharedPref.readString("nomeHost","");
        fabButton = findViewById(R.id.fab);
        radioGroup = findViewById(R.id.radioGroup);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogHost();
            }
        });

        hostDAO = new HostDAO(HostActivity.this);
        hosts = hostDAO.selectHosts();
        if(hosts.size() <= 1 || hosts == null){
            hostDAO.insertExistentes();
        }
        hosts = hostDAO.selectHosts();
        load();
    }

    @SuppressLint("SetTextI18n")
    public void load(){
        int pos = 0;
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearParams.setMargins(50, 30, 0, 0);

        for (cont = 0; cont < hosts.size(); cont++) {
            radioButton = new RadioButton(getApplicationContext());
            radioButton.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            radioButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            radioButton.setTextSize(2,20);

            radioButton.setId(cont);
            radioButton.setText(hosts.get(cont).getNomeHost().substring(0, 1).toUpperCase()
                    + hosts.get(cont).getNomeHost().substring(1).toLowerCase());
            if(hosts.get(cont).getNomeHost().equals(hostName)){
                pos = cont;
                hosts.get(cont).setMarked(true);
                Log.d("Elemento", hostName);
            }

            radioButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("Elemento", "-------------------------------");
                    Log.d("Elemento ONE CLICK: " + hosts.get(v.getId()).getNomeHost(), String.valueOf(hosts.get(v.getId()).isMarked()));
                    Log.d("Elemento", "-------------------------------");
                    for(int i = 0; i < hosts.size(); i++){
                        if(!hosts.get(i).isMarked()){
                            hosts.get(i).setMarked(false);
                        }
                        Log.d("Elemento ON CLICK: " + hosts.get(i).getNomeHost(), String.valueOf(hosts.get(i).isMarked()));
                    }
                    editarHost(v.getId());
                    return false;
                }
            });

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPref.writeInt("radioButtonId", v.getId());
                    SharedPref.writeBoolean("isChecked", radioButton.isChecked());
                    SharedPref.writeString("nomeHost", hosts.get(v.getId()).getNomeHost());
                    SharedPref.writeString("ipHost", hosts.get(v.getId()).getIdHost());
                    hosts.get(v.getId()).setMarked(true);
                    for(int i = 0; i < hosts.size(); i++){
                        if(!hosts.get(i).getNomeHost().equals(hosts.get(v.getId()).getNomeHost())){
                            hosts.get(i).setMarked(false);
                        }
                    }
                }
            });

            radioGroup.addView(radioButton, linearParams);
        }
        radioGroup.clearCheck();
        radioGroup.check(pos);
    }

    private void editarHost(final int id) {
        Button atualizarHost;
        Button deletarHost;

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(HostActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_update_delete_host, null);

        atualizarHost = mView.findViewById(R.id.btnAtualizarHost);
        deletarHost = mView.findViewById(R.id.btnDeletarHost);

        myDialog.setView(mView);
        final AlertDialog d = myDialog.create();
        d.show();

        atualizarHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (hosts.get(id).getNomeHost()){
                    case "Produção":
                        updateHostPadrao(hosts.get(id).getNomeHost(), id, d);
                        break;
                    case "Desenvolvimento":
                        updateHostPadrao(hosts.get(id).getNomeHost(), id, d);
                        break;
                    default:
                        updateHost(id, d);
                        break;
                }
            }
        });

        deletarHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (hosts.get(id).getNomeHost()){
                    case "Produção":
                        showAlertEnderecoPadrao(hosts.get(id).getNomeHost(), d);
                        break;
                    case "Desenvolvimento":
                        showAlertEnderecoPadrao(hosts.get(id).getNomeHost(), d);
                        break;
                    default:
                        deleteHost(id, d);
                        break;
                }
            }
        });
    }

    private void updateHostPadrao(final String nome, final int id, final AlertDialog d){
        Button attHost;
        Button closeJanela;
        final EditText novoIpHost;

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(HostActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_update_ip_host, null);

        attHost = mView.findViewById(R.id.btnAttHost);
        closeJanela = mView.findViewById(R.id.btnCloseHostUpdate);
        novoIpHost = mView.findViewById(R.id.editIPHostUpdate);
        novoIpHost.setText(hosts.get(id).getIdHost());

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        dialog.show();

        attHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String novoIp = novoIpHost.getText().toString();
                updatePadrao(nome, novoIp, id, d);
            }
        });

        closeJanela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                dialog.dismiss();
            }
        });

    }

    private void updateHost(final int id, final AlertDialog d){
        Button attHost;
        Button closeJanela;
        final EditText novoNomeHost;
        final EditText novoIpHost;

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(HostActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_update_host, null);

        attHost = mView.findViewById(R.id.btnAttHost);
        closeJanela = mView.findViewById(R.id.btnCloseHostUpdate);
        novoNomeHost = mView.findViewById(R.id.editNomeHostUpdate);
        novoIpHost = mView.findViewById(R.id.editIPHostUpdate);

        novoNomeHost.setText(hosts.get(id).getNomeHost());
        novoIpHost.setText(hosts.get(id).getIdHost());

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        dialog.show();

        attHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String novoNome = novoNomeHost.getText().toString();
                final String novoIp = novoIpHost.getText().toString();
                update(novoNome, novoIp, id, d);
            }
        });

        closeJanela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                dialog.dismiss();
            }
        });

    }

    private void updatePadrao(String nome, String novoIp, int id, AlertDialog d){
        if(novoIp.equals("")){
            Toast.makeText(HostActivity.this, "Digite um IP de host!",
                    Toast.LENGTH_SHORT).show();
        } else if(hosts.get(id).getIdHost().equalsIgnoreCase(novoIp)){
            new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Digite um novo IP!")
                    .setConfirmText("OK")
                    .show();
        } else {
            if(hostDAO.update(nome, novoIp, hosts.get(id).getID())){
                new SweetAlertDialog(HostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso")
                        .setContentText("Host atualizado com sucesso!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                                startActivity(getIntent());
                            }
                        }).show();
            } else {
                Dialog dialog = new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Aviso!")
                        .setContentText("Não foi possível atualizar o Host selecionado!")
                        .setConfirmText("OK");
                d.dismiss();
                dialog.show();
            }
            if(hosts.get(id).isMarked()){
                SharedPref.writeString("nomeHost", nome);
                SharedPref.writeString("ipHost", novoIp);
            }
        }
    }

    private void update(String novoNome, String novoIp, int id, AlertDialog d) {
        if(novoNome.equals("") || novoIp.equals("")){
            Toast.makeText(HostActivity.this, "Digite um Nome e um IP de host!",
                    Toast.LENGTH_SHORT).show();
        } else if(novoNome.length() < 3){
            new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("O nome de Host deve ter no mínimo 3 letras!")
                    .setConfirmText("OK")
                    .show();
        } else if(hosts.get(id).getNomeHost().equalsIgnoreCase(novoNome)
                && hosts.get(id).getIdHost().equalsIgnoreCase(novoIp)){
            new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Digite um novo nome ou um novo IP!")
                    .setConfirmText("OK")
                    .show();
        } else {
            if(hostDAO.update(novoNome, novoIp, hosts.get(id).getID())){
                new SweetAlertDialog(HostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso")
                        .setContentText("Host atualizado com sucesso!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                                startActivity(getIntent());
                            }
                        }).show();
            } else {
                Dialog dialog = new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Aviso!")
                        .setContentText("Não foi possível atualizar o Host selecionado!")
                        .setConfirmText("OK");
                d.dismiss();
                dialog.show();
            }
            if(hosts.get(id).isMarked()){
                SharedPref.writeString("nomeHost", novoNome);
                SharedPref.writeString("ipHost", novoIp);
            }
        }
    }

    private void deleteHost(final int id, final AlertDialog d) {
        new MaterialDialog.Builder(HostActivity.this)
                .title("Aviso")
                .content("Deseja realmente excluir o Host " + hosts.get(id).getNomeHost().substring(0, 1).toUpperCase()
                        + hosts.get(id).getNomeHost().substring(1).toLowerCase() + "?")
                .positiveText("Sim")
                .negativeText("Não")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delete(id, d);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        d.dismiss();
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public void delete(int id, AlertDialog d){
        d.dismiss();
        Log.d("Elemento ON DELETE: " + hosts.get(id).getNomeHost(), String.valueOf(hosts.get(id).isMarked()));
        hostDAO.deletarHost(hosts.get(id).getIdHost(), hosts.get(id).getID());
        if(hosts.get(id).isMarked()){
            SharedPref.writeString("nomeHost", "Produção");
            SharedPref.writeString("ipHost", getResources().getString(R.string.producao)
                    + getResources().getString(R.string.syncprice));
        }
        new SweetAlertDialog(HostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sucesso")
                .setContentText("Host excluído com sucesso!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                }).show();

    }

    public void openDialogHost(){
        Button adicionarHost;
        Button closeJanela;
        final EditText nomeHost;
        final EditText ipHost;

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(HostActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_host_customized, null);

        adicionarHost = mView.findViewById(R.id.btnAddHostEdit);
        closeJanela = mView.findViewById(R.id.btnCloseHostEdit);
        nomeHost = mView.findViewById(R.id.editNomeHost);
        ipHost = mView.findViewById(R.id.editIPHost);

        myDialog.setView(mView);
        final AlertDialog mDialog = myDialog.create();
        mDialog.show();

        adicionarHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nomeHost.getText().toString();
                String ip = ipHost.getText().toString();
                boolean containsNome = false;
                addHost(nome, ip, containsNome);
            }
        });

        closeJanela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void addHost(String nome, String ip, boolean containsNome){
        if(ip.equals("") || nome.equals("")){
            Toast.makeText(HostActivity.this, "Digite um Nome e um IP de host!", Toast.LENGTH_SHORT).show();
        } else if(nome.length() < 3){
            new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("O nome de Host deve ter no mínimo 3 caracteres!")
                    .setConfirmText("OK")
                    .show();
        } else if(nome.equalsIgnoreCase("Produção") || nome.equalsIgnoreCase("Desenvolvimento") ){
            new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Host " + nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase() +
                            " já existe e não pode ser adicionado novamente!")
                    .setConfirmText("OK")
                    .show();
        } else {
            for(int i = 0; i < hosts.size(); i++){
                if(hosts.get(i).getNomeHost().equalsIgnoreCase(nome)){
                    new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Aviso!")
                            .setContentText("Host " + nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase()
                                    + " já existe na lista")
                            .setConfirmText("OK")
                            .show();
                    containsNome = true;
                    break;
                }
            }
            if(!containsNome){
                hostDAO.adicionarHost(nome, ip);
                new SweetAlertDialog(HostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso")
                        .setContentText("Host adicionado com sucesso!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                                startActivity(getIntent());
                            }
                        }).show();
            }
        }
    }

    private void showAlertEnderecoPadrao(String nome, AlertDialog d){
        Dialog dialog = new SweetAlertDialog(HostActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Aviso!")
                .setContentText("Não é possível deletar o endereço padrão " + nome + "!")
                .setConfirmText("OK");
        d.dismiss();
        dialog.show();
    }

}
