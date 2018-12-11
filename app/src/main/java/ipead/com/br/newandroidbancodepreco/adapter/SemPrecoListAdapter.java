package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.MarcaActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.entity.Produto;

/**
 * Created by daniel on 01/03/2018.
 */
public class SemPrecoListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Produto> produtos;
    private Activity context;
    private TextView nomeProduto;
    private TextView tipoProduto;
    private CheckBox checkBoxSemPreco;
    int idPeriodoColeta;
    int tipoInformante;
    int idInformante;
    String filepath;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public SemPrecoListAdapter(Activity c, List<Produto> list, int idInformante, int idPeriodoColeta,
                               int tipoInformante, String filepath, CompoundButton.OnCheckedChangeListener checkedChangeListener){
        this.produtos = list;
        this.context = c;
        this.idInformante = idInformante;
        this.idPeriodoColeta = idPeriodoColeta;
        this.tipoInformante = tipoInformante;
        this.filepath = filepath;
        this.checkedChangeListener = checkedChangeListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View itemView = convertView;
        itemView = (itemView == null) ? inflater.inflate(R.layout.model_produtos_sem_preco, null) : itemView;

        nomeProduto = itemView.findViewById(R.id.txtNomeProdutoSemPreco);
        tipoProduto = itemView.findViewById(R.id.txtTipoProdutoSemPreco);
        checkBoxSemPreco = itemView.findViewById(R.id.checkboxItemsSemPreco);

        nomeProduto.setText(produtos.get(position).getDescricao());

        switch(produtos.get(position).getTipoProduto()){

            case 1:
                tipoProduto.setText("Tipo: Atacado");
                break;
            case 2:
                tipoProduto.setText("Tipo: Varejo");
                break;
            default:
                tipoProduto.setText("");
                break;
        }

        checkBoxSemPreco.setChecked(produtos.get(position).isChecked());
        checkBoxSemPreco.setTag(produtos.get(position));

        checkBoxSemPreco.setOnCheckedChangeListener(checkedChangeListener);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, MarcaActivity.class);

                int idGrupo = produtos.get(position).getIdGrupo();
                int idProduto =  produtos.get(position).getIdProduto();

                intent.putExtra("idInfo", idInformante)
                        .putExtra("idGrupo", idGrupo)
                        .putExtra("idProduto",String.valueOf(idProduto))
                        .putExtra("idPeriodo", idPeriodoColeta)
                        .putExtra("tipo", tipoInformante)
                        .putExtra("filepath", filepath);
                context.startActivity(intent);

                context.finish();

                return true;
            }
        });

        return itemView;
    }


}
