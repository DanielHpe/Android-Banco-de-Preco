package ipead.com.br.newandroidbancodepreco.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Coleta;

/**
 * Created by daniel on 22/02/2018.
 *
 */
public class ColetaDAO {

    private MultiBD db;

    public ColetaDAO(String filePath){
        db = new MultiBD(filePath);
    }

    @SuppressLint("SimpleDateFormat")
    public boolean inputData(Coleta coleta, int chave) {
        
        boolean hasWorked = false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        try {

            ContentValues cv = new ContentValues();
            cv.put("idMarcaProdutoInformante", coleta.getIdMarcaProdutoInformante());
            cv.put("idPeriodoColeta", coleta.getIdPeriodoColeta());
            cv.put("tipo", coleta.getTipo());
            if(coleta.getQuantidade() > 0){
                cv.put("quantidade", coleta.getQuantidade());
            } else if(coleta.getQuantidade() == 0){
                cv.put("quantidade", (String) null);
            }
            if(coleta.getPreco() > 0.0){
                cv.put("preco", coleta.getPreco());
            } else if(coleta.getPreco() == 0){
                cv.put("preco", (String) null);
            }
            cv.put("data", String.valueOf(dateFormat.format(date)));
            String where = "idMarcaProdutoInformante = "
                    + coleta.getIdMarcaProdutoInformante()
                    + " AND idPeriodoColeta = " + coleta.getIdPeriodoColeta()
                    + " AND tipo = " + coleta.getTipo();

            if(chave == 1){

                if(coleta.getLocalColetaLat() != 0.0 && coleta.getLocalColetaLong() != 0.0) {
                    cv.put("localColetaLat", coleta.getLocalColetaLat());
                    cv.put("localColetaLong", coleta.getLocalColetaLong());
                }

                if(db.insert("coleta", cv) == -1){
                    hasWorked = false;
                } else {
                    hasWorked =  true;
                }

            } else if(chave == 2) {

                if(db.update("coleta", cv, where, null) > 0){
                    hasWorked = true;
                } else {
                    hasWorked = false;
                }
            }

        } catch (Exception e) {

            return false;
        }

        return hasWorked;

    }

    public List<Coleta> selectColetaDados(int tipo, int tipoEscolhido, String idInformante, String idProduto,
                                    String idMarca){

        List<Coleta> coletas = new ArrayList<>();

        String sql;
        String[] whereArgs;

        if(tipo == 3){

            whereArgs = new String[] {idInformante, idProduto, idMarca, String.valueOf(tipoEscolhido)};

            Log.i("WhereArgs: ", "Parametros: " + whereArgs[0] + " | "
                    + whereArgs[1] + " | " + whereArgs[2] + " | "
                    + whereArgs[3]);

            sql = "SELECT idMarcaProdutoInformante, idPeriodoColeta, tipo, quantidade, ROUND(preco,2), data"
                    + " FROM marca m "
                    + " INNER JOIN coleta c USING (idMarcaProdutoInformante)"
                    + " WHERE idInformante = " + whereArgs[0]
                    + " AND idProduto = " + whereArgs[1]
                    + " AND idMarcaProdutoInformante = " + whereArgs[2]
                    + " AND tipo = " + tipoEscolhido;

        } else {

            whereArgs = new String[] {idInformante, idProduto, idMarca, String.valueOf(tipo)};

            Log.i("WhereArgs: ", "Parametros: " + whereArgs[0] + " | "
                    + whereArgs[1] + " | " + whereArgs[2] + " | "
                    + whereArgs[3]);

            sql = "SELECT idMarcaProdutoInformante, idPeriodoColeta, tipo, quantidade, ROUND(preco,2), data"
                    + " FROM marca m "
                    + " INNER JOIN coleta c USING (idMarcaProdutoInformante)"
                    + " WHERE idInformante = ?"
                    + " AND idProduto = ?"
                    + " AND idMarcaProdutoInformante = ?"
                    + " AND tipo = ?";

        }

        Cursor c = this.db.query(sql, whereArgs);

        if(c.getCount() > 0 && c.moveToFirst())
        {
            for(int i = 0; i < c.getCount(); i++)
            {
                Coleta coleta = new Coleta();
                coleta.setIdMarcaProdutoInformante(Integer.parseInt(c.getString(0)));
                coleta.setIdPeriodoColeta(Integer.parseInt(c.getString(1)));
                coleta.setTipo(Integer.parseInt(c.getString(2)));
                if(c.getString(3) != null){
                    coleta.setQuantidade(Integer.parseInt(c.getString(3)));
                }
                Log.d("COLUNA PRECO", String.valueOf(c.getDouble(4)));
                if(c.getString(4) != null){
                    coleta.setPreco(c.getDouble(4));
                }
                coleta.setData(c.getString(5));

                coletas.add(coleta);

                c.moveToNext();
            }
        }

        return coletas;

    }

//    public Cursor hasVisited(String idMarca) {
//
//        String[] whereArgs = new String[] {idMarca};
//
//        String sql = "SELECT quantidade from coleta WHERE idMarcaProdutoInformante = " + whereArgs[0];
//
//        Cursor c = this.db.query(sql, null);
//
//        return c;
//    }

    public boolean hasPreco() {
        boolean result = false;
        String sql = "SELECT COUNT(idMarcaProdutoInformante)"
                + " FROM coleta"
                + " WHERE preco IS NOT NULL";
        Cursor c = db.query(sql, null);

        if(c.moveToFirst() && c.getCount() > 0)
        {
            if(c.getInt(0) > 0)
                result = true;

            c.close();
        }

        return result;
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }

}
