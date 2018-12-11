package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

/**
 * Created by daniel
 */
public class RecycleExcluirRotaAdapter extends RecyclerView.Adapter<RecycleExcluirRotaAdapter.ViewHolderExcluirRota> {

    private List<Configuracao> listConfiguracao;
    private Activity context;
    private ConfiguracaoDAO configuracaoDAO;

    public RecycleExcluirRotaAdapter(Activity context, List<Configuracao> listConfiguracao){
        this.context = context;
        this.listConfiguracao = listConfiguracao;
    }

    public class ViewHolderExcluirRota extends RecyclerView.ViewHolder{

        TextView nomeRota;
        TextView idPeriodoColeta;
        TextView periodoColeta;
        TextView tipo;
        TextView informantesAbertos;

        public ViewHolderExcluirRota(View itemView) {
            super(itemView);

            nomeRota = itemView.findViewById(R.id.textNomeRota);
            idPeriodoColeta = itemView.findViewById(R.id.textIdPeriodoColeta);
            periodoColeta = itemView.findViewById(R.id.textPeriodo);
            tipo = itemView.findViewById(R.id.textTipoRota);
            informantesAbertos = itemView.findViewById(R.id.infoAbertos);
        }

    }


    @Override
    public ViewHolderExcluirRota onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.activity_rota, parent, false);

        RecycleExcluirRotaAdapter.ViewHolderExcluirRota holder =
                new RecycleExcluirRotaAdapter.ViewHolderExcluirRota(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderExcluirRota holder, final int position) {

        final Configuracao configSelect = listConfiguracao.get(position);

        holder.nomeRota.setText(configSelect.getIdRota() +  " - " + configSelect.getRota());

        holder.idPeriodoColeta.setText("" + configSelect.getIdPeriodoColeta());
        holder.periodoColeta.setText(configSelect.getPeriodoColeta());

        holder.tipo.setText(configSelect.getTipo());
        holder.informantesAbertos.setText("Informantes Abertos: " + configSelect.getInformantesAbertos());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new MaterialDialog.Builder(context)
                    .title("Aviso")
                    .content("Deseja realmente excluir essa rota?")
                    .positiveText("Sim")
                    .negativeText("Não")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            DirectoryBrowser browser = new DirectoryBrowser(context);

                            int idRota = listConfiguracao.get(position).getIdRota();
                            int idPeriodoColeta = listConfiguracao.get(position).getIdPeriodoColeta();
                            String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
                            configuracaoDAO = new ConfiguracaoDAO(context, path);
                            configuracaoDAO.excluirRota(idRota, idPeriodoColeta);

                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Sucesso")
                                    .setContentText("Rota Excluída com Sucesso!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            context.finish();
                                            context.startActivity(context.getIntent());
                                        }
                                    }).show();
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

    @Override
    public int getItemCount() {
        return listConfiguracao.size();
    }


}

