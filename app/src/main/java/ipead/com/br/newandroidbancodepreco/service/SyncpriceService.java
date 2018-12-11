package ipead.com.br.newandroidbancodepreco.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import ipead.com.br.newandroidbancodepreco.entity.Grupo;
import ipead.com.br.newandroidbancodepreco.entity.Marca;
import ipead.com.br.newandroidbancodepreco.entity.Ocorrencia;
import ipead.com.br.newandroidbancodepreco.entity.Produto;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Observable;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import ipead.com.br.newandroidbancodepreco.entity.Usuario;

/**
 * Created by daniel on 31/08/17.
 */

public class SyncpriceService {

    private String baseURL;

    private String protocolo;
    private String ip;

    public SyncpriceService(Context context) {
        SharedPref.init(context);
        protocolo = SharedPref.readString("protocolo", context.getResources().getString(R.string.https));
        ip = SharedPref.readString("ipHost", context.getResources().getString(R.string.producao) + context.getResources().getString(R.string.syncprice));
        if(!ip.substring(ip.length() -1 ).equals("/")){
            ip = ip + "/";
        }
        baseURL = protocolo + ip;
    }

    public SyncpriceAPI getAPI(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client.build())
                .build();

        return retrofit.create(SyncpriceAPI.class);
    }

    public interface SyncpriceAPI {

        @Headers(value = {"Authorization: Basic b25saW5lOjBubDFuMw==", "Connection: Close"})
        @GET ("conexao.php/new")
        Call<Conexao> isServerOnline();

        /* OBSERVABLES */

        @Headers("Connection: Close")
        @GET ("grupo.php")
        Observable<List<Grupo>> getGruposObservable(@Header("Authorization") String userCredentials);

        @Headers("Connection: Close")
        @GET ("produto.php/{idRota}/{idPeriodoColeta}/{idInformante}")
        Observable<List<Produto>> getProdutosObservable(@Header("Authorization") String userCredentials,
                                        @Path("idRota") int idRota, @Path("idPeriodoColeta") int idPeriodoColeta, @Path("idInformante") int idInformante);

        @Headers("Connection: Close")
        @GET ("marca.php/{idRota}/{idPeriodoColeta}/{idInformante}/{numLinha}")
        Observable<List<Marca>> getMarcasObservable (@Header("Authorization") String userCredentials,
                                   @Path("idRota") int idRota, @Path("idPeriodoColeta") int idPeriodoColeta, @Path("idInformante") int idInformante,
                                   @Path("numLinha") int numLinha);

        /* OBSERVABLES */

        @Headers(value = {"Authorization: Basic b25saW5lOjBubDFuMw==", "Connection: Close"})
        @GET ("usuario.php")
        Call<List<Usuario>> getUsuarios();

        @Headers("Connection: Close")
        @GET ("configuracao.php/{idUsuario}")
        Call<List<Configuracao>> getConfiguracoes(@Header("Authorization") String userCredentials,
                                                  @Path("idUsuario") int idUsuario);

        @Headers("Connection: Close")
        @GET ("configuracao.php/{idUsuario}/{idRota}")
        Call<List<Configuracao>> getRota(@Header("Authorization") String userCredentials,
                                   @Path("idUsuario") int idUsuario, @Path("idRota") int idRota);


        @Headers("Connection: Close")
        @GET ("marca.php/{idRota}/{idPeriodoColeta}/{idInformante}/{numLinha}")
        Call<List<Marca>> getMarca(@Header("Authorization") String userCredentials,
                                      @Path("idRota") int idRota, @Path("idPeriodoColeta") int idPeriodoColeta, @Path("idInformante") int idInformante,
                                      @Path("numLinha") int numLinha);

        @Headers("Connection: Close")
        @GET ("informante.php/{list}/{idPeriodoColeta}/{idUsuario}/2")
        Call<Ocorrencia> confirmarDownloadInformantes(@Header("Authorization") String userCredentials,
                                                      @Path("list") String list, @Path("idPeriodoColeta") int idPeriodoColeta, @Path("idUsuario") int idUsuario);

        @Headers(value = {"Accept: application/json", "Content-type: application/json", "Connection: Close"})
        @POST("coleta.php/{idInformante}")
        Call<Ocorrencia> enviarColeta(@Header("Authorization") String userCredentials,
                                      @Path("idInformante") int idInformante, @Body RequestBody coletaJson);

        @Headers(value = {"Accept: application/json", "Content-type: application/json", "Connection: Close"})
        @GET("informante.php/{idInformante}/{idPeriodoColeta}/{idUsuario}/2")
        Call<Ocorrencia> alterarStatusTransferido(@Header("Authorization") String userCredentials,
                                                  @Path("idInformante") int idInformante,
                                                  @Path("idPeriodoColeta") int idPeriodoColeta, @Path("idUsuario") int idUsuario);


        @Headers("Connection: Close")
        @POST("sempreco.php/{idInformante}/{idPeriodoColeta}")
        Call<Ocorrencia> finalizarInformanteSemPreco(@Header("Authorization") String userCredentials,
                                                     @Path("idInformante") int idInformante, @Path("idPeriodoColeta") int idPeriodoColeta);

    }

}
