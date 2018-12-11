package ipead.com.br.newandroidbancodepreco.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.database.ConfiguracaoScriptBD;
import ipead.com.br.newandroidbancodepreco.database.UsuarioScriptBD;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daniel
 *
 */
public class ConfiguracaoDAO {

    public static final String DBNAME = "configuracao";
    private ConfiguracaoDAO.ListConfiguracoesListener mListener;
    private ConfiguracaoDAO.ConfiguracoesListener mListenerConf;
    private SyncpriceService syncpriceService;
    protected SQLiteDatabase db;
    private Context context;
    public List<Configuracao> result;
    public List<Configuracao> retorno;

    public ConfiguracaoDAO(Context context, String filepath) {
        this.context = context;
        this.syncpriceService = new SyncpriceService(context);
        File dbFile = new File(filepath);

        if(dbFile.exists())
            db = SQLiteDatabase.openDatabase(filepath, null, SQLiteDatabase.OPEN_READWRITE);
        else
            db = SQLiteDatabase.openOrCreateDatabase(filepath, null);
    }

    /**
     * Listener para quando terminar de excutar busca dos dados no servidor
     */
    public interface ListConfiguracoesListener {
        void listConfiguracoesReady(List<Configuracao> list);
    }

    public void setListConfiguracoesListener(ConfiguracaoDAO.ListConfiguracoesListener listener) {
        this.mListener = listener;
    }

    public interface ConfiguracoesListener{
        void configuracoesReady(List<Configuracao> list);
    }

    public void setConfiguracaoListener(ConfiguracaoDAO.ConfiguracoesListener listenerConf){
        this.mListenerConf = listenerConf;
    }

    /**
     * Busca lista de usuários do servidor
     */
    public void getListConfiguracao(String userCredentials, int idUsuario) {
        syncpriceService
                .getAPI()
                .getConfiguracoes(userCredentials, idUsuario)
                .enqueue(new Callback<List<Configuracao>>() {
                    @Override
                    public void onResponse(Call<List<Configuracao>> call, Response<List<Configuracao>> response) {
                        Log.i("response", response.message());
                        result = response.body();

                        mListener.listConfiguracoesReady(result);

                    }

                    @Override
                    public void onFailure(Call<List<Configuracao>> call, Throwable t) {
                        try {
                            throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *
     * Busca configuracoes pelo ID do Usuário e pela Rota
     *
     * @param userCredentials
     * @param idUsuario
     * @param idRota
     */

    @SuppressLint("SimpleDateFormat")
    public void getConfiguracaoByRota(String userCredentials, int idUsuario, int idRota){
        syncpriceService
                .getAPI()
                .getRota(userCredentials, idUsuario, idRota)
                .enqueue(new Callback<List<Configuracao>>() {
                    @Override
                    public void onResponse(Call<List<Configuracao>> call, Response<List<Configuracao>> response) {
                        Log.d("resposta", response.message());
                        retorno = response.body();

                        mListenerConf.configuracoesReady(retorno);
                    }

                    @Override
                    public void onFailure(Call<List<Configuracao>> call, Throwable t) {
                        try {
                            throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

//    @SuppressLint("SimpleDateFormat")
//    public long inserirConfiguracao(Configuracao configuracao) {
//        ContentValues values = new ContentValues();
//
//        values.put("idPeriodoColeta", configuracao.getIdPeriodoColeta());
//        values.put("idUsuario", configuracao.getIdUsuario());
//        values.put("periodoColeta", configuracao.getPeriodoColeta());
//        values.put("nome", configuracao.getNome());
//        values.put("login", configuracao.getLogin());
//        values.put("senha", configuracao.getSenha());
//        values.put("idRota", configuracao.getIdRota());
//        values.put("rota", configuracao.getRota());
//        values.put("tipo", configuracao.getTipo());
//
//        Date d1, d2;
//        String inicio = "", fim = "";
//
//        try {
//            d1 = new SimpleDateFormat("dd/MM/yyyy").parse(configuracao.getInicio());
//            d2 = new SimpleDateFormat("dd/MM/yyyy").parse(configuracao.getFim());
//
//            inicio = new SimpleDateFormat("yyyy-MM-dd").format(d1);
//            fim = new SimpleDateFormat("yyyy-MM-dd").format(d2);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        values.put("inicio", inicio);
//        values.put("fim", fim);
//
//        long result = db.insert("configuracao", null, values);
//        return result;
//    }

    public boolean checkIfExistsDataInTable() {

        boolean retorno;

        try{

            String sql = "SELECT * FROM informante";
            Cursor cursor = db.rawQuery(sql, null);

            if(cursor == null){
                return false;
            }

            retorno = !(cursor.getCount() <= 0);
            cursor.close();

        } catch (Exception e){
            return false;
        }

        return retorno;
    }

    public static boolean hasConfig(Context ctx) {
        boolean result = false;
        DirectoryBrowser browser = new DirectoryBrowser(ctx);

        SQLiteDatabase con = ctx.openOrCreateDatabase(browser.dirBancoDados()
                                + ConfiguracaoDAO.DBNAME, SQLiteDatabase.OPEN_READWRITE, null);

        String sql = "SELECT idPeriodoColeta, idRota"
                + " FROM informante"
                + " WHERE status < 4"
                + " GROUP BY idPeriodoColeta, idRota";

        Cursor c = con.rawQuery(sql, null);

        if(c.moveToFirst()) {
            if(c.getCount() > 0)
                result = true;
        }

        Log.i("Configs", "Total: " + c.getCount());
        c.close();
        con.close();

        return result;
    }

    public void verifyTables() {
        db.execSQL(ConfiguracaoScriptBD.createInformante);
    }

    public void excluirRota(int idRota, int idPeriodoColeta) {
        String sql = "DELETE FROM informante"
                + "	WHERE idRota = " + idRota + " AND idPeriodoColeta = " + idPeriodoColeta;
        db.execSQL(sql);

        DirectoryBrowser browser = new DirectoryBrowser(context);

        File[] files = new File(browser.dir()).listFiles();

        String fileNamePrefix = ConfiguracaoScriptBD.DBNAME_INFORMANTE + idPeriodoColeta + "-" + idRota;
        String moveTo = browser.dirError();

        if(files != null) {
            for(int i = 0; i < files.length; i++) {
                if(files[i].getName().contains(fileNamePrefix))
                    files[i].renameTo(new File(moveTo.concat(files[i].getName())));
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    public List<Configuracao> getListConfiguracaoLocal() {
        List<Configuracao> configs = new ArrayList<Configuracao>();

        String sql = "SELECT idPeriodoColeta, idUsuario, periodoColeta, nome, idRota, rota, inicio, fim, "
                + "		(SELECT COUNT(idInformante)"
                + "			FROM informante"
                + "			WHERE status = 1 AND idRota = i.idRota AND idPeriodoColeta = i.idPeriodoColeta) AS abertos, tipoRota"
                + " FROM informante i"
                + " GROUP BY idRota, idPeriodoColeta";
        Cursor c = db.rawQuery(sql, null);

        if(c == null){
            return null;
        }

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Configuracao conf = new Configuracao();
                conf.setIdPeriodoColeta(c.getInt(0));
                conf.setIdUsuario(c.getInt(1));
                conf.setPeriodoColeta(c.getString(2));
                conf.setNome(c.getString(3));
                conf.setIdRota(c.getInt(4));
                conf.setRota(c.getString(5));
                conf.setInformantesAbertos(c.getInt(8));
                conf.setTipo(c.getString(9));

                Date inicio, fim;
                try {
                    inicio = new SimpleDateFormat("yyyy-MM-dd").parse(c.getString(6));
                    fim = new SimpleDateFormat("yyyy-MM-dd").parse(c.getString(7));
                    String d1 = new SimpleDateFormat("dd/MM/yyyy").format(inicio);
                    String d2 = new SimpleDateFormat("dd/MM/yyyy").format(fim);
                    conf.setInicio(d1);
                    conf.setFim(d2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                configs.add(conf);

                c.moveToNext();

            }
        }

        c.close();

        return configs;
    }

    public String getLoginFromInformante() {
        String result = "";
        String sql = "SELECT login "
                + "FROM informante "
                + "LIMIT 1";

        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            result = c.getString(0);
        }

        c.close();

        return result;
    }

    public String getIdPeriodoColeta(int idInformante){
        String result = "";
        String sql = "SELECT idPeriodoColeta"
                + " FROM informante"
                + " WHERE idInformante = " + idInformante;
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            result = c.getString(0);
        }

        c.close();
        return result;
    }

    public String getIdRota(int idInformante) {
        String result = "";
        String sql = "SELECT idRota"
                + " FROM informante"
                + " WHERE idInformante = " + idInformante;
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            result = c.getString(0);
        }

        c.close();
        return result;
    }

    //Método que retorna o SQLITE
    public SQLiteDatabase getConfigDatabase() {
        return db;
    }

    public void syncConfiguracao(String login, String senha) {
        try {

            String sql = "UPDATE informante SET senha = '" + senha + "' WHERE login = '" + login + "'";
            db.execSQL(sql);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fecha conexão
     */
    public void close() {
        if (db != null) {
            db.close();
        }
    }

}
