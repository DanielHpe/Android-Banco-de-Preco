package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.database.UsuarioScriptBD;
import ipead.com.br.newandroidbancodepreco.entity.Usuario;
import ipead.com.br.newandroidbancodepreco.service.SyncpriceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daniel on 08/09/17.
 */
public class UsuarioDAO {

    private UsuarioDAO.ListUsuariosListener mListener;
    private SyncpriceService syncpriceService;
    public DirectoryBrowser browser;
    protected SQLiteDatabase db;
    protected Context context;
    protected String filepath;

    /**
     * Listener para quando terminar de excutar busca dos dados no servidor
     */
    public interface ListUsuariosListener {
        void listUsuariosReady(List<Usuario> list);
    }

    public UsuarioDAO (Context context) {
        this.context = context;
        this.syncpriceService = new SyncpriceService(context);
        browser = new DirectoryBrowser(context);
        filepath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        File dbFile = new File(filepath);

        if(dbFile.exists())
            db = SQLiteDatabase.openDatabase(filepath, null, SQLiteDatabase.OPEN_READWRITE);
        else
            db = SQLiteDatabase.openOrCreateDatabase(filepath, null);

        createTableUsuario();
    }

    private void createTableUsuario() {
        db.execSQL(UsuarioScriptBD.createUsuario);
    }

    public void setListUsuariosListener(UsuarioDAO.ListUsuariosListener listener) {
        this.mListener = listener;
    }

    /**
     * Busca lista de usuários do servidor
     */
    public void getListUsuario() {
        syncpriceService
            .getAPI()
            .getUsuarios()
            .enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    List<Usuario> result = response.body();

                    mListener.listUsuariosReady(result);

                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    try {
                        throw  new InterruptedException("Erro na comunicação com o servidor! " + t.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    /**
     * Insere novo usuario
     * @param usuario
     * @return
     */
    public long inserirUsuario(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put(UsuarioScriptBD.COLUMN_ID, usuario.getIdUsuario());
        values.put(UsuarioScriptBD.COLUMN_IDGRUPO, usuario.getIdGrupoUsuario());
        values.put(UsuarioScriptBD.COLUMN_NOME, usuario.getNome());
        values.put(UsuarioScriptBD.COLUMN_LOGIN, usuario.getLogin());
        values.put(UsuarioScriptBD.COLUMN_SENHA, usuario.getSenha());

        return inserirUsuario(values);
    }

    private long inserirUsuario(ContentValues valores) {
        return db.insert(UsuarioScriptBD.TABLE, "", valores);
    }

    /**
     * Atualiza algum usuario
     * @param usuario
     * @return
     *
    public long atualizarUsuario(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put(UsuarioScriptBD.COLUMN_NOME, usuario.getNome());
        values.put(UsuarioScriptBD.COLUMN_LOGIN, usuario.getLogin());
        values.put(UsuarioScriptBD.COLUMN_SENHA, usuario.getSenha());
        String _id = String.valueOf(usuario.getIdUsuario());
        String where = UsuarioScriptBD.COLUMN_ID + "=?";
        String[] whereArgs = new String[] { _id };

        return atualizarUsuario(values, where, whereArgs);
    }

    private int atualizarUsuario(ContentValues valores, String where,
                                 String[] whereArgs) {
        int count = db.update(UsuarioScriptBD.TABLE, valores, where, whereArgs);
        return count;
    }*/

    /**
     * @author Daniel
     *   Deletar todos usuarios
     * @return
     */
    public int deleteAllFromUsuarios() {
        return deletarUsuario(null);
    }

    private int deletarUsuario(String where) {
        return db.delete(UsuarioScriptBD.TABLE, where, null);
    }

    public boolean validarLogin(String login, String senha){

        senha = this.convertPassMd5(senha);
        String query = new String("SELECT " + UsuarioScriptBD.COLUMN_LOGIN + ", " + UsuarioScriptBD.COLUMN_SENHA + "" +
                " FROM " + UsuarioScriptBD.TABLE + " WHERE " + UsuarioScriptBD.COLUMN_LOGIN + " = '" + login + "' AND " + UsuarioScriptBD.COLUMN_SENHA +
                " = '" + senha + "' ");

        Cursor cursor = this.db.rawQuery(query, null);

        String loginV = "";
        String senhaV = "";

        if(cursor.moveToFirst())
        {
            loginV = cursor.getString(cursor.getColumnIndex(UsuarioScriptBD.COLUMN_LOGIN));
            senhaV = cursor.getString(cursor.getColumnIndex(UsuarioScriptBD.COLUMN_SENHA));
        }

        cursor.close();

        if(loginV.equals(login) && senhaV.equals(senha)){
            return true;
        } else {
            return false;
        }
    }


    public boolean checkIfExistsDataInTable() {
        boolean retorno;

        try{
            String sql = "SELECT * FROM " + UsuarioScriptBD.TABLE;
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

    /**
     * TODO Reescrever metodo de retorno
     * @param login
     * @param senha
     * @return
     */
    public String isLogin(String login, String senha) {
        String result;

        try {
            senha = this.convertPassMd5(senha);
            String where = UsuarioScriptBD.COLUMN_LOGIN + " = '" + login + "' AND "
                    + UsuarioScriptBD.COLUMN_SENHA + " = '" + senha + "' ";
            String[] columns = new String[] {"login", "senha", "nome"};
            Cursor c;

            String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

            ConfiguracaoDAO dao = new ConfiguracaoDAO(context, path);
            dao.verifyTables();

            if(ConfiguracaoDAO.hasConfig(context)) {
                c = dao.getConfigDatabase().query("informante", columns, where, null, null,
                        null, null, null);
                if(c.getCount() > 0){
                    result = "true";
                } else{
                    c = dao.getConfigDatabase().rawQuery("SELECT nome FROM informante WHERE status < 4 LIMIT 1", null);
                    c.moveToFirst();
                    result = "false" + c.getString(0);
                }

            } else {
                c = db.query(true, UsuarioScriptBD.TABLE, columns, where, null, null,
                        null, null, null);
                if(c.getCount() > 0){
                    result = "true";
                } else{
                    result = "usr false";
                }
            }

            c.close();
        } catch(Exception e) {
            throw e;
        }

        return result;
    }


    /**
     * Metodo para converter senha para md5
     *
     * @param pwd
     * @return
     */

    public String convertPassMd5(String pwd) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pwd.getBytes(), 0, pwd.length());
            pwd = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pwd.length() < 32) {
                pwd = "0" + pwd;
            }
            password = pwd;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    /**
     * Retorna o id do usuário de acordo com o login
	 * @param login
	 * @return
     */
    public String getIdUsuario(String login) {

        String result = "";
        String sql = "SELECT idUsuario"
                + " FROM usuario"
                + " WHERE login = '" + login + "'";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            result = c.getString(0);

        Log.i("result", "result: " + result + "  count: " + c.getCount() + "  parametro: -->" + login);
        c.close();

        return result;
    }

    public String getNomeUsuario(String login) {

        String result = "";
        String sql = "SELECT nome"
                + " FROM usuario"
                + " WHERE login = '" + login + "'";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            result = c.getString(0);

        c.close();


        return result;
    }

    public boolean isGerenteOrAdmin(String login) {
        String result = "";
        String sql = "SELECT idGrupoUsuario" +
                " FROM usuario" +
                " WHERE login = '" + login + "'";

        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            result = c.getString(0);

        c.close();

        return !result.equals("5");
    }


    public String getIdGrupoUsuario(String login){

        String result = "";
        String sql = "SELECT idGrupoUsuario" +
                " FROM usuario" +
                " WHERE login = '" + login + "'";

        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            result = c.getString(0);

        Log.i("result", "result: " + result + "  count: " + c.getCount() + "  parametro: -->" + login);
        c.close();

        return result;
    }

    /**
     * Retorna o senha do usuário de acordo com o login
     * @param login
     * @return
     */
    public String getSenhaUsuario(String login) {

        String result = "";
        String sql = "SELECT senha"
                + " FROM usuario"
                + " WHERE login = '" + login + "'";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            result = c.getString(0);

        c.close();


        return result;
    }

    public String getUserCredentials(int idUsuario) {
        String userCredentials = "";

        String sql = "SELECT login || ':' || senha"
                + " FROM usuario"
                + " WHERE idUsuario = " + idUsuario;
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
            userCredentials = "Basic " + Base64.encodeToString(c.getString(0).getBytes(), Base64.NO_WRAP);

        c.close();

        return userCredentials;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }
}
