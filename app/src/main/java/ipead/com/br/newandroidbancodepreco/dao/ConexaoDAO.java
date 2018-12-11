package ipead.com.br.newandroidbancodepreco.dao;

import android.content.Context;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daniel on 06/09/18.
 *
 */
public class ConexaoDAO {

    private final StatusConexaoListener mListener;
    private final SyncpriceService syncpriceService;

    public interface StatusConexaoListener {
        void statusConexaoReady(Conexao conexao);
    }

    public ConexaoDAO (StatusConexaoListener listener, Context context) {
        this.mListener = listener;
//        this.context = context;
        this.syncpriceService = new SyncpriceService(context);
    }

    public void getStatusConexao() {
        syncpriceService
            .getAPI()
            .isServerOnline()
            .enqueue(new Callback<Conexao>() {
                @Override
                public void onResponse(Call<Conexao> call, Response<Conexao> response) {
                    Conexao result = response.body();
                   // Log.d("ERROR", response.body().toString());

                    if(result != null)
                        mListener.statusConexaoReady(result);
                    else {
                        result = new Conexao();
                        //result.setStatusConexao(response.message());
                        result.setStatusConexao("Offline");
                        mListener.statusConexaoReady(result);
                    }

                }

                @Override
                public void onFailure(Call<Conexao> call, Throwable t) {
                    try {
                        throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}
