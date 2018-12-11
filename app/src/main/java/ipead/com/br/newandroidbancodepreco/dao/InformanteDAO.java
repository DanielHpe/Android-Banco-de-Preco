package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.database.ColetaScriptDB;
import ipead.com.br.newandroidbancodepreco.database.ConfiguracaoScriptBD;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Coleta;
import ipead.com.br.newandroidbancodepreco.entity.Configuracao;
import ipead.com.br.newandroidbancodepreco.entity.Informante;

/**
 * Created by daniel
 *
 */
public class InformanteDAO{

    public static String createInformanteDB = "CREATE TABLE IF NOT EXISTS `informante` ("
                    + " `idInformante` INT NOT NULL ,"
                    + " `descricao` VARCHAR(100) NOT NULL ,"
                    + " `transporte` VARCHAR(255) NULL ,"
                    + " `observacao` TEXT NULL ,"
                    + " `orcamento` VARCHAR(45) NULL ,"
                    + " `endereco` VARCHAR(512) NULL ,"
                    + " `tipo` INT NOT NULL ,"
                    + " `latitude` FLOAT NULL ,"
                    + " `longitude` FLOAT NULL ,"
                    + " `telefone` VARCHAR(15) NULL ,"
                    + " `contato` VARCHAR(100) NULL ,"
                    + " `validade` DATE NOT NULL ,"
                    + " `status` INT NOT NULL ,"
                    + " PRIMARY KEY (`idInformante`) )";
    public static final int INFORMANTE_VAZIO = 7;
    public static final int INFORMANTE_PARCIAL = 8;
    public static final int INFORMANTE_TODOS_PRECOS = 9;
    private MultiBD extDb;
    private String filePath;
    private Context context;
    private SQLiteDatabase db;
    private ColetaScriptDB coleta;

    public InformanteDAO(Context context, String filePath, String mainpath) {
        this.filePath = filePath;
        this.context = context;
        db = context.openOrCreateDatabase(mainpath, SQLiteDatabase.OPEN_READWRITE, null);
        if(filePath != null)
            extDb = new MultiBD(filePath);
    }

    public List<Informante> getJson(String bodyType){

        Log.d("Resultado", bodyType);
        return parseJson(bodyType);

    }

    private List<Informante> parseJson(String json){

        List<Informante> informantes = new ArrayList<>();

        String lat;
        String lng;

        try {

            JSONArray jsonArray = new JSONArray(json);
            JSONObject objInformante;

            for(int i = 0; i < jsonArray.length(); i++){

                objInformante = new JSONObject(jsonArray.getString(i));
                Informante informante = new Informante();

                informante.setIdInformante(Integer.parseInt(objInformante.getString("idI")));
                informante.setDescricao(objInformante.getString("rS"));
                informante.setTransporte(objInformante.getString("t"));
                informante.setObservacao(objInformante.getString("ob"));
                informante.setOrcamento(objInformante.getString("o"));
                informante.setEndereco(objInformante.getString("end"));
                informante.setTipo(Integer.parseInt(objInformante.getString("tipo")));

                lat = objInformante.getString("lat");

                if(!objInformante.getString("lat").equals("")){
                    informante.setLatitude(Double.parseDouble(lat));
                }

                lng = objInformante.getString("lon");

                if(!objInformante.getString("lon").equals("")){
                    informante.setLongitude(Double.parseDouble(lng));
                }

                informante.setTelefone(objInformante.getString("tel"));
                informante.setContato(objInformante.getString("c"));
                informante.setValidade(objInformante.getString("val"));
                informante.setStatus(objInformante.getString("s"));

                informantes.add(informante);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return informantes;
    }

    /**
     * Retorna o status do informante baseado nos precos em seus produtos (tabela produtoInformante)
     * @return
     */
    public int statusAtualInformante(String idInformante) {

        String[] whereArgs = new String[]{ idInformante };

//        String sqlTeste = "SELECT idProduto AS id FROM produtoInformante WHERE idInformante = " + whereArgs[0];

        String sql = "SELECT COUNT(idProduto) AS total, (SELECT COUNT(idProduto)"
                + " FROM produtoInformante WHERE pi.idInformante = idInformante AND statusColeta = 1) AS vazio,"
                + " (SELECT COUNT(idProduto) FROM produtoInformante"
                + " WHERE pi.idInformante = idInformante AND statusColeta = 2) AS semPreco"
                + " FROM produtoInformante pi"
                + " WHERE idInformante = " + whereArgs[0];

        int status = -1;

        try {

            Cursor c = extDb.query(sql, null);
//            int columnCount = c.getColumnCount();
//            int rowCount = c.getCount();

            if(c.moveToFirst()) {
                int totalProduto =  Integer.parseInt(c.getString(0));
//                String coluna = c.getString(0);
                int vazio = Integer.parseInt(c.getString(1));
                int semPreco =  Integer.parseInt(c.getString(2));

                Log.i("VALORES", "Total: " + totalProduto +
                        " | semPreco: " + vazio + " | semPRecoConfirmado: " + semPreco);

                if((vazio + semPreco) == totalProduto)
                    status = INFORMANTE_VAZIO;
                else if(vazio == 0)
                    status = INFORMANTE_TODOS_PRECOS;
                else
                    status = INFORMANTE_PARCIAL;

            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return status;
    }


    /**
     * Inserindo informantes no Banco de Dados
     * Verifica se o número de informantes inseridos é igual ao número de informantes na lista
     */
    public boolean inserirListInformante(List<Informante> informantes, Configuracao conf)
    {
        int cadastrado = 0;
        db.beginTransaction();

        try{

            for(int i = 0; i < informantes.size(); i++) {
                inserirInformante(informantes.get(i), conf, db);
                cadastrado++;
                Log.i("Informante", "Informante cadastrado!" + informantes.get(i).getIdInformante());
            }

            db.setTransactionSuccessful();

        } catch(Exception e) {
            e.printStackTrace();
            db.endTransaction();
        }

        db.endTransaction();

        return (cadastrado == informantes.size());
    }

    public long inserirInformante(Informante informante) {
        ContentValues values = new ContentValues();

        values.put("idInformante", informante.getIdInformante());
        values.put("descricao", informante.getDescricao());
        values.put("transporte", informante.getTransporte());
        values.put("observacao", informante.getObservacao());
        values.put("orcamento", informante.getOrcamento());
        values.put("endereco", informante.getEndereco());
        values.put("tipo", informante.getTipo());
        values.put("latitude", informante.getLatitude());
        values.put("longitude", informante.getLongitude());
        values.put("telefone", informante.getTelefone());
        values.put("contato", informante.getContato());
        values.put("validade", informante.getValidade());
        values.put("status", informante.getStatus());

        Long result = extDb.insert("informante", values);
        return result;
    }

    public void inserirInformante(Informante informante, Configuracao config, SQLiteDatabase con)
    {
        ContentValues values = new ContentValues();

        values.put("idInformante", informante.getIdInformante());
        values.put("descricao", informante.getDescricao());
        values.put("transporte", informante.getTransporte());
        values.put("observacao", informante.getObservacao());
        values.put("orcamento", informante.getOrcamento());
        values.put("endereco", informante.getEndereco());
        values.put("tipo", informante.getTipo());
        values.put("latitude", informante.getLatitude());
        values.put("longitude", informante.getLongitude());
        values.put("telefone", informante.getTelefone());
        values.put("contato", informante.getContato());
        values.put("validade", informante.getValidade());
        values.put("status", informante.getStatus());
        values.put("idPeriodoColeta", config.getIdPeriodoColeta());
        values.put("idUsuario", config.getIdUsuario());
        values.put("periodoColeta", config.getPeriodoColeta());
        values.put("nome", config.getNome());
        values.put("login", config.getLogin());
        values.put("senha", config.getSenha());
        values.put("idRota", config.getIdRota());
        values.put("rota", config.getRota());
        values.put("tipoRota", config.getTipo());

        java.util.Date d1, d2;
        String inicio = "", fim = "";

        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd").parse(config.getInicio());
            d2 = new SimpleDateFormat("yyyy-MM-dd").parse(config.getFim());

            inicio = new SimpleDateFormat("dd/MM/yy").format(d1);
            fim = new SimpleDateFormat("dd/MM/yy").format(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        values.put("inicio", inicio);
        values.put("fim", fim);

        if(con.insert("informante", null, values) == -1){
            con.replace("informante", null, values);
        }

    }

    public void createTableInformante()
    {
        db.execSQL(ConfiguracaoScriptBD.createInformante);
    }

/*    public List<String> selectInformantes(){

        List<String> lista = new ArrayList<>();

        String[] colunas = new String[]{"idInformante", "descricao", "transporte"};
        Cursor cursor = db.query("informante", colunas, null, null, null, null, "idInformante");

        if (cursor.moveToFirst()){
            do{
                lista.add(cursor.getString(0) + " - " + cursor.getString(1)
                        + " - " + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        return lista;
    }*/

    /*public ArrayList<Informante> getListInformanteByRota(String idRota) {
        ArrayList<Informante> informantes = new ArrayList<Informante>();

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, tipoRota"
                + " FROM informante"
                + " WHERE status BETWEEN 1 AND 3 AND idRota = " + idRota + " AND idPeriodoColeta = " + getMaxIdPeriodoColeta(context)
                + "	ORDER BY descricao";
        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setTipoRota(c.getString(5));

                informantes.add(inf);

                c.moveToNext();
            }
        }

        c.close();

        return informantes;
    }*/

    public ArrayList<Informante> getListInformanteByRota(String idRota, String idPeriodoColeta) {
        ArrayList<Informante> informantes = new ArrayList<Informante>();

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, tipoRota"
                + " FROM informante"
                + " WHERE status BETWEEN 1 AND 3 AND idRota = " + idRota + " AND idPeriodoColeta = " + idPeriodoColeta
                + "	ORDER BY descricao";
        Log.i("SQL", sql);
        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setTipoRota(c.getString(5));

                informantes.add(inf);

                c.moveToNext();
            }
        }

        c.close();

        return informantes;
    }

    public ArrayList<Informante> getListInformanteByRotaTransferidos(String idRota, String idPeriodoColeta) {
        ArrayList<Informante> informantes = new ArrayList<Informante>();

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, tipoRota"
                + " FROM informante"
                + " WHERE status BETWEEN 1 AND 5 AND idRota = " + idRota + " AND idPeriodoColeta = " + idPeriodoColeta
                + "	ORDER BY descricao";
        Log.i("SQL", sql);
        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setTipoRota(c.getString(5));

                informantes.add(inf);

                c.moveToNext();
            }
        }

        c.close();

        return informantes;
    }

    public ArrayList<Informante> getLocationsByInformante(String idRota, String idPeriodoColeta, String idInformante){

        ArrayList<Informante> informantes = new ArrayList<>();

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, tipoRota, latitude, longitude, endereco"
                + " FROM informante"
                + " WHERE status BETWEEN 1 AND 5 AND idRota = " + idRota + " AND idPeriodoColeta = " + idPeriodoColeta
                + " AND idInformante = " + idInformante
                + "	ORDER BY descricao";
        Log.i("SQL", sql);
        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setTipoRota(c.getString(5));
                if(!c.getString(6).isEmpty()){
                    inf.setLatitude(Double.parseDouble(c.getString(6)));
                }
                if(!c.getString(7).isEmpty()){
                    inf.setLongitude(Double.parseDouble(c.getString(7)));
                }
                if(c.getString(8) != null){
                    inf.setEndereco(c.getString(8));
                }
                informantes.add(inf);

                c.moveToNext();
            }
        }

        c.close();

        return informantes;
    }

    public ArrayList<Informante> getAllLocations(String idRota, String idPeriodoColeta){

        ArrayList<Informante> informantes = new ArrayList<>();

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, tipoRota, latitude, longitude, endereco"
                + " FROM informante"
                + " WHERE status BETWEEN 1 AND 5 AND idRota = " + idRota + " AND idPeriodoColeta = " + idPeriodoColeta
                + "	ORDER BY descricao";

        Log.i("SQL", sql);

        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setTipoRota(c.getString(5));
                if(!c.getString(6).isEmpty()){
                    inf.setLatitude(Double.parseDouble(c.getString(6)));
                }
                if(!c.getString(7).isEmpty()){
                    inf.setLongitude(Double.parseDouble(c.getString(7)));
                }
                if(c.getString(8) != null){
                    inf.setEndereco(c.getString(8));
                }
                informantes.add(inf);

                c.moveToNext();
            }
        }

        c.close();

        return informantes;
    }

    public int selectIdInformante(){

        String query = "SELECT idInformante FROM informante";

        int cont = 0;

        Cursor result = db.rawQuery( query, null );

        if(result.moveToFirst())
        {
            do{
                cont++;
            } while (result.moveToNext());
        }

        return cont;

    }

    public int getTipoInformante(String idInformante) {

        int tipoInformante = 0;
        String[] whereArgs = new String[]{ idInformante };
        String sql = "SELECT tipo FROM informante WHERE idInformante = ?";

        Cursor c = db.rawQuery(sql, whereArgs);

        if (c.moveToFirst() && c.getCount() > 0) {
            tipoInformante = c.getInt(0);
        }

        c.close();

        return tipoInformante;
    }

    public boolean fecharInformante(int idInformante, boolean isVazio) {

        boolean result;
        int stats;

        if(isVazio)
            stats = 3;
        else
            stats = 2;

        try {
            updateStatusInformante(stats, String.valueOf(idInformante));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return result;

    }

    public String getEncodedJson() {

        String encodedJson = "";

        String sql = "SELECT GROUP_CONCAT(infPeriodoColeta)"
                + " FROM (SELECT '(' || idInformante || ',' || idPeriodoColeta || ')' AS infPeriodoColeta"
                + " FROM informante WHERE status < 4) t";

        Cursor c = db.rawQuery(sql, null);

        if(c.getCount() > 0 && c.moveToFirst()) {

            try {
                String listInfPeriodoColeta = c.getString(0);
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object().key("listInformantePeriodoColeta").value(listInfPeriodoColeta).endObject();

                encodedJson = jsonStringer.toString();

            } catch(JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        c.close();
        db.close();
        return encodedJson;
    }

    /*public static int getMaxIdPeriodoColeta(Context context){
        DirectoryBrowser browser = new DirectoryBrowser(context);
        String mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        SQLiteDatabase con = context.openOrCreateDatabase(mainPath, SQLiteDatabase.OPEN_READWRITE, null);
        int result = 0;

        String sql = "SELECT COALESCE(MAX(idPeriodoColeta), 0)"
                + " FROM informante";

        Cursor c = con.rawQuery(sql,null);

        if (c.moveToFirst() && c.getCount() > 0) {
            result = c.getInt(0);
        }

        c.close();
        con.close();

        return result;
    }*/

    public Cursor getInformacoesInformante(String idInformante) {
        String whereArgs = idInformante;

        String sql = "SELECT descricao, endereco, contato, telefone, transporte, orcamento, observacao, tipo"
                + " FROM informante"
                + " WHERE idInformante = " + whereArgs;

        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public int selectIdRotaByIdInformante(String idInformante){

        String result = "";

        String sql = "SELECT idRota FROM informante WHERE idInformante = " + idInformante;

        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            result = c.getString(0);
            c.close();
        }

        return Integer.parseInt(result);
    }

    public int selectIdPeriodoColetaByIdInformante(String idInformante){

        String result = "";

        String sql = "SELECT idPeriodoColeta FROM informante WHERE idInformante = " + idInformante;

        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            result = c.getString(0);
            c.close();
        }

        return Integer.parseInt(result);
    }



    /**
     * Retorna um Strign com o JSON das coletas relacionadas a um informante específico
     * @param idInformante
     * @param loginUsuario
     * @return
     */
    public String coletaInformanteToJson(Context context, String idInformante, String loginUsuario) {
        coleta = new ColetaScriptDB(filePath, idInformante);
        List<Coleta> listColeta = coleta.getColetasInformante();
        UsuarioDAO usr = new UsuarioDAO(context);
        Log.i("LOGIN", "LOGIN: " + loginUsuario);
        Log.i("IDUSUARIO", "id: " + usr.getIdUsuario(loginUsuario));
        String result;

        try{

            JSONStringer jsonStringer = new JSONStringer().array();

            for(Coleta coleta : listColeta) {
				/*Log.i("Coleta: ", "idMarcaProdutoInformante: " + coleta.getIdMarcaProdutoInformante());
				Log.i("Coleta: ", "idPeriodoCOleta: " + coleta.getIdPeriodoColeta());
				Log.i("Coleta: ", "tipo: " + coleta.getTipo());
				Log.i("Coleta: ", "quantidade: " + coleta.getQuantidade());
				Log.i("Coleta: ", "preco: " + coleta.getPreco());
				Log.i("Coleta: ", "data: " + coleta.getData());
				Log.i("Coleta: ", "idUSuario: " + usr.getIdUsuario((loginUsuario)));
				Log.i("Coleta: ", "validade: " + getValidadeInformante(idInformante));*/

                jsonStringer.object().key("idMpi").value(coleta.getIdMarcaProdutoInformante())
                        .key("idPc").value(coleta.getIdPeriodoColeta())
                        .key("t").value(coleta.getTipo())
                        .key("q").value(coleta.getQuantidade())
                        .key("p").value(coleta.getPreco())
                        .key("d").value(coleta.getData())
                        .key("idU").value(usr.getIdUsuario(loginUsuario))
                        .key("v").value(getValidadeInformante(idInformante)).endObject();
            }
            jsonStringer.endArray();
            Log.i("JSONSTRING", jsonStringer.toString());

            result = jsonStringer.toString();

        } catch(Exception e) {
            e.printStackTrace();
            result = null;
        }

        coleta.close();
        usr.close();

        return result;
    }

    /*public String precoNullInformanteToJson(Context context, int idInformante)
    {
        SemPrecoDAO dao = new SemPrecoDAO(context, filePath);
        List<Coleta> listColeta = dao.getColetasPrecoNull(idInformante);
        String result = null;

        if (listColeta.size() > 0) {

            try {
                JSONStringer jsonStringer = new JSONStringer().array();

                for (Coleta coleta : listColeta) {
//					 * Log.i("Coleta: ", "idMarcaProdutoInformante: " +
//					 * coleta.getIdMarcaProdutoInformante()); Log.i("Coleta: ",
//					 * "tipo: " + coleta.getTipo());

                    jsonStringer.object().key("idMpi")
                            .value(coleta.getIdMarcaProdutoInformante())
                            .key("t").value(coleta.getTipo()).endObject();
                }
                jsonStringer.endArray();
                Log.i("JSONSTRING", jsonStringer.toString());

                result = jsonStringer.toString();

            } catch (Exception e) {
                e.printStackTrace();
                result = "";
            }
        }

        dao.close();

        return result;
    }*/

    private String getValidadeInformante(String idInformante) {
        String result = "";
        String sql = "SELECT validade"
                + " FROM informante"
                + " WHERE idInformante = " + idInformante;
        Cursor c = extDb.query(sql, null);

        if(c.moveToFirst() && c.getCount() > 0) {
            //Log.i("GETVALIDADE", "Cursor -->" + c.getString(0));
            result = c.getString(0);
            c.close();
        }

        return result;
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Calendar getValidade(Context context, int idInformante) {
        DirectoryBrowser browser = new DirectoryBrowser(context);
        String mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        SQLiteDatabase con = context.openOrCreateDatabase(mainPath, SQLiteDatabase.OPEN_READWRITE, null);
        Calendar result = null;

        String sql = "SELECT validade"
                + " FROM informante"
                + "	WHERE idInformante = " + idInformante;

        Cursor c = con.rawQuery(sql,null);

        if (c.moveToFirst() && c.getCount() > 0) {
            Date validade = null;
            try {
                if(!"".equals(c.getString(0)))
                    validade = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(c.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(validade != null) {
                result = Calendar.getInstance();
                result.setTime(validade);
            }
        }

        c.close();
        con.close();

        return result;
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean setValidade(String idInformante, Calendar cal) {
        try {
            String data = "";
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
            String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            Log.i("INT", "year: " + year + " Month: " + month + " Day: " + day);

            if(day.length() > 1)
                data += day + "/";
            else
                data += 0 + day + "/";

            if(month.length() > 1)
                data += month + "/";
            else
                data += 0 + month + "/";

            data += String.valueOf(year);
            Log.i("SETVAL", data);
            ContentValues cv = new ContentValues();
            cv.put("validade", data);
            extDb.update("informante", cv, "idInformante = " + idInformante, null);
            return true;
        } catch(Exception e) {
            Log.e("UPDATE VALIDADE", e.getMessage() + "  Cause: " + e.getCause());
            return false;
        }
    }

    public void moverFinalizadoAutomatico(int[] idInformante) {

        DirectoryBrowser browser = new DirectoryBrowser(context);
        String path = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;

        try {
            if(idInformante != null && idInformante.length > 0) {
                String idsInformantes = "";

                for(int i = 0; i < idInformante.length; i++) {
                    if(i == (idInformante.length-1))
                        idsInformantes += idInformante[i];
                    else
                        idsInformantes += idInformante[i] + ",";
                }

                String sql = "SELECT idInformante"
                        + " FROM informante"
                        + " WHERE idInformante IN (" + idsInformantes + ")";

                Cursor c = db.rawQuery(sql, null);


                if(c.getCount() > 0 && c.moveToFirst())
                {
                    ConfiguracaoDAO dao = new ConfiguracaoDAO(context, path);

                    for(int i = 0; i < c.getCount(); i++) {
                        int id = c.getInt(0);
                        String idPeriodoColeta = dao.getIdPeriodoColeta(id);
                        String idRota = dao.getIdRota(id);
                        String filepath = browser.dir() + MultiBD.BD_INFORMANTE + idPeriodoColeta
                                + "-" + idRota + "-" + id;

                        ColetaDAO coleta = new ColetaDAO(filepath);
                        browser.criarPasta(browser.dirFinalizadoAutomatico());
                        boolean result = coleta.hasPreco();

                        File file = new File(filepath);

                        if(result) {
                            file.renameTo(new File(browser.dirFinalizadoAutomatico() +
                                    MultiBD.BD_INFORMANTE + idPeriodoColeta + "-" + idRota + "-" + id));
                        } else {
                            file.delete();
                        }

                        db.execSQL("DELETE FROM informante WHERE idInformante = " + id);

                        c.moveToNext();
                    }

                    c.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createTableInformanteDB()
    {
        extDb.execSQL(createInformanteDB);
    }

    /*public boolean setStatusInformanteErro(String idInformante){
        boolean result = false;
        try{

            updateStatusInformante(6, idInformante);
            result = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        return result;
    }*/

    public List<Informante> reabrirInformante() {

        String sql = "SELECT idInformante, descricao, status, tipo, periodoColeta, idPeriodoColeta, idUsuario"
                + " FROM informante"
                + " WHERE status BETWEEN 2 AND 5"
                + "	ORDER BY descricao";

        Cursor c = db.rawQuery(sql, null);

        List<Informante> informantes = new ArrayList<>();

        if (c.getCount() > 0 && c.moveToFirst()) {

            do {
                Informante inf = new Informante();
                inf.setIdInformante(Integer.parseInt(c.getString(0)));
                inf.setDescricao(c.getString(1));
                inf.setStatus(c.getString(2));
                inf.setTipo(Integer.parseInt(c.getString(3)));
                inf.setPeriodicidade(c.getString(4));
                inf.setIdPeriodoColeta(Integer.parseInt(c.getString(5)));
                inf.setIdUsuario(Integer.parseInt(c.getString(6)));
                informantes.add(inf);

            } while (c.moveToNext());
        }

        return informantes;
    }

    public int numberInformanteTransferido(String idRota, String idPeriodoColeta){

        String sql = "SELECT COUNT(idInformante)" +
                " FROM informante WHERE status BETWEEN 4 and 5 AND idRota = " + idRota
                + " AND idPeriodoColeta = " + idPeriodoColeta;

        Cursor c = db.rawQuery(sql, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
        }

        return c.getInt(0);

    }


    /**
     * Troca o status do informante para 4 (Transferido) ou para 5 (transferido vazio)
     * @param idInformante
     * @return
     */
    public boolean setStatusInformanteTransferido(String idInformante, boolean isVazio) {

        boolean result = false;
        int stats;

        if(isVazio)
            stats = 5;
        else
            stats = 4;

        try {

            updateStatusInformante(stats, idInformante);
            result = true;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /*public boolean isFirstTransfered(Context context){
        DirectoryBrowser browser = new DirectoryBrowser(context);
        String mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        SQLiteDatabase con = context.openOrCreateDatabase(mainPath, SQLiteDatabase.OPEN_READWRITE, null);
        boolean result = false;

        String sql = "SELECT COUNT(idInformante)"
                + " FROM informante"
                + " WHERE status IN (4, 5)";

        Cursor c = con.rawQuery(sql, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            if(c.getInt(0) == 1)
                result = true;
        }

        c.close();
        con.close();

        return result;
    }*/

    public int updateStatusInformante(int status, String idInformante) {
        int rows;
        ContentValues cv = new ContentValues();
        cv.put("status", status);

        rows = db.update("informante", cv, "idInformante = ? ",
                new String[] { String.valueOf(idInformante) });

        return rows;
    }

    public int deleteInformante(String idInformante) {
        int rows;

        rows = db.delete("informante", "idInformante = ? ",
                new String[] { String.valueOf(idInformante) });

        return rows;
    }

    /*public void deleteAll() {
        this.db.execSQL("DELETE FROM informante");
        Toast.makeText(context, "Informantes Deletados", Toast.LENGTH_SHORT);
    }*/

    public void close() {
        if (db != null) {
            db.close();
        }
    }
}
