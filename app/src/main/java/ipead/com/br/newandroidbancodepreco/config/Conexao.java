package ipead.com.br.newandroidbancodepreco.config;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by daniel on 04/09/17.
 */
public class Conexao {

    private String statusConexao;

    /**
     * Método que verifica conexão com a internet
     * @param context
     * @return
     */
    public static boolean verificaConexao(Context context) {

        boolean conectado = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try{
            if (connectivityManager.getActiveNetworkInfo() != null
                    && connectivityManager.getActiveNetworkInfo().isAvailable()
                    && connectivityManager.getActiveNetworkInfo().isConnected()) {
                conectado = true;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return conectado;
    }

    public String getStatusConexao() {
        return statusConexao;
    }

    public void setStatusConexao(String statusConexao) {
        this.statusConexao = statusConexao;
    }
}
