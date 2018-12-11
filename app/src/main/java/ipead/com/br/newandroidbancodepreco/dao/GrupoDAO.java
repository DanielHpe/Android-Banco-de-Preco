package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ipead.com.br.newandroidbancodepreco.database.ConfiguracaoScriptBD;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Grupo;

/**
 * Created by daniel
 *
 */
public class GrupoDAO extends ConfiguracaoScriptBD {

    private MultiBD db;
    private String TABLE = "grupo";
    private String COLUNM_GRUPOID = "idGrupo";
    private String COLUNM_DESCRICAO = "descricao";

    public GrupoDAO(String filePath){

        db = new MultiBD(filePath);
    }

    public long inserirGrupo(Grupo grupo)
    {
        ContentValues values = new ContentValues();

        values.put(COLUNM_GRUPOID, grupo.getIdGrupo());
        values.put(COLUNM_DESCRICAO, grupo.getDescricao());

        long result = db.insert(TABLE, values);

//        long result = db.insert("grupo", values);

        return result;

    }

    public boolean inserirListGrupo(List<Grupo> grupos)
    {
        int cadastrado = 0;

        db.beginTransaction();

        try {

            for (int i = 0; i < grupos.size(); i++) {
                inserirGrupo(grupos.get(i));
                cadastrado++;
                // Log.i("Grupo", "Grupo cadastrado!");
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            db.endTransaction();

        }

        db.endTransaction();

        if(cadastrado == grupos.size())
            return true;
        else
            return false;

    }
                         //Key  //Value
    public List<HashMap<String, String>> selectGrupo(String idInformante){

        String sqlTest = "SELECT idGrupo, descricao FROM grupo";

        String[] whereArgs = new String[]{ idInformante };

        String sql = "SELECT idGrupo, g.descricao, COUNT(DISTINCT idProduto) AS total,"
                + " (SELECT COUNT(DISTINCT idProduto)"
                + " FROM coleta c"
                + " INNER JOIN marca cM USING (idMarcaProdutoInformante)"
                + " INNER JOIN produto p USING (idProduto)"
                + " WHERE p.idGrupo = g.idGrupo"
                + " AND cM.idInformante = m.idInformante) AS coletado"
                + " FROM grupo g"
                + " INNER JOIN produto p USING (idGrupo)"
                + " INNER JOIN marca m USING (idProduto)"
                + " WHERE m.idInformante = ?"
                + " GROUP BY g.idGrupo"
                + " ORDER BY g.descricao";

        Cursor c = db.query(sql, whereArgs);

//        if (c.getCount() <= 0)
//            Log.d("Vazio", "Empty");

        List<HashMap<String, String>> list = new ArrayList<>();

        if(c == null){
            return null;
        }

        if (c.moveToFirst()) {
            do {

                HashMap<String, String> inf = new HashMap<>();
                inf.put("idGrupo", Integer.toString(c.getInt(0)));
                inf.put("Nome", c.getString(1));
                inf.put("Status", c.getString(3) + " de " + c.getString(2)
                        + " Produto(s) Cadastrado(s)");

                list.add(inf);

            } while (c.moveToNext());
        } else {


        }

        c.close();

        return list;
    }

    public int selectIdGrupo(){

        String query = new String("SELECT idGrupo FROM grupo");

        int cont = 0;

        Cursor result = db.database.rawQuery( query, null );

        if(result.moveToFirst())
        {
            do{
                cont++;
            } while (result.moveToNext());
        }

        return cont;

    }

    public void close(){
        if(db != null){
            db.close();
        }
    }
}

