package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.InformanteActivity;
import ipead.com.br.newandroidbancodepreco.MapsActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

/**
 * Created by daniel on 19/02/2018.
 */
public class RecycleRotaAdapter extends RecyclerView.Adapter<RecycleRotaAdapter.ViewHolder> {

    private List<Configuracao> listConfiguracao;
    private Activity context;
    private String login;
    private String senha;
    private String mainPath;
    private static int selectedItem;

    public RecycleRotaAdapter(Activity context, List<Configuracao> listConfiguracao,
                              String login, String senha, String mainPath){
        this.context = context;
        this.listConfiguracao = listConfiguracao;
        this.login = login;
        this.senha = senha;
        this.mainPath = mainPath;
        SharedPref.init(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nomeRota;
        private TextView idPeriodoColeta;
        private TextView periodoColeta;
        private TextView tipo;
        private TextView informantesAbertos;

        public ViewHolder(View itemView) {
            super(itemView);

            nomeRota = itemView.findViewById(R.id.textNomeRota);
            idPeriodoColeta = itemView.findViewById(R.id.textIdPeriodoColeta);
            periodoColeta = itemView.findViewById(R.id.textPeriodo);
            tipo = itemView.findViewById(R.id.textTipoRota);
            informantesAbertos = itemView.findViewById(R.id.infoAbertos);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.activity_rota, parent, false);

        RecycleRotaAdapter.ViewHolder holder = new RecycleRotaAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Configuracao configSelect = listConfiguracao.get(position);

        holder.nomeRota.setText(configSelect.getIdRota() +  " - " + configSelect.getRota());

        holder.idPeriodoColeta.setText("" + configSelect.getIdPeriodoColeta());
        holder.periodoColeta.setText(configSelect.getPeriodoColeta());

        holder.tipo.setText(configSelect.getTipo());
        holder.informantesAbertos.setText("Informantes Abertos: " + configSelect.getInformantesAbertos());

        if(SharedPref.readBoolean("restart", false)){
            holder.itemView.setSelected(selectedItem == position);
            holder.itemView.setBackgroundColor(selectedItem == position ? Color.CYAN : Color.TRANSPARENT);
        }

        SharedPref.writeBoolean("restart", false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPref.writeInt("posRota", position);

                selectedItem = holder.getAdapterPosition();

                Intent intent = new Intent(context, InformanteActivity.class);
                int idRota = configSelect.getIdRota();
                int idPeriodoColeta = configSelect.getIdPeriodoColeta();
                intent.putExtra("idRota", idRota)
                        .putExtra("idPeriodo", idPeriodoColeta)
                        .putExtra("login", login)
                        .putExtra("senha", senha)
                        .putExtra("mainPath", mainPath);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Button verMapa;

                AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
                View mView = context.getLayoutInflater().inflate(R.layout.alert_view_map, null);

                verMapa = mView.findViewById(R.id.btnViewMap);

                myDialog.setView(mView);
                final AlertDialog mapDialog = myDialog.create();
                mapDialog.show();

                verMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MapsActivity.class);
                        int idRota = configSelect.getIdRota();
                        int idPeriodoColeta = configSelect.getIdPeriodoColeta();
                        intent.putExtra("idRota", idRota)
                                .putExtra("idPeriodo", idPeriodoColeta)
                                .putExtra("login", login)
                                .putExtra("senha", senha);
                        context.startActivity(intent);
                        mapDialog.dismiss();
                    }
                });

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listConfiguracao.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
