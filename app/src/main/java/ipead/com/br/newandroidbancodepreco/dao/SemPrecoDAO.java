package ipead.com.br.newandroidbancodepreco.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.config.DirectoryBrowser;
import ipead.com.br.newandroidbancodepreco.database.MultiBD;
import ipead.com.br.newandroidbancodepreco.entity.Coleta;

/**
 * Created by daniel on 01/03/2018.
 */
public class SemPrecoDAO {

    private MultiBD db;
    private SQLiteDatabase dataBase;

    public SemPrecoDAO(Context context, String filePath){
        db = new MultiBD(filePath);
        DirectoryBrowser browser = new DirectoryBrowser(context);
        String mainPath = browser.dirBancoDados() + ConfiguracaoDAO.DBNAME;
        dataBase = context.openOrCreateDatabase(mainPath,  SQLiteDatabase.OPEN_READWRITE, null);
    }

    private int updateStatusInformante(int status, String idInformante) {
        int rows;
        ContentValues cv = new ContentValues();
        cv.put("status", status);

        rows = db.update("informante", cv, "idInformante = ? ",
                new String[] { String.valueOf(idInformante) });

        return rows;
    }

    /**
     * Verifica se o informante tem preco confirmado para todos os produtos e
     * Troca o status do informante para 2 (fechado)
     * @param idInformante
     * @return
     * @FIXME Alterar consultas para quando o informante for do tipo atacado/varejo
     */

//    public boolean fecharInformante(int idInformante, boolean isVazio) {
//
//        String sql = "SELECT COUNT(idProduto)"
//                + " FROM produtoInformante"
//                + " WHERE idInformante = " + idInformante + " AND statusColeta = 1";
//        boolean result = false;
//        int stats;
//
//        if(isVazio)
//            stats = 3;
//        else
//            stats = 2;
//
//        Cursor c = db.query(sql, null);
//
//        if (c.moveToFirst() && Integer.parseInt(c.getString(0)) == 0) {
//            updateStatusInformante(stats, String.valueOf(idInformante));
//            result = true;
//
//        }
//
//        c.close();
//        return result;
//    }

    /**
     * Coloca status 2 na tabela de produtoInformante
     * confirmando que aqueles produtos daquele informante estao sem preco
     * @param idInformante
     * @return
     */
    public boolean confirmaTodosSemPreco(int idInformante) {
        String sql = "UPDATE produtoInformante"
                + " set statusColeta = 2"
                + " WHERE idInformante = " + idInformante + " AND statusColeta = 1";

        boolean result;

        try {
            db.execSQL(sql);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return result;

    }

    public Cursor produtosSemPreco(int tipo, String idInformante){

        String sql;

        if(tipo == 3) {
            sql = "";
        } else {
            sql= "SELECT idProduto, idGrupo, p.descricao"
                    + " FROM produto p"
                    + " INNER JOIN marca m USING(idProduto)"
                    + " WHERE idInformante = " + idInformante
                    + " AND idProduto IN"
                    + " (SELECT idProduto"
                    + " FROM produtoInformante pi"
                    + " WHERE pi.idInformante ="+ idInformante +" AND statusColeta = 1)"
                    + " GROUP BY idProduto"
                    + " ORDER BY p.descricao";
        }

        Cursor c = db.query(sql, null);

        return c;
    }

    public List<Coleta> getColetasPrecoNull(int idInformante)
    {
        String sql =  "SELECT idMarcaProdutoInformante, tipo"
                + " FROM marca m"
                + " JOIN informante i USING (idInformante)"
                + " WHERE m.idInformante = " + idInformante
                + " AND idMarcaProdutoInformante NOT IN"
                + " (SELECT idMarcaProdutoInformante"
                + " FROM coleta c"
                + " JOIN marca mC USING(idMarcaProdutoInformante)"
                + " WHERE mC.idInformante = " + idInformante + " AND preco IS NOT NULL);";
        Cursor c = db.query(sql, null);

        List<Coleta> coleta = new ArrayList<>();
        Coleta col;

        if(c.getCount() > 0 && c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++) {
                col = new Coleta();
                col.setIdMarcaProdutoInformante(Integer.parseInt(c.getString(0)));
                col.setTipo(Integer.parseInt(c.getString(1)));

                coleta.add(col);

                c.moveToNext();
                //Log.i("COLETASCRIPT", "Preenchido coleta " + i);
            }
        }

        c.close();
        return coleta;
    }

    public void close() {
        if (db != null) {
            db.close();
        }

        if(dataBase != null) {
            dataBase.close();
        }
    }
}
