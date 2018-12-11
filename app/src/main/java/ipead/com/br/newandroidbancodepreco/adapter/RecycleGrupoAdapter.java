package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.ProdutoActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;

/**
 * Created by daniel on 19/02/2018.
 */
public class RecycleGrupoAdapter extends RecyclerView.Adapter<RecycleGrupoAdapter.ViewHolder> {

    private List<HashMap<String, String>> listaGrupo;
    private Activity context;
    private int idInformante;
    private int tipoInformante;
    private int idPeriodoColeta;
    private String filepath;
    private static int selectedItem;

    public RecycleGrupoAdapter(Activity context, List<HashMap<String, String>> list, int idPeriodoColeta,
                               int idInformante, int tipoInformante, String filepath){
        this.context = context;
        this.listaGrupo = list;
        this.idPeriodoColeta = idPeriodoColeta;
        this.idInformante = idInformante;
        this.tipoInformante = tipoInformante;
        this.filepath = filepath;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView descricaoGrupo;
        public TextView produtosCadastrados;

        public ViewHolder(View itemView) {
            super(itemView);

            descricaoGrupo = itemView.findViewById(R.id.txtGrupoDescricao);
            produtosCadastrados = itemView.findViewById(R.id.txtProdutosCadastrados);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.model_grupo_recycleview, parent, false);

        RecycleGrupoAdapter.ViewHolder holder = new RecycleGrupoAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.descricaoGrupo.setText(listaGrupo.get(position).get("Nome"));
        holder.produtosCadastrados.setText(listaGrupo.get(position).get("Status"));

        if(SharedPref.readBoolean("restart", false)){
            holder.itemView.setSelected(selectedItem == position);
            holder.itemView.setBackgroundColor(selectedItem == position ? Color.CYAN : Color.TRANSPARENT);
        }

        SharedPref.writeBoolean("restart", false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPref.writeInt("positionGrupo", position);

                selectedItem = holder.getAdapterPosition();

                Intent intent = new Intent(context, ProdutoActivity.class);
                intent.putExtra("idInfo", idInformante).putExtra("idGrupo", listaGrupo.get(position).get("idGrupo"))
                        .putExtra("idPeriodo", idPeriodoColeta).putExtra("tipo", tipoInformante)
                        .putExtra("filepath", filepath);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaGrupo.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
