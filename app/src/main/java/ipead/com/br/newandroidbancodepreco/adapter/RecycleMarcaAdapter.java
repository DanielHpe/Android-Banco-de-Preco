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
import ipead.com.br.newandroidbancodepreco.ColetaActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;

/**
 * Created by daniel on 19/02/2018.
 */

public class RecycleMarcaAdapter extends RecyclerView.Adapter<RecycleMarcaAdapter.ViewHolder>{

    private Activity context;
    private List<HashMap<String, String>> listMarca;
    private int idInformante;
    private int idGrupo;
    private int idProduto;
    private int idPeriodoColeta;
    private int tipoInformante;
    private String filepath;

    private static int selectedItem;

    public RecycleMarcaAdapter(Activity context, List<HashMap<String, String>> lista,
                               int idInformante, int idGrupo, int idProduto, int idPeriodoColeta, int tipoInformante, String filepath){
        this.context = context;
        this.listMarca = lista;
        this.idInformante = idInformante;
        this.idGrupo = idGrupo;
        this.idProduto = idProduto;
        this.idPeriodoColeta = idPeriodoColeta;
        this.tipoInformante = tipoInformante;
        this.filepath = filepath;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView marca1;
        public TextView marca2;

        public ViewHolder(View itemView) {
            super(itemView);

            marca1 = itemView.findViewById(R.id.txtMarca1);
            marca2 = itemView.findViewById(R.id.txtMarca2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.model_marca_recycleview, parent, false);

        ViewHolder holder = new RecycleMarcaAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.marca1.setText(listMarca.get(position).get("Nome"));
        holder.marca2.setText(listMarca.get(position).get("Status"));

        if(SharedPref.readBoolean("restart", false)){
            holder.itemView.setSelected(selectedItem == position);
            holder.itemView.setBackgroundColor(selectedItem == position ? Color.CYAN : Color.TRANSPARENT);
        }

        SharedPref.writeBoolean("restart", false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPref.writeInt("posMarca", position);

                selectedItem = holder.getAdapterPosition();

                Intent intent = new Intent(context, ColetaActivity.class);
                intent.putExtra("idInfo", idInformante).putExtra("idGrupo", idGrupo).putExtra("idProduto",
                        idProduto).putExtra("idMarca", listMarca.get(position).get("idMarcaProdutoInformante"))
                        .putExtra("idPeriodo", idPeriodoColeta).putExtra("tipo", tipoInformante).
                        putExtra("descricaoMarca", listMarca.get(position).get("Nome")).putExtra("filepath", filepath)
                        .putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMarca.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
