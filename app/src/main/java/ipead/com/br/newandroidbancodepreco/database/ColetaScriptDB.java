package ipead.com.br.newandroidbancodepreco.database;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ipead.com.br.newandroidbancodepreco.entity.Coleta;

/**
 * Created by daniel
 *
 */
public class ColetaScriptDB {

    static final int VERSAO = 1;
    MultiBD db;
    String idInformante;

    private final static String[] TRIGGER = new String[] {
        "DROP TRIGGER IF EXISTS insertPrecoNull",
        "DROP TRIGGER IF EXISTS updatePrecoNull",
        "CREATE TRIGGER IF NOT EXISTS insertPrecoNull"
            + " AFTER INSERT ON coleta"
            + " WHEN NEW.quantidade = 0 OR NEW.preco = 0"
            + " BEGIN"
            + " UPDATE coleta"
            + " SET preco = NULL, quantidade = NULL"
            + " WHERE idMarcaProdutoInformante = new.idMarcaProdutoInformante;"
            + " END",
        "CREATE TRIGGER IF NOT EXISTS updatePrecoNull"
            + " AFTER UPDATE ON coleta"
            + " WHEN NEW.quantidade = 0 OR NEW.preco = 0"
            + " BEGIN"
            + " UPDATE coleta"
            + " SET preco = NULL, quantidade = NULL"
            + " WHERE idMarcaProdutoInformante = new.idMarcaProdutoInformante;"
            + " END"
    };

    public ColetaScriptDB(String filePath, String idInformante) {
        db = new MultiBD(filePath);
        this.idInformante = idInformante;
    }

    public List<Coleta> getColetasInformante() {
        String sql =  "SELECT idMarcaProdutoInformante, idPeriodoColeta, tipo, quantidade, preco, data"
                + " FROM marca m"
                + " INNER JOIN coleta c USING(idMarcaProdutoInformante)"
                + " WHERE preco IS NOT NULL AND idInformante = " + idInformante;
        Cursor c = db.query(sql, null);

        Log.i("DADOS", idInformante + " | registros: " + c.getCount() + " | " + c.getColumnCount());

        List<Coleta> coleta = new ArrayList<>();
        Coleta col;

//        if(c.getCount() > 0 && c.moveToFirst() ) {
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            col = new Coleta();
            col.setIdMarcaProdutoInformante(Integer.parseInt(c.getString(0)));
            col.setIdPeriodoColeta(Integer.parseInt(c.getString(1)));
            col.setTipo(Integer.parseInt(c.getString(2)));

            if(c.getString(4) != null && c.getString(3) != null) {
                col.setQuantidade(Integer.parseInt(c.getString(3)));
                col.setPreco(Double.parseDouble(c.getString(4)));
            } else {
                col.setQuantidade(0);
                col.setPreco(0.0);
            }

            col.setData(c.getString(5));
            coleta.add(col);
            //Log.i("COLETASCRIPT", "Preenchido coleta " + i);
            c.moveToNext();
        }

        c.close();
        return coleta;
    }

//    public void createTableColeta() {
//        //db.execSQL(createColeta);
//
//        for (int i = 0; i < TRIGGER.length; i++) {
//            try {
//                db.execSQL(TRIGGER[i]);
//                // Log.i("TRIGGER ", sql[i]);
//            } catch (Exception e) {
//                Log.e("ERRO TRIGGER", e.getMessage()
//                        + "  |  CAUSE: " + e.getCause());
//            }
//        }
//    }

    public void close(){
        if(db != null){
            db.close();
        }
    }


}
