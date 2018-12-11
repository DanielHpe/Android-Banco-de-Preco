package ipead.com.br.newandroidbancodepreco.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

/**
 * Created by daniel on 26/03/2018.
 */
public class ViewPagerAdapter extends PagerAdapter{

    private Activity context;
    private List<Configuracao> listConfigs;
    private TextView titulo;
    private TextView rota;
    private TextView tipoRota;
    private TextView rotaDescricao;
    private TextView coletor;
    private TextView nomeColetor;
    private TextView periodo;
    private TextView data;
    private TextView countInformante;

    public ViewPagerAdapter(Activity context, List<Configuracao> list){
        this.context = context;
        this.listConfigs = list;
    }

    @Override
    public int getCount() {
        return listConfigs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Configuracao conf = listConfigs.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

        View view = layoutInflater.inflate(R.layout.rota_item, container, false);

        titulo = view.findViewById(R.id.lblTituloRota);
        rota = view.findViewById(R.id.lblRota);
        rotaDescricao = view.findViewById(R.id.lblRotaDescricao);
        tipoRota = view.findViewById(R.id.lblTipoRota);
        coletor = view.findViewById(R.id.lblColetor);
        nomeColetor = view.findViewById(R.id.lblNomeColetor);
        periodo = view.findViewById(R.id.lblPeriodo);
        data = view.findViewById(R.id.lblData);
        countInformante = view.findViewById(R.id.lblInformantes);

        titulo.setText(conf.getRota());
        rota.setText("Id Rota: " + conf.getIdRota());
        rotaDescricao.setText("Rota: " + conf.getRota());
        tipoRota.setText("Tipo Rota: " + conf.getTipo());
        coletor.setText("Id Coleta: " + conf.getIdPeriodoColeta());
        nomeColetor.setText("Coletor: " + conf.getNome());
        periodo.setText("Período: " + conf.getPeriodoColeta());
        countInformante.setText("Informantes Abertos: " + conf.getInformantesAbertos());

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
