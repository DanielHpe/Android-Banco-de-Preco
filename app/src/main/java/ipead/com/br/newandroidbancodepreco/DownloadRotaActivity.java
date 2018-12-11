package ipead.com.br.newandroidbancodepreco;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.adapter.ViewPagerDownloadAdapter;
import ipead.com.br.newandroidbancodepreco.config.Conexao;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.config.SharedPref;
import ipead.com.br.newandroidbancodepreco.dao.ConfiguracaoDAO;
import ipead.com.br.newandroidbancodepreco.dao.InformanteDAO;
import ipead.com.br.newandroidbancodepreco.dao.UsuarioDAO;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DownloadRotaActivity extends AppCompatActivity implements ConfiguracaoDAO.ListConfiguracoesListener{

    private String login;
    private String senha;
    private UsuarioDAO userDAO;
    private ConfiguracaoDAO confDAO;
    private ViewPagerDownloadAdapter adapterListRota;
    private List<Configuracao> listConfig;
    private SweetAlertDialog progress;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private ViewPager viewPager;
    private DirectoryBrowser browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        browser = new DirectoryBrowser(getApplicationContext());
        Bundle b = getIntent().getExtras();

        login = b.getString("login");
        senha = b.getString("senha");

        viewPager = findViewById(R.id.viewPager);

        sliderDotspanel = findViewById(R.id.SliderDots);

        if(Conexao.verificaConexao(DownloadRotaActivity.this)){

            getRotasDisponíveis();

        } else {

            new SweetAlertDialog(DownloadRotaActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Sem conexão com a Internet!")
                    .setContentText("Conecte-se para buscar Rotas Disponíveis")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    public void addDotsIndicators() {
        dotscount = adapterListRota.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void getRotasDisponíveis() {

        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        progress = new SweetAlertDialog(DownloadRotaActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        progress.setTitleText("Buscando rotas...");
        progress.setCancelable(true);
        progress.show();

        addHandler();

        String userCredentials;
        userDAO = new UsuarioDAO(DownloadRotaActivity.this);
        confDAO = new ConfiguracaoDAO(DownloadRotaActivity.this, path);
        confDAO.setListConfiguracoesListener(this);

        userCredentials = userDAO.getUserCredentials(Integer.parseInt(userDAO.getIdUsuario(login)));
        confDAO.getListConfiguracao(userCredentials, Integer.parseInt(userDAO.getIdUsuario(login)));

    }

    public void addHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Dialog dialog = new SweetAlertDialog(DownloadRotaActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Aviso")
                        .setContentText("Falha na conexão com o servidor! Tente novamente!")
                        .setConfirmText("OK");

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        DownloadRotaActivity.this.finish();
                    }
                });

                if(progress.isShowing()){
                    progress.dismissWithAnimation();
                    dialog.show();
                }
            }
        }, 5000);

    }

    @Override
    public void listConfiguracoesReady(List<Configuracao> list) {
        listConfig = list;

        if(listConfig == null || listConfig.size() == 0) {

            Dialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Aviso!")
                    .setContentText("Sem rotas disponíveis para Download!")
                    .setConfirmText("OK");

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    DownloadRotaActivity.this.finish();
                }
            });

            dialog.show();

        } else {

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int idRota = Integer.parseInt(view.getTag().toString());
                    Log.i("TRANSFERENCIA ACTIVITY", "idRota: " + idRota);
                    final Intent intent = new Intent(DownloadRotaActivity.this, AsyncTaskActivity.class)
                            .putExtra("login", login).putExtra("senha", senha).putExtra("idRota", idRota);
                    buildNotification();
                    if(Conexao.verificaConexao(DownloadRotaActivity.this)){
                        new Thread(
                                new Runnable(){
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void run(){
                                        verificarFinalizadoAutomatico();
                                        startActivity(intent);
                                    }
                                }).start();
                    } else{
                        new SweetAlertDialog(DownloadRotaActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Erro")
                                .setContentText("Sem conexão com a internet!")
                                .show();
                    }
                }
            };

            adapterListRota = new ViewPagerDownloadAdapter(DownloadRotaActivity.this, listConfig, onClickListener);
            viewPager.setAdapter(adapterListRota);

            addDotsIndicators();

        }

        confDAO.close();
        progress.dismissWithAnimation();

    }

    public void buildNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "0")
                        .setSmallIcon(R.drawable.ic_assignment_returned_36dp)
                        .setContentTitle("Download Iniciado!")
                        .setAutoCancel(true)
                        .setContentText("Baixando Rota para o dispositivo!");

        Intent resultIntent = new Intent(this, MainMenuActivity.class)
                .putExtra("login", login).putExtra("senha", senha);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainMenuActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(0, mBuilder.build());

    }

    private boolean verificarFinalizadoAutomatico() {
        boolean result = false;

        String protocolo = SharedPref.readString("protocolo", getResources().getString(R.string.https));
        String ipHost = SharedPref.readString("ipHost", getResources().getString(R.string.producao)
                + getResources().getString(R.string.syncprice));
        String url = protocolo + ipHost + "informante.php/";
        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        try {

            int[] ids;
            InformanteDAO informanteDAO = new InformanteDAO(DownloadRotaActivity.this, null, path);

            String encodedJson =  informanteDAO.getEncodedJson();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor())
                    .build();

            RequestBody body = RequestBody.create(JSON, encodedJson);

            Request r = new Request.Builder().url(url).post(body).build();

            okhttp3.Response response = client.newCall(r).execute();
            String retorno = response.body().string();

            JSONArray jsonArray = new JSONArray(retorno);
            JSONObject objInformante;
            ids = new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); i++){
                objInformante = new JSONObject(jsonArray.getString(i));

                int idInformante = Integer.parseInt(objInformante.getString("idI"));
                ids[i] = idInformante;

            }

            result = moverFinalizadoAutomatico(ids);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean moverFinalizadoAutomatico(int[] ids) {

        File[] files = new File(browser.dirFinalizadoAutomatico()).listFiles();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, -10);

        for(int i = 0; i < files.length; i++){
            if (files[i].lastModified() < date.getTimeInMillis()) {
                files[i].delete();
            }
        }

        boolean result = false;
        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        if(ids != null) {
            InformanteDAO dao = new InformanteDAO(DownloadRotaActivity.this, null, path);
            dao.moverFinalizadoAutomatico(ids);
            dao.close();
            result = true;
        }

        return result;

    }

    public class BasicAuthInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            String userCredentials = userDAO.getUserCredentials(Integer.parseInt(userDAO.getIdUsuario(login)));
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", userCredentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    /*public class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            DownloadRotaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }

                }
            });

        }
    }*/
}
