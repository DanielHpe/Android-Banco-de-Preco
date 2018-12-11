package ipead.com.br.newandroidbancodepreco.config;

//import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import java.io.IOException;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.MainActivity;
import ipead.com.br.newandroidbancodepreco.MainMenuActivity;
import ipead.com.br.newandroidbancodepreco.R;
import ipead.com.br.newandroidbancodepreco.RotaActivity;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.GrupoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.dao.MarcaDAO;
import ipead.com.br.newandroidbancodepreco.dao.ProdutoDAO;
import ipead.com.br.newandroidbancodepreco.dao.ProdutoInformanteDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import ipead.com.br.newandroidbancodepreco.entity.Grupo;
import ipead.com.br.newandroidbancodepreco.entity.Informante;
import ipead.com.br.newandroidbancodepreco.entity.Marca;
import ipead.com.br.newandroidbancodepreco.entity.Ocorrencia;
import ipead.com.br.newandroidbancodepreco.entity.Produto;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class DownloadBDManager implements ConfiguracaoDAO.ConfiguracoesListener {

    private List<Configuracao> listConfigByRota;
    private Activity context;
    public String login;
    public String senha;
    public int idRota;
    private int idUser;
    private MultiBD bancoInfo;
    private ConfiguracaoDAO confDAO;
    private UsuarioDAO userDAO;
    private String userCredentials;
    private int idPeriodoColeta;
    private SyncpriceService service;
    private DirectoryBrowser browser;
    public SQLiteDatabase db;
    private String mainPath;
    private Configuracao conf;
    private int pos = 0;
    private int i;
    private int posicao;
    private boolean hasMore;
    private int contadorCalls = 0;
    private int contadorCallsErros = 0;
    private int RETRY = 10;
    private MaterialDialog mDialog;

    public DownloadBDManager(Activity context){
        this.context = context;
        this.service = new SyncpriceService(context);
        SharedPref.init(context);
    }


    //Boolean
    public boolean inicializaDB(String login, String senha, int idRota){

        boolean result = false;
        browser = new DirectoryBrowser(context);
        mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        try{

            userDAO = new UsuarioDAO(context);
            confDAO = new ConfiguracaoDAO(context, mainPath);
            confDAO.setConfiguracaoListener(this);

            this.login = login;
            this.idRota = idRota;
            this.senha = senha;
            this.idUser = Integer.parseInt(userDAO.getIdUsuario(login));

            userCredentials = userDAO.getUserCredentials(idUser);
            confDAO.getConfiguracaoByRota(userCredentials, idUser, idRota);

            result = true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void configuracoesReady(final List<Configuracao> list) {
        this.listConfigByRota = list;
        Log.d("Lista: ", listConfigByRota.toString());

        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        InformanteDAO inf = new InformanteDAO(context, null, path);
        inf.createTableInformante();
        Log.d("Trust: ", "create Informante");
        for (int i = 0; i < listConfigByRota.size(); i++) {
            idPeriodoColeta = listConfigByRota.get(i).getIdPeriodoColeta();
            conf = listConfigByRota.get(i);
        }

        String protocolo = SharedPref.readString("protocolo",  context.getResources().getString(R.string.https));
        String ip = SharedPref.readString("ipHost", context.getResources().getString(R.string.producao)
                + context.getResources().getString(R.string.syncprice));

        Log.d("Trust: ", ip);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor())
                .build();

        Request r = new Request.Builder()
                .url(protocolo + ip + "informante.php/" + idRota + "/" + idPeriodoColeta)
                .build();

        Log.d("Trust: ", "Request Builder" + protocolo + ip + "informante.php/" + idRota + "/" + idPeriodoColeta);

        client.newCall(r).enqueue(new okhttp3.Callback() {
//            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("Erro ao conectar a URL!", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String bodyType = responseBody.string();
                InformanteDAO infoDAO = new InformanteDAO(context, null, mainPath);
                List<Informante> listInfo = infoDAO.getJson(bodyType);
                inserirDadosDatabase(listInfo);
            }
        });


    }

    public class BasicAuthInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", userCredentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    public class Wrapper{

        private List<Grupo> listG;
        private List<Produto> listP;
        private List<Marca> listM;


        public Wrapper(List<Grupo> listGrupo, List<Produto> listProduto, List<Marca> listMarcas){

            this.listG = listGrupo;
            this.listP = listProduto;
            this.listM = listMarcas;
        }

        public void storageListsInDatabase(String filePath, int idInformante){

            inserirGruposDatabase(listG, filePath);
            inserirProdutosDatabase(listP, filePath);
            inserirMarcasDatabase(listM, filePath, idInformante);

        }
    }

    public void inserirDadosDatabase(final List<Informante> listInfo){

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialog = new MaterialDialog.Builder(context)
                        .title("Baixando informantes")
                        .content("aguarde...")
                        .cancelable(false)
                        .progress(false, listInfo.size(), true)
                        .show();
            }
        });

        try{

            InformanteDAO inf = new InformanteDAO(context, null, mainPath);
            inf.createTableInformante();
            if(inf.selectIdInformante() != listInfo.size()){
                inf.inserirListInformante(listInfo, conf);
            }
            inf.close();

            Observable<List<Grupo>> grupoObservable =
                    service
                    .getAPI()
                    .getGruposObservable(userCredentials)
                    .retry(RETRY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            for(i = 0; i < listInfo.size(); i++){

                final int idInformante = listInfo.get(i).getIdInformante();
                final String filePath = browser.dirTemp() + MultiBD.BD_INFORMANTE +
                        conf.getIdPeriodoColeta() + "-" + conf.getIdRota() + "-" + idInformante;

                inf = new InformanteDAO(context, filePath, mainPath);
                inf.createTableInformanteDB();
                inf.inserirInformante(listInfo.get(i));
                inf.close();

                bancoInfo = new MultiBD(filePath);
                bancoInfo.createTableGrupo();
                bancoInfo.createTableProduto();
                bancoInfo.createTableMarca();
                bancoInfo.createTableColeta();

                Observable<List<Produto>> produtoObservable =
                    service
                        .getAPI()
                        .getProdutosObservable(userCredentials, idRota, idPeriodoColeta, idInformante)
                        .retry(RETRY)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                Observable<List<Marca>> marcaObservable =
                    service
                        .getAPI()
                        .getMarcasObservable(userCredentials, idRota, idPeriodoColeta, idInformante, posicao)
                        .retry(RETRY)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

                final Observable<Wrapper> combined = Observable.zip(grupoObservable, produtoObservable, marcaObservable
                        , new Func3<List<Grupo>, List<Produto>, List<Marca>, Wrapper>() {
                            @Override
                            public Wrapper call(List<Grupo> grupos, List<Produto> produtos, List<Marca> marcas) {
                                return new Wrapper(grupos, produtos, marcas);
                            }
                        });

                combined.subscribe(new Subscriber<Wrapper>() {
                    @Override
                    public void onCompleted() {
                        contadorCalls++;
                        Log.d("Conexão bem-sucedida!", "SUCCESS");
                        Log.d("Contador de Chamadas: ", String.valueOf(contadorCalls));

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.setContent(contadorCalls + " de " + listInfo.size() + " informantes");
                                mDialog.incrementProgress(1);
                            }
                        });

                        if(contadorCalls == listInfo.size()){
                            buildNotificationSuccess();
                            SharedPref.writeString("login", login);
                            successDownload();
                            moverDirSucesso();
                            finalizarDownloadInformantes(listInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Erro ao conectar", e.getMessage());
                        contadorCallsErros++;
                        if(contadorCallsErros == 1){
                            errorDownload();
                            buildNotificationError();
                            unsubscribe();
                            moverDirErro();
                            context.finish();
                        }
                    }

                    @Override
                    public void onNext(Wrapper wrapper) {

                        wrapper.storageListsInDatabase(filePath, idInformante);

                        ProdutoInformanteDAO prodInfo = new ProdutoInformanteDAO(context, filePath);
                        prodInfo.createTableProdutoInformante();
                        prodInfo.close();

                    }
                });

            }

        } catch (Exception e){
            e.printStackTrace();
            Log.d("Erro ao inserir infos", e.getMessage());
        }

    }

    private void successDownload() {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sucesso!")
                        .setContentText("Download finalizado com sucesso!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                context.finish();
                                Intent i = new Intent(context, MainMenuActivity.class);
                                i.putExtra("login", login);
                                i.putExtra("senha", senha);
                                context.startActivity(i);
                            }
                        })
                        .setConfirmText("OK");

                mDialog.dismiss();
                dialog.show();
            }
        });



    }

    private void errorDownload(){

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erro!")
                        .setContentText("Erro ao baixar dados! Tente Novamente!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                context.finish();
                                Intent i = new Intent(context, MainMenuActivity.class);
                                i.putExtra("login", login);
                                i.putExtra("senha", senha);
                                context.startActivity(i);
                            }
                        })
                        .setConfirmText("OK");

                mDialog.dismiss();
                dialog.show();
            }
        });


    }

    private void buildNotificationSuccess() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "0")
                .setSmallIcon(R.drawable.ic_assignment_black_36dp)
                .setContentTitle("Download Finalizado!")
                .setAutoCancel(true)
                .setContentText("Rota transferida com Sucesso!");

        Intent resultIntent = new Intent(context, MainActivity.class)
                .putExtra("login", login).putExtra("senha", senha).putExtra("idUsuario", idUser);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(RotaActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

    }

    private void buildNotificationError(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "0")
                .setSmallIcon(R.drawable.ic_info_outline_white_24dp)
                .setContentTitle("Erro ao Baixar!")
                .setAutoCancel(true)
                .setContentText("Download Não Concluído. Tente Novamente!");

        Intent resultIntent = new Intent(context, MainActivity.class)
                .putExtra("login", login).putExtra("senha", senha).putExtra("idUsuario", idUser);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(RotaActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }

    //Inserção dos grupos
    public void inserirGruposDatabase(List<Grupo> listGrupo, String filepath){

        GrupoDAO grupo = new GrupoDAO(filepath);
        if(grupo.selectIdGrupo() != listGrupo.size()){
            grupo.inserirListGrupo(listGrupo);
        }
        grupo.close();

    }

    //Inserção dos produtos
    public void inserirProdutosDatabase(List<Produto> listProduto, String filepath){

        ProdutoDAO produto = new ProdutoDAO(filepath, context);
        if (produto.selectIdProduto() != listProduto.size()) {
            produto.inserirListProduto(listProduto);
        }
        produto.close();

    }

    //Inserção das Marcas
    public void inserirMarcasDatabase(final List<Marca> listMarca, String filepath, final int idInformante) {

        final MarcaDAO marcaDAO = new MarcaDAO(filepath);
        if(marcaDAO.selectIdMarca() != listMarca.size()){
            marcaDAO.inserirListMarca(listMarca);
        }
        MarcasAsync mAsync = new MarcasAsync(idInformante, filepath);
        mAsync.execute();

    }

    public class MarcasAsync extends AsyncTask<Void, Void, Void> {

        private int idInformante;
        private MarcaDAO marcaDAO;

        public MarcasAsync(int idInformante, String filepath) {
            this.idInformante = idInformante;
            marcaDAO = new MarcaDAO(filepath);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            pos = 0;
            hasMore = true;
            do{
                pos++;
                try {
                    List<Marca> listM = service.getAPI()
                            .getMarca(userCredentials, idRota, idPeriodoColeta, idInformante, pos).execute().body();
                    if(listM == null){
                        hasMore = false;
                    } else if(listM.size() == 0){
                        hasMore = false;
                    } else {
                        Log.i("config", "Inserindo marcas do informante " + idInformante + " com " + pos + " posicao!");
                        marcaDAO.inserirListMarca(listM);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } while (hasMore);

            return null;

        }

    }

//    Se download dos dados foi bem sucedido, move para sistemas/bancodepreco
//    Se não, move para sistemas/error
    public void moverDirSucesso(){

        String moveFrom = browser.dirTemp();
        String moveTo = browser.dir();

        browser.moveFile(moveFrom, moveTo);

    }

    public void moverDirErro(){

        String moveFrom =  browser.dirTemp();
        String moveTo = browser.dirError();
        browser.moveFile(moveFrom, moveTo);

    }

    public void finalizarDownloadInformantes(List<Informante> listaInf){

        try {

            String list = "";

            for (int i = 0; i < listaInf.size(); i++) {
                if (i != listaInf.size() - 1)
                    list += listaInf.get(i).getIdInformante() + ",";
                else
                    list += listaInf.get(i).getIdInformante();
            }

            if (listaInf.size() == 0)
                list = "0";

            service.getAPI().confirmarDownloadInformantes(userCredentials, list, idPeriodoColeta, idUser)
                    .enqueue(new Callback<Ocorrencia>() {
                        @Override
                        public void onResponse(Call<Ocorrencia> call, Response<Ocorrencia> response) {
//                            Ocorrencia ocorrencia = response.body();
                        }

                        @Override
                        public void onFailure(Call<Ocorrencia> call, Throwable t) {
                            try {
                                throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (Exception e){

            e.printStackTrace();
            Log.d("Erro ao confirmar infos", e.getMessage());

        }

    }


}
