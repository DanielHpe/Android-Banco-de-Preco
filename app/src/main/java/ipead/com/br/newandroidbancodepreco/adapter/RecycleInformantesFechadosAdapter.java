package ipead.com.br.newandroidbancodepreco.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.io.File;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.entity.Informante;
import ipead.com.br.newandroidbancodepreco.entity.Ocorrencia;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daniel on 20/03/2018.
 */
public class RecycleInformantesFechadosAdapter extends RecyclerView.Adapter<RecycleInformantesFechadosAdapter.ViewHolder> {

    private List<Informante> listaInformante;
    private Activity context;
    private InformanteDAO informanteDAO;
    public String mainPath;
    private DirectoryBrowser browser;
    private int idUsuario;

    public RecycleInformantesFechadosAdapter(Activity context, List<Informante> list, int idUsuario){
        this.context = context;
        this.listaInformante = list;
        this.idUsuario = idUsuario;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtInformante;
        public TextView txtPeriodo;
        public TextView txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            txtInformante = itemView.findViewById(R.id.txtInformante);
            txtPeriodo = itemView.findViewById(R.id.txtPeriodo);
            txtStatus = itemView.findViewById(R.id.txtStatus);

        }
    }

    @Override
    public RecycleInformantesFechadosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.model_recycleview, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecycleInformantesFechadosAdapter.ViewHolder holder, final int position) {

        final Informante configSelect = listaInformante.get(position);

        holder.txtInformante.setText(configSelect.getDescricao());
        holder.txtPeriodo.setText(configSelect.getPeriodicidade());

        browser = new DirectoryBrowser(context);
        final int idInformante = configSelect.getIdInformante();
        mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        informanteDAO = new InformanteDAO(context, null, mainPath);

        switch(configSelect.getStatus().charAt(0)){
            case '1':
                holder.txtStatus.setText("Aberto");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(21, 160, 51));
                break;
            case '2':
                holder.txtStatus.setText("Fechado");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;
            case '3':
                holder.txtStatus.setText("Fechado (Vazio)");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;
            case '4':
                holder.txtStatus.setText("Transferido");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(241, 119, 4));
                break;
            case '5':
                holder.txtStatus.setText("Transferido (Vazio)");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(241, 119, 4));
                break;
            case '6':
                holder.txtStatus.setText("Erro");
                holder.txtStatus.setTextColor(android.graphics.Color.rgb(176, 21, 31));
                break;

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Button reabrirInformante;
                Button deletarInterno;

                final AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
                View mView = context.getLayoutInflater().inflate(R.layout.alert_opcoes_reabrir_excluir, null);

                reabrirInformante = mView.findViewById(R.id.btnReabrirInformante);
                deletarInterno = mView.findViewById(R.id.btnDeletarInterno);

                myDialog.setView(mView);
                final AlertDialog duploDialog = myDialog.create();
                duploDialog.show();

                reabrirInformante.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(configSelect.getStatus().equals("4")){

                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Alerta")
                                    .setContentText("Não é possível reabrir um informante já transferido!")
                                    .show();

                            duploDialog.dismiss();

                        } else if(configSelect.getStatus().equals("5")){

                            duploDialog.dismiss();

                            new MaterialDialog.Builder(context)
                                    .title("Aviso")
                                    .content("Deseja realmente reabrir este Informante?")
                                    .positiveText("Sim")
                                    .negativeText("Não")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            reabrirInformanteTransferidoVazio(configSelect, informanteDAO);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                        } else {

                            duploDialog.dismiss();

                            new MaterialDialog.Builder(context)
                                    .title("Aviso")
                                    .content("Deseja realmente reabrir este Informante?")
                                    .positiveText("Sim")
                                    .negativeText("Não")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            informanteDAO.updateStatusInformante(1, String.valueOf(idInformante));;

                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Sucesso")
                                                    .setContentText("Informante reaberto com sucesso!")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            context.finish();
                                                            context.startActivity(context.getIntent());
                                                        }
                                                    })
                                                    .show();

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
                });

                deletarInterno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        duploDialog.dismiss();

                        new MaterialDialog.Builder(context)
                                .title("Aviso")
                                .content("Deseja realmente deletar este Informante da tabela interna?")
                                .positiveText("Sim")
                                .negativeText("Não")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        informanteDAO.deleteInformante(String.valueOf(idInformante));

                                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Sucesso")
                                                .setContentText("Informante deletado com sucesso!")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        context.finish();
                                                        context.startActivity(context.getIntent());
                                                    }
                                                })
                                                .show();
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
                });

            }
        });

    }

    @SuppressLint("LongLogTag")
    private void reabrirInformanteTransferidoVazio(Informante configSelect, InformanteDAO informanteDAO) {


        int idInfo = configSelect.getIdInformante();
        int idPeriodoColeta = configSelect.getIdPeriodoColeta();
        int idUsuario = configSelect.getIdUsuario();

        Log.d("Informacoes IdInfo", String.valueOf(idInfo));

        /* -------------------------------------------------------------------------- */

        // Mover arquivo de Finalizado p/ Producao
        String moveFrom =  browser.dirF();
        String moveTo = browser.dir();

        File[] files = new File(browser.dirF()).listFiles();

        for(int i = 0; i < files.length; i++) {
            if (files[i].toString().contains(String.valueOf(idInfo))
                    && files[i].toString().contains(String.valueOf(idPeriodoColeta)) ) {
                int index = files[i].toString().lastIndexOf('/') + 1;
                String nomeArquivo = files[i].toString().substring(index, files[i].toString().length());
                browser.moverArquivo(moveFrom, nomeArquivo, moveTo);
            }
        }

        /* -------------------------------------------------------------------------- */

        // Atualizar status de Transferido vazio p/ Aberto
        informanteDAO.updateStatusInformante(1, String.valueOf(idInfo));

        /* -------------------------------------------------------------------------- */

        UsuarioDAO usuarioDAO = new UsuarioDAO(context);
        String userCredentials = usuarioDAO.getUserCredentials(idUsuario);

        SyncpriceService service = new SyncpriceService(context);
        service.getAPI()
                .alterarStatusTransferido(userCredentials, idInfo, idPeriodoColeta, idUsuario)
                .enqueue(new Callback<Ocorrencia>() {
                    @Override
                    public void onResponse(Call<Ocorrencia> call, Response<Ocorrencia> response) {
                        Ocorrencia ocorrencia = response.body();

                        Log.d("Ocor", ocorrencia.toString());

                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sucesso")
                                .setContentText("Informante reaberto com sucesso!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        context.finish();
                                        context.startActivity(context.getIntent());
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onFailure(Call<Ocorrencia> call, Throwable t) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return listaInformante.size();
    }

}
