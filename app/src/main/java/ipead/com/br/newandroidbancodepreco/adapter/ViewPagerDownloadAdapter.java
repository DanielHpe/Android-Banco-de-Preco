package ipead.com.br.newandroidbancodepreco.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;

/**
 * Created by daniel on 26/03/2018.
 */
public class ViewPagerDownloadAdapter extends PagerAdapter {

    private List<Configuracao> list;
    private TextView rota;
    private TextView coletor;
    private TextView periodo;
    private TextView data;
    private TextView titulo;
    private TextView descricaoRota;
    private TextView tipoRota;
    private TextView nomeUsuarioColetor;
    private TextView idPeriodoColeta;
    private Button btnReceber;
    private View.OnClickListener onClickListener;

    public ViewPagerDownloadAdapter(Context c, List<Configuracao> configs, View.OnClickListener onClickListener) {
        list = configs;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Configuracao conf = list.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());

        View convertView = layoutInflater.inflate(R.layout.nova_rota_item, container, false);

        btnReceber = convertView.findViewById(R.id.btnReceber);

        btnReceber.setTag(conf.getIdRota());
        btnReceber.setOnClickListener(onClickListener);

        titulo = convertView.findViewById(R.id.lblTituloDownload);

        rota = convertView.findViewById(R.id.lblRota);
        descricaoRota = convertView.findViewById(R.id.lblNomeRota);
        tipoRota = convertView.findViewById(R.id.lblTipoRota);
        nomeUsuarioColetor = convertView.findViewById(R.id.lblNomeUsuarioColetor);
        coletor = convertView.findViewById(R.id.lblColetor);
        idPeriodoColeta = convertView.findViewById(R.id.lblIdPeriodoColeta);
        periodo = convertView.findViewById(R.id.lblPeriodo);
        data = convertView.findViewById(R.id.lblData);

        titulo.setText(conf.getRota());

        rota.setText("ID Rota: " + conf.getIdRota());
        descricaoRota.setText("Rota: " + conf.getRota());
        tipoRota.setText("Tipo Rota: " + conf.getTipo());
        coletor.setText("ID Usuário: " + conf.getIdUsuario());
        nomeUsuarioColetor.setText("Usuário: " + conf.getNome());
        idPeriodoColeta.setText("ID Período Coleta: " + conf.getIdPeriodoColeta());
        periodo.setText("Período: " + conf.getPeriodoColeta());

        Date d1, d2;
        String inicio = "", fim = "";

        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd").parse(conf.getInicio());
            d2 = new SimpleDateFormat("yyyy-MM-dd").parse(conf.getFim());

            inicio = new SimpleDateFormat("dd/MM/yy").format(d1);
            fim = new SimpleDateFormat("dd/MM/yy").format(d2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        data.setText("Data: " + inicio + " até " + fim);

        container.addView(convertView);

        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
