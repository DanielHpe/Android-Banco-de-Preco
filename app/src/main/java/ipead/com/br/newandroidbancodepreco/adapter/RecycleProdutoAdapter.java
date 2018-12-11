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
import ipead.com.br.newandroidbancodepreco.MarcaActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;

/**
 * Created by daniel on 19/02/2018.
 */

public class RecycleProdutoAdapter extends RecyclerView.Adapter<RecycleProdutoAdapter.ViewHolder>{

    private Activity context;
    private List<HashMap<String, String>> listProdutos;
    private int idInformante;
    private int idGrupo;
    private int idPeriodoColeta;
    private int tipoInformante;
    private String filepath;
    private static int selectedItem;

    public RecycleProdutoAdapter(Activity context, List<HashMap<String, String>> listProdutos,
                                 int idInformante, int idGrupo, int idPeriodoColeta, int tipoInformante, String filepath){
        this.context = context;
        this.listProdutos = listProdutos;
        this.idInformante = idInformante;
        this.idGrupo = idGrupo;
        this.idPeriodoColeta = idPeriodoColeta;
        this.tipoInformante = tipoInformante;
        this.filepath = filepath;
        SharedPref.init(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nomeProduto;
        public TextView statusProduto;

        public ViewHolder(View itemView) {
            super(itemView);

            nomeProduto = itemView.findViewById(R.id.txtDescricaoProduto);
            statusProduto = itemView.findViewById(R.id.txtStatusProduto);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.model_produto_recycleview, parent, false);

        RecycleProdutoAdapter.ViewHolder holder = new RecycleProdutoAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.nomeProduto.setText(listProdutos.get(position).get("Nome"));
        holder.statusProduto.setText(listProdutos.get(position).get("Status"));

        if(SharedPref.readBoolean("restart", false)){
            holder.itemView.setSelected(selectedItem == position);
            holder.itemView.setBackgroundColor(selectedItem == position ? Color.CYAN : Color.TRANSPARENT);
        }

        SharedPref.writeBoolean("restart", false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPref.writeInt("posProduto", position);

                selectedItem = holder.getAdapterPosition();
                
                Intent intent = new Intent(context, MarcaActivity.class);
                intent.putExtra("idInfo", idInformante).putExtra("idGrupo", idGrupo).putExtra("idProduto",
                        listProdutos.get(position).get("idProduto")).putExtra("idPeriodo", idPeriodoColeta).putExtra("tipo", tipoInformante)
                        .putExtra("filepath", filepath);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProdutos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
